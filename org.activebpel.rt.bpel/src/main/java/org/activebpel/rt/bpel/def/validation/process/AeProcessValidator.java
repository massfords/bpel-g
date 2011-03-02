//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/process/AeProcessValidator.java,v 1.8 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.process; 

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.bpel.def.validation.AeLinkValidator;
import org.activebpel.rt.bpel.def.validation.AeMessageExchangeValidationVisitor;
import org.activebpel.rt.bpel.def.validation.IAeValidationContext;
import org.activebpel.rt.bpel.def.validation.IAeValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityPickValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeBaseScopeValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeOnMessageValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeWSIOActivityValidator;
import org.activebpel.rt.bpel.def.validation.activity.wsio.AeCorrelationValidator;
import org.activebpel.rt.bpel.def.validation.extensions.AeExtensionsValidator;
import org.activebpel.rt.bpel.def.validation.query.AeXPathQueryValidator;
import org.activebpel.rt.bpel.def.visitors.AeCheckStartActivityVisitor;
import org.activebpel.rt.util.AeUtil;

/**
 * Root validator for the process.
 */
public class AeProcessValidator extends AeBaseScopeValidator
{
   /** provides context for the validation including WSDL provider, error reporter ...etc  */
   private IAeValidationContext mValidationContext;
   /** list o' models that are marked as create instances */
   private List mCreateInstances = new LinkedList();
   /** Link validation helper for this instance. */
   private AeLinkValidator mLinkValidator; 
   /** The extensions validator. */
   private AeExtensionsValidator mExtensionsValidator;

   /** XPath query validator. */
   private AeXPathQueryValidator mXPathQueryValidator = new AeXPathQueryValidator() ;
   
   /**
    * ctor takes the context and def
    * @param aContext
    * @param aDef
    */
   public AeProcessValidator(IAeValidationContext aContext, AeProcessDef aDef)
   {
      super(aDef);
      mValidationContext = aContext;
      mLinkValidator = new AeLinkValidator( aDef, getReporter() );
      
   }
   
   /**
    * Report an exception among the validation errors.
    *
    * @param aDef The node involved.
    * @param aCause The cause of the exception, i.e., what was being validated.
    * @param aThrowable The exception thrown.
    */
   protected void reportException( AeBaseDef aDef, String aCause, Throwable aThrowable )
   {
      String[] args = new String[] { aCause, aThrowable.getLocalizedMessage()};
      getReporter().reportProblem( BPEL_EXCEPTION_DURING_VALIDATION_CODE, EXCEPTION_DURING_VALIDATION, args, aDef );
      MessageFormat form = new MessageFormat( EXCEPTION_DURING_VALIDATION );
      String msg = form.format( args );
      AeException.logError( aThrowable, msg );
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityScopeValidator#validate()
    */
   public void validate()
   {
      if ( getValidationContext().getContextWSDLProvider() == null )
      {
         // Can't do anything w/o WSDL Provider
         //
         String cause = AeMessages.getString("AeDefValidationVisitor.ERROR_4"); //$NON-NLS-1$
         AeException ae = new AeException(AeMessages.getString("AeDefValidationVisitor.5")); //$NON-NLS-1$
         reportException( getProcessDef(), cause, ae );
         AeException.logError( ae, cause );
         return ;
      }

      try
      {
         super.validate();
         
         // Check for create instance found during traversal.
         //
         if ( !getProcessDef().isAbstractProcess() && mCreateInstances.size() == 0 )
            getReporter().reportProblem( BPEL_NO_CREATE_CODE, ERROR_NO_CREATE, null, getProcessDef() );
   
         // Validate that the process definition conforms to BPEL spec when CreateInstance specified
         if (mCreateInstances.size() > 0)
         {
            AeCheckStartActivityVisitor viz = new AeCheckStartActivityVisitor(getReporter());
            List createInstanceDefs = new ArrayList(mCreateInstances.size());
            for (Iterator iter = mCreateInstances.iterator(); iter.hasNext();)
            {
               AeBaseValidator model = (AeBaseValidator) iter.next();
               createInstanceDefs.add(model.getDefinition());
            }
            viz.doValidation(createInstanceDefs);
         }
   
         // Check for invalid expression language override.
         IAeExpressionLanguageFactory exprLangFactory = getValidationContext().getExpressionLanguageFactory();
         if (!exprLangFactory.supportsLanguage(getProcessDef().getNamespace(), getProcessDef().getExpressionLanguage()))
         {
            getReporter().reportProblem( BPEL_UNSUPPORTED_EXPRESSION_LANGUAGE_CODE,
                                    AeMessages.getString("AeDefValidationVisitor.EXPR_LANGUAGE_NOT_SUPPORTED_ERROR"), //$NON-NLS-1$
                                    new String[] { getProcessDef().getExpressionLanguage() }, getProcessDef());
         }
         else if (AeUtil.notNullOrEmpty(getProcessDef().getExpressionLanguage()) && !exprLangFactory.isBpelDefaultLanguage(getProcessDef().getNamespace(), getProcessDef().getExpressionLanguage()))
         {
            getReporter().reportProblem(BPEL_NON_STANDARD_EXPRESSION_LANGUAGE_CODE,
                                 AeMessages.getString("AeDefValidationVisitor.EXPR_LANGUAGE_NONSTANDARD_WARNING"), //$NON-NLS-1$
                                 new String[] { getProcessDef().getExpressionLanguage() }, getProcessDef());
         }

         new AeMessageExchangeValidationVisitor(getReporter()).visit(getProcessDef());

         if (getExtensionsValidator() != null)
            getExtensionsValidator().validate();
         
         if (mCreateInstances.size() > 1)
            validateMultiStartCorrelation();
         
         getLinkValidator().checkLinks();
      }
      catch(Throwable t)
      {
         reportException( getProcessDef(), getProcessDef().getName(), t );
      }
   }
   
   /**
    * special validation for processes with multiple start activities
    */
   protected void validateMultiStartCorrelation()
   {
      // fixme 2.0 static analysis should emit warning/error for multi-start that shares correlations that aren't initiate="join"
      List wsioActivities = new LinkedList();
      for (Iterator iter = mCreateInstances.iterator(); iter.hasNext();)
      {
         AeBaseValidator model = (AeBaseValidator) iter.next();
         if (model instanceof AeActivityPickValidator)
         {
            for(Iterator iter2 = model.getChildren(AeOnMessageValidator.class).iterator(); iter2.hasNext();)
            {
               AeOnMessageValidator onMessageModel = (AeOnMessageValidator)iter2.next();
               wsioActivities.add(onMessageModel);
            }
         }
         else
         {
            wsioActivities.add(model);
         }
      }
      
      Set acceptedSetPaths = null;
      boolean reportError = false;
      for (Iterator iter = wsioActivities.iterator(); iter.hasNext();)
      {
         Object next = iter.next();
         AeWSIOActivityValidator model = (AeWSIOActivityValidator) next;
         Set setPaths = getCorrelationSetPaths(model);
               
         if (setPaths == null)
         {
            // an activity referenced a correlationSet that wasn't in scope, no point in continuing
            // the validation here since they'll already have error messages for the correlation
            reportError = true;
            break;
         }
         else if (acceptedSetPaths == null)
         {
            acceptedSetPaths = new HashSet(setPaths);
         }
         else if (!acceptedSetPaths.equals(setPaths) || acceptedSetPaths.isEmpty())
         {
            // encountered a create instance that used different correlation paths then a
            // previously encountered create instance. Also detects if the two create instances
            // are empty which is not valid for 1.1 or 2.0 (at least until we impl engine managed correlation)
            reportError = true;
            break;
         }
      }
      
      if (reportError || acceptedSetPaths == null)
      {
         for ( Iterator iter = mCreateInstances.iterator() ; iter.hasNext() ; )
         {
            IAeValidator model = (IAeValidator) iter.next();
            getReporter().reportProblem( BPEL_CORR_SET_MISMATCH_CODE, ERROR_CS_MISMATCH, null, model.getDefinition() );
         }
      }
   }

   /**
    * Gets the paths for the correlation sets used by the wsio activity
    * @param aWSIOActivityModel
    */
   protected Set getCorrelationSetPaths(AeWSIOActivityValidator aWSIOActivityModel)
   {
      List correlationModels = aWSIOActivityModel.getCorrelations();
      Set setPaths = new HashSet();
      for(Iterator iter=correlationModels.iterator(); iter.hasNext();)
      {
         AeCorrelationValidator corrModel = (AeCorrelationValidator) iter.next();
         if (corrModel.getSetModel() == null)
         {
            // an activity referenced a correlationSet that wasn't in scope, no point in continuing
            // the validation here since they'll already have error messages for the correlation
            return null;
         }
         setPaths.add(corrModel.getSetModel().getDef().getLocationPath());
      }
      return setPaths;
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityScopeValidator#add(org.activebpel.rt.bpel.def.validation.AeBaseValidator)
    */
   public void add(AeBaseValidator aModel)
   {
      if (aModel instanceof AeExtensionsValidator)
      {
         setExtensionsValidator((AeExtensionsValidator) aModel);
      }
      else
      {
         super.add(aModel);
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#getProcessDef()
    */
   public AeProcessDef getProcessDef()
   {
      return (AeProcessDef) super.getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#getProcessValidator()
    */
   public AeProcessValidator getProcessValidator()
   {
      return this;
   }
   
   /**
    * records the model as a create instance activity
    * @param aModel
    */
   public void addCreateInstance(AeBaseValidator aModel)
   {
      mCreateInstances.add(aModel);
   }

   /**
    * Getter for the class that validates our links for cycles and such
    */
   public AeLinkValidator getLinkValidator()
   {
      return mLinkValidator;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#getValidationContext()
    */
   public IAeValidationContext getValidationContext()
   {
      return mValidationContext;
   }
   
   /**
    * Validator for xpath queries
    * 
    * TODO (EPW) can't assume xpath - change if we implement a pluggable query framework
    */
   public AeXPathQueryValidator getXPathQueryValidator()
   {
      return mXPathQueryValidator;
   }

   /**
    * @return Returns the extensionsValidator.
    */
   public AeExtensionsValidator getExtensionsValidator()
   {
      return mExtensionsValidator;
   }

   /**
    * @param aExtensionsValidator The extensionsValidator to set.
    */
   protected void setExtensionsValidator(AeExtensionsValidator aExtensionsValidator)
   {
      mExtensionsValidator = aExtensionsValidator;
      mExtensionsValidator.setParent(this);
   }
}
 