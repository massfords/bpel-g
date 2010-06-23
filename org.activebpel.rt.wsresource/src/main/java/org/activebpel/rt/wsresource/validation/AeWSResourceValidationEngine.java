// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/AeWSResourceValidationEngine.java,v 1.10 2008/02/17 21:58:06 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.wsresource.IAeWSResourceConstants;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;

/**
 * Engine used to validation web service resources.
 */
public class AeWSResourceValidationEngine
{
   /** The rule registry to use when validating. */
   private IAeWSResourceValidationRuleRegistry mRuleRegistry;
   /** The validation context to use when validating. */
   private IAeWSResourceValidationContext mContext;

   /**
    * C'tor.
    *
    * @param aRuleRegistry
    * @param mContext
    */
   public AeWSResourceValidationEngine(IAeWSResourceValidationRuleRegistry aRuleRegistry,
         IAeWSResourceValidationContext mContext)
   {
      setRuleRegistry(aRuleRegistry);
      setContext(mContext);
   }

   /**
    * Validate a web service resource.
    *
    * @param aResourceModel
    * @param aWSResourceType
    * @param aPreferences
    * @param aProblemHandler
    */
   public void validate(Object aResourceModel, String aWSResourceType,
         IAeWSResourceValidationPreferences aPreferences, IAeWSResourceProblemHandler aProblemHandler)
   {
      AeWSResourceProblemHandlerAdapter problemReporter = new AeWSResourceProblemHandlerAdapter(aProblemHandler);

      // Apply all validation rules
      List validationRules = getRuleRegistry().getRules(aWSResourceType);

      // supply a classloader so the rules can be found

      for (Iterator iter = validationRules.iterator(); iter.hasNext(); )
      {
         IAeWSResourceValidationRule rule = (IAeWSResourceValidationRule) iter.next();
         problemReporter.setProblemInfo(rule.getId(), aPreferences.getSeverity(rule));

         rule.createValidator(getContext()).validate(aResourceModel, getContext(), problemReporter);
      }
   }

   /**
    * @return Returns the context.
    */
   public IAeWSResourceValidationContext getContext()
   {
      return mContext;
   }

   /**
    * @param aContextFactory the context to set
    */
   public void setContext(IAeWSResourceValidationContext aContextFactory)
   {
      mContext = aContextFactory;
   }

   /**
    * @return Returns the ruleRegistry.
    */
   protected IAeWSResourceValidationRuleRegistry getRuleRegistry()
   {
      return mRuleRegistry;
   }

   /**
    * @param aRuleRegistry the ruleRegistry to set
    */
   protected void setRuleRegistry(IAeWSResourceValidationRuleRegistry aRuleRegistry)
   {
      mRuleRegistry = aRuleRegistry;
   }

   /**
    * Implementation of the IAeWSResourceProblemReporter interface.  This class
    * delegates all calls to its instance of IAeWSResourceProblemHandler.
    */
   protected static class AeWSResourceProblemHandlerAdapter implements IAeWSResourceProblemReporter
   {
      /** Problem handler to delegate callbacks to. */
      private IAeWSResourceProblemHandler mProblemHandler;
      /** The ID of the rule being used currently for validation. */
      private QName mCurrentRuleId;
      /** The severity of the rule being used currently for validation. */
      private int mCurrentRuleSeverity;

      /**
       * C'tor.
       *
       * @param aProblemHandler
       */
      public AeWSResourceProblemHandlerAdapter(IAeWSResourceProblemHandler aProblemHandler)
      {
         setProblemHandler(aProblemHandler);
      }

      /**
       * This is called once each time a new rule is being executed. This allows
       * both the rule and the problem handler to be unaware of the ID and
       * severity of the currently executing rule.
       *
       * @param aId
       * @param aSeverity
       */
      public void setProblemInfo(QName aId, int aSeverity)
      {
         setCurrentRuleId(aId);
         setCurrentRuleSeverity(aSeverity);
      }

      /**
       * @see org.activebpel.rt.wsresource.validation.IAeWSResourceProblemReporter#reportProblem(java.lang.String, org.activebpel.rt.xml.def.AeBaseXmlDef)
       */
      public void reportProblem(String aMessage, AeBaseXmlDef aDef)
      {
         Set suppressedIds = getSuppressedRuleIds(aDef);
         // If the document being validated tells us to suppress a rule, then
         // we should do that.
         if (suppressedIds == null || !suppressedIds.contains(getCurrentRuleId()))
         {
            getProblemHandler().reportProblem( getCurrentRuleId(), getCurrentRuleSeverity(), aMessage, aDef.getLocationPath());
         }
      }

      /**
       * Returns the set of suppressed rule IDs currently in scope.
       *
       * @param aDef
       */
      protected Set getSuppressedRuleIds(AeBaseXmlDef aDef)
      {
         Set suppressedIds = null;

         // First get all suppressed rules from the def's parent (if any)
         if (aDef.getParentXmlDef() != null)
         {
            suppressedIds = getSuppressedRuleIds(aDef.getParentXmlDef());
         }

         AeExtensionAttributeDef attributeDef = aDef.getExtensionAttributeDef(new QName(IAeWSResourceConstants.RULES_NAMESPACE, "suppress-problems")); //$NON-NLS-1$
         if (attributeDef != null)
         {
            // Create the set here if not already created.
            if (suppressedIds == null)
            {
               suppressedIds = new HashSet();
            }
            
            // The value of the extension attribute is a list of encoded QNames
            String qnameList = attributeDef.getValue();
            String[] split = qnameList.split(" "); //$NON-NLS-1$
            for (int i = 0; i < split.length; i++)
            {
               String encodedQname = split[i].trim();
               QName qname = aDef.toQName(encodedQname);
               suppressedIds.add(qname);
            }
         }

         return suppressedIds;
      }

      /**
       * @return Returns the problemHandler.
       */
      protected IAeWSResourceProblemHandler getProblemHandler()
      {
         return mProblemHandler;
      }

      /**
       * @param aProblemHandler the problemHandler to set
       */
      protected void setProblemHandler(IAeWSResourceProblemHandler aProblemHandler)
      {
         mProblemHandler = aProblemHandler;
      }

      /**
       * @return Returns the currentRuleId.
       */
      protected QName getCurrentRuleId()
      {
         return mCurrentRuleId;
      }

      /**
       * @param aCurrentRuleId the currentRuleId to set
       */
      protected void setCurrentRuleId(QName aCurrentRuleId)
      {
         mCurrentRuleId = aCurrentRuleId;
      }

      /**
       * @return Returns the currentRuleSeverity.
       */
      protected int getCurrentRuleSeverity()
      {
         return mCurrentRuleSeverity;
      }

      /**
       * @param aCurrentRuleSeverity the currentRuleSeverity to set
       */
      protected void setCurrentRuleSeverity(int aCurrentRuleSeverity)
      {
         mCurrentRuleSeverity = aCurrentRuleSeverity;
      }
   }
}
