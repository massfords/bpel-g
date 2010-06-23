// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AeDeploymentValidator.java,v 1.20 2008/03/20 16:08:11 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.def.validation.AeValidationContext;
import org.activebpel.rt.bpel.def.validation.AeValidationProblemReporter;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.def.validation.IAeValidationContext;
import org.activebpel.rt.bpel.def.visitors.AeDefVisitorFactory;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidatorFactory;

/**
 * Validates process deployments.  This class implements IAeBaseErrorReporter
 * so it can append the pdd resource name to any incoming messages before
 * delegating to the IAeBaseErrorReporter passed in the constructor.
 */
public class AeDeploymentValidator implements IAeBaseErrorReporter
{
   // error constants
   private static final String UNEXPECTED_ERROR = AeMessages.getString("AeDeploymentValidator.UNEXPECTED_ERROR"); //$NON-NLS-1$
   private static final String ABSTRACT_PROCESS_ERROR = AeMessages.getString("AeDeploymentValidator.ABSTRACT_PROCESS_ERROR"); //$NON-NLS-1$
   
   /** The process deployment to validate. */
   private IAeProcessDeployment mDeployment;
   /** The source of the deployment. */
   private String mPddResource;
   /** The error reporter. */
   private IAeBaseErrorReporter mReporter;

   /**
    * Constructor.
    * @param aPddResource The deployment source.
    * @param aDeployment The deployment object.
    * @param aReporter The error handler.
    */
   public AeDeploymentValidator(String aPddResource, IAeProcessDeployment aDeployment, IAeBaseErrorReporter aReporter)
   {
      setPddResource(aPddResource);
      setDeployment(aDeployment);
      setReporter(aReporter);
   }

   /**
    * Validate the deployment.
    */
   public void validate()
   {
      try
      {
         // defect 1360: check for abstract process.
         if (getDeployment().getProcessDef().isAbstractProcess())
         {
            addError(ABSTRACT_PROCESS_ERROR, null, null);
         }
         else
         {
            IAeExpressionLanguageFactory expressionLanguageFactory = AeEngineFactory.getExpressionLanguageFactory();
            IAeFunctionValidatorFactory functionValidatorFactory = AeEngineFactory.getFunctionValidatorFactory();
            
            AeValidationProblemReporter errorReporter = new AeValidationProblemReporter(this, getDeployment().getProcessDef().getNamespace());
            IAeValidationContext validationContext = new AeValidationContext(errorReporter, getDeployment(),
                  expressionLanguageFactory, functionValidatorFactory);

            getDeployment().getProcessDef().preProcessForValidation(getDeployment(), expressionLanguageFactory);
            IAeDefVisitor validator = AeDefVisitorFactory.getInstance( getDeployment().getProcessDef().getNamespace()).createValidationVisitor(validationContext );
            validator.visit(getDeployment().getProcessDef());
       
            AeStaticEndpointReferenceValidator.validate( validationContext.getErrorReporter(), getDeployment() );
            AePolicyReferenceValidator.validate( validationContext.getErrorReporter(), getDeployment() );
            
            //defect 1793: validate persistent/container managed typed process
            AePersistentTypeDeploymentValidator persistentValidator = new AePersistentTypeDeploymentValidator(validationContext.getErrorReporter(), getDeployment() );
            persistentValidator.validate();
         }
      }
      catch( AeException e )
      {
         AeException.logError(e, e.getLocalizedMessage());
         addError( UNEXPECTED_ERROR, new String[] {e.getLocalizedMessage()}, null );
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#addError(java.lang.String, java.lang.Object[], java.lang.Object)
    */
   public void addError(String aErrorCode, Object[] aArgs, Object aNode)
   {
      getReporter().addError( aErrorCode + getLocationHint(), aArgs, aNode );
   }

   /**
    * Returns a string that contains the bpel source location and the pdd 
    * resource name.
    */
   protected String getLocationHint()
   {
      return ": in " + getPddResource(); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#addWarning(java.lang.String, java.lang.Object[], java.lang.Object)
    */
   public void addWarning(String aWarnCode, Object[] aArgs, Object aNode)
   {
      getReporter().addWarning( aWarnCode + getLocationHint(), aArgs, aNode );
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#hasErrors()
    */
   public boolean hasErrors()
   {
      return getReporter().hasErrors();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#hasWarnings()
    */
   public boolean hasWarnings()
   {
      return getReporter().hasWarnings();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter#addInfo(java.lang.String, java.lang.Object[], java.lang.Object)
    */
   public void addInfo(String aInfoCode, Object[] aArgs, Object aNode)
   {
      getReporter().addInfo( aInfoCode + getLocationHint(), aArgs, aNode );
   }

   /**
    * Setter for the deployment
    * @param aDeployment
    */
   protected void setDeployment(IAeProcessDeployment aDeployment)
   {
      mDeployment = aDeployment;
   }

   /**
    * Getter for the deployment
    */
   protected IAeProcessDeployment getDeployment()
   {
      return mDeployment;
   }

   /**
    * Setter for the pdd resource
    * @param aPddResource
    */
   protected void setPddResource(String aPddResource)
   {
      mPddResource = aPddResource;
   }

   /**
    * Getter for the pdd resource
    */
   protected String getPddResource()
   {
      return mPddResource;
   }

   /**
    * Setter for the error reporter
    * @param aReporter
    */
   protected void setReporter(IAeBaseErrorReporter aReporter)
   {
      mReporter = aReporter;
   }

   /**
    * Getter for the error reporter
    */
   protected IAeBaseErrorReporter getReporter()
   {
      return mReporter;
   }
}
