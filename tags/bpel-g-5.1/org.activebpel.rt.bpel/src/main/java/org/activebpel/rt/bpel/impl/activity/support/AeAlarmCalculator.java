//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeAlarmCalculator.java,v 1.5 2007/11/01 18:23:52 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support; 

import java.util.Date;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.activity.IAeTimedDef;
import org.activebpel.rt.bpel.def.activity.support.AeForDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.expr.AeExpressionRunnerContext;
import org.activebpel.rt.bpel.impl.expr.AeExpressionRunnerVariableResolver;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext;
import org.activebpel.rt.xml.schema.AeSchemaDuration;

/**
 * Schedules the alarm for an activity. Handles the exeuction of an expression 
 * to produce a duration or deadline. 
 */
public abstract class AeAlarmCalculator
{
   /**
    * Ctor accepts ref to parent and event id value
    */
   protected AeAlarmCalculator()
   {
   }
   
   /**
    * Calculates a deadline given a for/until/repeatEvery expression def
    * 
    * @param aBpelObject
    * @param aTimedDef
    * @param aEventId
    * @throws AeBusinessProcessException
    */
   public static Date calculateDeadline(AeAbstractBpelObject aBpelObject, IAeTimedDef aTimedDef, int aEventId) throws AeBusinessProcessException
   {
      if (aTimedDef.getForDef() != null)
      {
         return calculateDeadline(aBpelObject, aTimedDef.getForDef(), aEventId);
      }
      else if (aTimedDef.getUntilDef() != null)
      {
         return calculateDeadline(aBpelObject, aTimedDef.getUntilDef(), aEventId);
      }
      else
      {
         return calculateDeadline(aBpelObject, aTimedDef.getRepeatEveryDef(), aEventId);
      }
   }

   /**
    * Calculates a deadline given a for/until/repeatEvery expression def
    * @param aBpelObject
    * @param aExpressionDef
    * @param aEventId
    * @throws AeBusinessProcessException
    */
   private static Date calculateDeadline(AeAbstractBpelObject aBpelObject, IAeExpressionDef aExpressionDef, int aEventId) throws AeBusinessProcessException
   {
      StringBuffer alarmInfo = new StringBuffer();
      alarmInfo.append('\n');
      alarmInfo.append(aBpelObject.getClass().getName());
      alarmInfo.append(AeMessages.getString("AeAbstractBpelObject.0")); //$NON-NLS-1$
      alarmInfo.append(AeMessages.getString("AeAbstractBpelObject.1")).append((new Date()).toString()).append('\n'); //$NON-NLS-1$ 

      String expression = aExpressionDef.getExpression();
      Object value = executeExpression(aBpelObject, aExpressionDef);
      Date deadline = null;
      if (value instanceof AeSchemaDuration)
         deadline = ((AeSchemaDuration)value).toDeadline();
      else
         deadline = (Date) value;

      alarmInfo.append(AeMessages.getString("AeAbstractBpelObject.8")).append(deadline.toString()).append('\n'); //$NON-NLS-1$ 
      aBpelObject.getProcess().getEngine().fireEvaluationEvent(
            aBpelObject.getProcess().getProcessId(), expression, aEventId,
            aBpelObject.getLocationPath(), deadline.toString());

      AeException.info(alarmInfo.toString());
      return deadline;
   }
   
   /**
    * Executes an expression that returns a schema duration
    * @param aBpelObject
    * @param aExpressionDef
    * @throws AeBusinessProcessException
    */
   public static AeSchemaDuration calculateRepeatInterval(AeAbstractBpelObject aBpelObject, IAeExpressionDef aExpressionDef) throws AeBusinessProcessException
   {
      return (AeSchemaDuration) executeExpression(aBpelObject, aExpressionDef);
   }
   
   /**
    * Executes an XPath expression that we expect to return a deadline/duration. 
    * Any other return value from the evaluation will result in an exception 
    * being thrown since there is something terribly wrong.
    * 
    * @param aBpelObject
    * @param aExpressionDef
    * @return Object Date or AeSchemaDuration
    * @throws AeBusinessProcessException if not a duration return or other error
    */
   private static Object executeExpression(AeAbstractBpelObject aBpelObject, IAeExpressionDef aExpressionDef) throws AeBusinessProcessException
   {
      String expressionLanguage = getExpressionLanguage(aExpressionDef);
      try
      {
         IAeExpressionLanguageFactory factory = aBpelObject.getProcess().getExpressionLanguageFactory();
         IAeExpressionRunner runner = factory.createExpressionRunner(aExpressionDef.getBpelNamespace(), expressionLanguage);
         IAeExpressionRunnerContext context = createExpressionRunnerContext(expressionLanguage, aBpelObject);
         if (aExpressionDef instanceof AeForDef)
         {
            AeSchemaDuration duration = runner.executeDurationExpression(context, aExpressionDef.getExpression());
            return duration;
         }
         else
         {
            return runner.executeDeadlineExpression(context, aExpressionDef.getExpression());
         }
      }
      catch (AeBusinessProcessException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         // was previously checking for ParseException but that should be caught 
         // in the factory and rethrown as AeBpelException
         throw new AeBusinessProcessException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * Creates an expression runner context when running an expression.
    * 
    * @param aLanguageURI
    * @param aObject
    */
   protected static IAeExpressionRunnerContext createExpressionRunnerContext(String aLanguageURI, AeAbstractBpelObject aObject)
   {
      return new AeExpressionRunnerContext(aObject, null, aLanguageURI, aObject, aObject, new AeExpressionRunnerVariableResolver(aObject));
   }

   /**
    * Gets the expression language that should be used when executing the 
    * expression found in the given IAeExpressionDef object.
    * 
    * @param aExpressionDef
    */
   protected static String getExpressionLanguage(IAeExpressionDef aExpressionDef)
   {
      AeProcessDef processDef = AeDefUtil.getProcessDef((AeBaseDef) aExpressionDef);
      return AeDefUtil.getExpressionLanguage(aExpressionDef, processDef);
   }
}
 