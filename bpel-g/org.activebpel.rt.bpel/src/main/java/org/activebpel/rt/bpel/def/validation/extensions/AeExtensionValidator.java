//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/extensions/AeExtensionValidator.java,v 1.4 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.extensions; 

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.bpel.def.AeExtensionDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.bpel.def.validation.IAeValidator;

/**
 * model provides validation for an extension model
 */
public class AeExtensionValidator extends AeBaseValidator
{
   /** Users of this extension. */
   private List mExtensionUsers = new LinkedList();

   /**
    * ctor
    * @param aDef
    */
   public AeExtensionValidator(AeExtensionDef aDef)
   {
      super(aDef);
   }

   /**
    * Gets the extension's namespace.
    */
   public String getNamespace()
   {
      return getDef().getNamespace();
   }
   
   /**
    * Gets the extension's 'mustUnderstand' flag.
    */
   public boolean isMustUnderstand()
   {
      return getDef().isMustUnderstand();
   }
   
   /**
    * Adds a usage of the extension.
    * 
    * @param aValidator
    * @param aUnderstood
    */
   public void addUsage(IAeValidator aValidator, boolean aUnderstood)
   {
      getExtensionUsers().add(new AeExtensionUsage(aValidator, aUnderstood));
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      if (!isReferenced())
      {
         getReporter().reportProblem( BPEL_EXTENSION_NOT_USED_CODE, WARNING_EXTENSION_NOT_USED,  new String[] { getDef().getNamespace() }, getDefinition() );
      }
      else if (isMustUnderstand())
      {
         // Find any usages that are not understood.
         for (Iterator iter = getExtensionUsers().iterator(); iter.hasNext(); )
         {
            AeExtensionUsage usage = (AeExtensionUsage) iter.next();
            if (!usage.isUnderstood())
            {
               getReporter().reportProblem(BPEL_EXTENSION_NOT_UNDERSTOOD_CODE, ERROR_DID_NOT_UNDERSTAND_EXTENSION, null, usage.getValidator().getDefinition());
            }
         }
      }
      super.validate();
   }

   /**
    * Returns true if someone references this extension.
    */
   protected boolean isReferenced()
   {
      return getExtensionUsers().size() > 0;
   }
   
   /**
    * @return Returns the extensionUsers.
    */
   public List getExtensionUsers()
   {
      return mExtensionUsers;
   }

   /**
    * @param aExtensionUsers The extensionUsers to set.
    */
   public void setExtensionUsers(List aExtensionUsers)
   {
      mExtensionUsers = aExtensionUsers;
   }

   /**
    * Getter for the def
    */
   public AeExtensionDef getDef()
   {
      return (AeExtensionDef) getDefinition();
   }
 
   /**
    * Simple class that contains information about how the extension is being used.
    */
   protected static class AeExtensionUsage
   {
      /** The validator that uses the extension. */
      private IAeValidator mValidator;
      /** Flag indicating whether the extension usage was understood. */
      private boolean mUnderstood;

      /**
       * Constructor.
       * 
       * @param mValidator
       * @param aUnderstood
       */
      public AeExtensionUsage(IAeValidator mValidator, boolean aUnderstood)
      {
         setValidator(mValidator);
         setUnderstood(aUnderstood);
      }

      /**
       * @return Returns the validator.
       */
      public IAeValidator getValidator()
      {
         return mValidator;
      }

      /**
       * @param aValidator The validator to set.
       */
      public void setValidator(IAeValidator aValidator)
      {
         mValidator = aValidator;
      }

      /**
       * @return Returns the understood.
       */
      public boolean isUnderstood()
      {
         return mUnderstood;
      }

      /**
       * @param aUnderstood The understood to set.
       */
      public void setUnderstood(boolean aUnderstood)
      {
         mUnderstood = aUnderstood;
      }
   }
}
 