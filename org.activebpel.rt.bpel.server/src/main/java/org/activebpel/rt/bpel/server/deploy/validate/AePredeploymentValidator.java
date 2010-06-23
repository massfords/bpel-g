// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AePredeploymentValidator.java,v 1.14 2006/07/18 20:05:32 ckeller Exp $
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
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;

/**
 * The top level predeployment validator.  Maintains a collection
 * of the "default" IAePredeploymentValidator objects.
 */
public class AePredeploymentValidator implements IAePredeploymentValidator
{
   /** The default IAePredeploymentValidators */
   private IAePredeploymentValidator[] mValidators;

   /**
    * @param aValidators
    */
   public AePredeploymentValidator(IAePredeploymentValidator[] aValidators)
   {
      mValidators = aValidators;
   }
   
   /**
    * Constructor.
    * This instance will be populated with the default predeployment validators.
    */
   public AePredeploymentValidator()
   {
      mValidators = createDefaultValidators();
   }
   
   /**
    * Factory method that creates the default predeployment validator
    */
   public static IAePredeploymentValidator createDefault()
   {
      return new AePredeploymentValidator( createDefaultValidators() );
   }
   
   /**
    * Create the default predeployment validtors.
    */
   protected static IAePredeploymentValidator[] createDefaultValidators()
   {
      return new IAePredeploymentValidator[]
      {
         new AePddValidator(),
         new AeCatalogValidator(),
         new AeWsdlValidator(),
         new AeBpelLocationValidator(),
         new AePartnerLinkValidator(),
         new AeProcessNameMatchValidator(),
         new AeWsdlCircRefValidator(),
         new AeDuplicateServiceNameValidator(),
         new AeServiceQNameValidator()
      };
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.validate.IAePredeploymentValidator#validate(org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr, org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter)
    */
   public void validate( IAeBpr aBprFile, IAeBaseErrorReporter aReporter )
   throws AeException
   {
      for( int i=0; i < mValidators.length; i++ )
      {
         IAePredeploymentValidator validator = (IAePredeploymentValidator)mValidators[i];
         try
         {
            validator.validate( aBprFile, aReporter );
         }
         catch( Throwable t )
         {
            // if there were no reported errors, then something bad happened
            // so log the error and throw the wrapped exception
            if( !aReporter.hasErrors() )
            {
               AeException.logError( t, t.getLocalizedMessage() );
               throw new AeException(t);
            }
            else
            {
               // trap this exception to handle the case where an error in one
               // validator (eg: missing bpel file) could cause another unexpected
               // error in another validator (eg: NPE in partner link validation)
               Object[] args = { aBprFile.getBprFileName() };
               String msg = AeMessages.format( "AePredeploymentValidator.ERROR_0", args );  //$NON-NLS-1$
               aReporter.addError(msg, args, null);
            }
         }
      }
   }
}
