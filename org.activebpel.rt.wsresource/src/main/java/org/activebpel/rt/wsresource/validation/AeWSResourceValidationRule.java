// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/AeWSResourceValidationRule.java,v 1.7 2008/02/17 21:58:06 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation;

import java.text.MessageFormat;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.wsresource.AeMessages;

/**
 * Implementation of a validation rule.
 */
public class AeWSResourceValidationRule implements IAeWSResourceValidationRule
{
   /** The rule id. */
   private QName mId;
   /** Rule description. */
   private String mDescription;
   /** The name of the validator class. */
   private String mValidatorClassname;
   /** The default severity of the rule. */
   private int mDefaultSeverity;

   /**
    * C'tor.
    * 
    * @param aId
    * @param aDefaultSeverity
    * @param aDescription
    * @param aValidatorClassname
    */
   public AeWSResourceValidationRule(QName aId, int aDefaultSeverity, String aDescription,
         String aValidatorClassname)
   {
      setId(aId);
      setDescription(aDescription);
      setValidatorClassname(aValidatorClassname);
      setDefaultSeverity(aDefaultSeverity);
   }

   /**
    * @see org.activebpel.rt.wsresource.validation.IAeWSResourceValidationRule#getId()
    */
   public QName getId()
   {
      return mId;
   }

   /**
    * @see org.activebpel.rt.wsresource.validation.IAeWSResourceValidationRule#createValidator(org.activebpel.rt.wsresource.validation.IAeWSResourceValidationContext)
    */
   public IAeWSResourceValidator createValidator(IAeWSResourceValidationContext aContext)
   {
      try
      {
         ClassLoader classloader = aContext.getRuleClassLoader();
         if (classloader != null)
            return (IAeWSResourceValidator) classloader.loadClass(getValidatorClassname()).newInstance();
         else
            return (IAeWSResourceValidator) Class.forName(getValidatorClassname()).newInstance();
      }
      catch (final Exception ex)
      {
         AeException.logError(ex);
         return new IAeWSResourceValidator()
         {
            /**
             * @see org.activebpel.rt.wsresource.validation.IAeWSResourceValidator#validate(java.lang.Object, org.activebpel.rt.wsresource.validation.IAeWSResourceValidationContext, org.activebpel.rt.wsresource.validation.IAeWSResourceProblemReporter)
             */
            public void validate(Object aResourceModel, IAeWSResourceValidationContext aContext,
                  IAeWSResourceProblemReporter aErrorReporter)
            {
               String msg = MessageFormat.format(AeMessages.getString("AeWSResourceValidationRule.ErrorCreatingValidatorClass"), //$NON-NLS-1$
                     new Object[] { getId().getLocalPart(), ex.getLocalizedMessage() } );
               aErrorReporter.reportProblem(msg, null);
            }
         };
      }
   }

   /**
    * @return Returns the validatorClassname.
    */
   protected String getValidatorClassname()
   {
      return mValidatorClassname;
   }

   /**
    * @param aValidatorClassname the validatorClassname to set
    */
   protected void setValidatorClassname(String aValidatorClassname)
   {
      mValidatorClassname = aValidatorClassname;
   }

   /**
    * @param aId the id to set
    */
   protected void setId(QName aId)
   {
      mId = aId;
   }

   /**
    * @see org.activebpel.rt.wsresource.validation.IAeWSResourceValidationRule#getDescription()
    */
   public String getDescription()
   {
      return mDescription;
   }

   /**
    * @param aDescription the description to set
    */
   protected void setDescription(String aDescription)
   {
      mDescription = aDescription;
   }

   /**
    * @return Returns the mDefaultSeverity.
    */
   public int getDefaultSeverity()
   {
      return mDefaultSeverity;
   }

   /**
    * @param aDefaultSeverity the mDefaultSeverity to set
    */
   protected void setDefaultSeverity(int aDefaultSeverity)
   {
      mDefaultSeverity = aDefaultSeverity;
   }
}
