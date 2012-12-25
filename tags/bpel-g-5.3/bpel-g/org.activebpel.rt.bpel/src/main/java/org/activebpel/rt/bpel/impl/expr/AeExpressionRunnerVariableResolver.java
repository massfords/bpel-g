//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/expr/AeExpressionRunnerVariableResolver.java,v 1.2 2008/02/17 21:37:10 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.expr;

import org.activebpel.rt.bpel.IAeVariableView;
import org.activebpel.rt.bpel.def.expr.xpath.AeXPathVariableReference;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.AeUninitializedVariableException;

/**
 * Implements variable resolver by using <code>IAeBpelObject::findVariable</code>
 * method to resolve a Business Process variable.
 */
public class AeExpressionRunnerVariableResolver implements IAeExpressionRunnerVariableResolver
{
   /**
    * BPEL object.
    */
   private AeAbstractBpelObject mBpelObject;

   /**
    * BPEL context object. The variable is found by checking to see if BPEL object
    * owns the variable, otherwise the request propogates up to the parent activity.
    * @param aBpelObj
    */
   public AeExpressionRunnerVariableResolver(AeAbstractBpelObject aBpelObj)
   {
      setBpelObject(aBpelObj);
   }

   /**
    * @return BPEL object
    */
   protected AeAbstractBpelObject getBpelObject()
   {
      return mBpelObject;
   }

   /**
    * Sets the bpel object.
    * @param aBpelObject
    */
   protected void setBpelObject(AeAbstractBpelObject aBpelObject)
   {
      mBpelObject = aBpelObject;
   }

   /**
    * Returns variable given local name of variable (excluding any part notation).
    * @param aVariableName
    */
   protected IAeVariableView internalFindVariable(String aVariableName)
   {
      return getBpelObject().findVariable(aVariableName);
   }

   /**
    * Overrides method to locate the business process variable by name.
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerVariableResolver#findVariable(java.lang.String)
    */
   public IAeVariableView findVariable(String aName)
   {
      AeXPathVariableReference varRef = new AeXPathVariableReference(aName);
      return internalFindVariable(varRef.getVariableName());
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerVariableResolver#variableExists(java.lang.String)
    */
   public boolean variableExists(String aName)
   {
      return findVariable(aName) != null;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerVariableResolver#getVariableData(java.lang.String)
    */
   public Object getVariableData(String aName) throws AeUninitializedVariableException
   {
      AeXPathVariableReference varRef = new AeXPathVariableReference(aName);
      IAeVariableView variable = internalFindVariable(varRef.getVariableName());
      Object result = null;
      if (variable.hasData())
      {
         // Is the variable a message variable?
         if (variable.isMessageType())
         {
            result = variable.getMessageData().getData(varRef.getPartName());
         }
         // Is the variable a simple type?
         else if (variable.isType())
         {
            result = variable.getTypeData();
         }
         // Must be a element type
         else
         {
            result = variable.getElementData();
         }
      }      
      return result;
   }

}
