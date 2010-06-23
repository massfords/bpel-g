// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/util/AeDefUtil.java,v 1.26 2008/02/21 21:26:01 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.util;

import java.util.List;
import java.util.ListIterator;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetsDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.IAeCorrelationSetsParentDef;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.IAePartnerLinksParentDef;
import org.activebpel.rt.bpel.def.IAeVariableParentDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Contains some helper methods for the def visitors and validation
 */
public class AeDefUtil
{
   /**
    * Finds the appropriate partner link def with the given partner link name.  This method will
    * search up the process def tree until it finds the partner link or runs out of parents.  This
    * method returns null if no partner link def is found.
    * 
    * @param aDef
    * @param aPartnerLinkName
    */
   public static AePartnerLinkDef findPartnerLinkDef(AeBaseDef aDef, String aPartnerLinkName)
   {
      for (AeBaseDef currentDef = aDef; currentDef != null; currentDef = currentDef.getParent())
      {
         if (currentDef instanceof IAePartnerLinksParentDef)
         {
            AePartnerLinkDef plinkDef = ((IAePartnerLinksParentDef) currentDef).getPartnerLinkDef(aPartnerLinkName);
            if (plinkDef != null)
               return plinkDef;
         }
      }

      return null;
   }
   
   /**
    * Locate a correlation set among the process' definitions, by name.
    *
    * @param aName The name to search.
    * @param aDef The def object from which to search (up).
    *
    * @return AeCorrelationSetDef if found, otherwise null.
    */
   public static AeCorrelationSetDef findCorrSetByName( String aName, AeBaseDef aDef )
   {
      for (AeBaseDef currentDef = aDef; currentDef != null; currentDef = currentDef.getParent())
      {
         if (currentDef instanceof IAeCorrelationSetsParentDef)
         {
            AeCorrelationSetsDef correlationsSetDef = ((IAeCorrelationSetsParentDef) currentDef).getCorrelationSetsDef();
            if (correlationsSetDef != null)
            {
               AeCorrelationSetDef csDef = correlationsSetDef.getCorrelationSetDef(aName);
               if (csDef != null)
                  return csDef;
            }
         }
      }
      return null ;
   }

   /**
    * Given an arbitrary base def, returns the associated activity def.  This method may simply
    * return the def passed to it, in the case that that def WAS itself an activity.  If the
    * passed in def is not an activity, then this method will walk up the tree until it finds an
    * acivity.
    * 
    * @param aDef
    */
   public static AeActivityDef getActivityDef( AeBaseXmlDef aDef )
   {
      AeBaseXmlDef def = aDef;
      while (def != null && !(def instanceof AeActivityDef))
      {
         def = def.getParentXmlDef();
      }
      return (AeActivityDef) def;
   }

   /**
    * Retrieve a variable from the process scope heirarchy, by name. The search begins
    * with the def passed in
    *
    * @param aVariable The name of the variable to search.
    * @param aDef The def object from which to search (up).
    *
    * @return AeVariableDef or null if not found.
    */
   public static AeVariableDef getVariableByName( String aVariable, AeBaseXmlDef aDef )
   {
      // TODO (MF) make the methods here consistent, either String,Def or Def,String
      if (AeUtil.notNullOrEmpty(aVariable) && aDef != null )
      {
         // Search all enclosing scopes.
         //
         for (AeBaseXmlDef currentDef = aDef; currentDef != null; currentDef = currentDef.getParentXmlDef())
         {
            if (currentDef instanceof IAeVariableParentDef)
            {
               AeVariableDef var = ((IAeVariableParentDef)currentDef).getVariableDef(aVariable);
               if (var != null)
               {
                  return var;
               }
            }
         }
      }

      return null ;
   }

   /**
    * Gets the root process def parent of a given def object.
    *
    * @param aDef
    */
   public static AeProcessDef getProcessDef(AeBaseXmlDef aDef)
   {
      if (aDef instanceof AeProcessDef)
      {
         return (AeProcessDef) aDef;
      }

      AeBaseXmlDef def = null;
      for (def = aDef; def != null && !(def instanceof AeProcessDef); def = def.getParentXmlDef() );
      return (AeProcessDef) def;
   }
   
   /**
    * Gets the root process or scope def parent of a given def object.
    *
    * @param aDef
    */
   public static AeBaseDef getEnclosingScopeDef(AeBaseXmlDef aDef)
   {
      if (aDef instanceof AeProcessDef || aDef instanceof AeActivityScopeDef)
      {
         return (AeBaseDef) aDef;
      }

      AeBaseXmlDef def = null;
      for (def = aDef; def != null && !(def instanceof AeProcessDef || def instanceof AeActivityScopeDef); def = def.getParentXmlDef() );
      return (AeBaseDef) def;
   }
   
   /**
    * Returns true if this is a BPWS def
    * @param aDef
    */
   public static boolean isBPWS(AeBaseDef aDef)
   {
      AeProcessDef def = getProcessDef(aDef);
      return def.getNamespace().equals(IAeBPELConstants.BPWS_NAMESPACE_URI);
   }
   
   /**
    * Gets the expression language to use when validating the given expression def.  This method
    * assumes that the given expression def is also an AeBaseDef, so that it can find the 
    * AeProcessDef.
    * 
    * @param aExpressionDef
    */
   public static String getExpressionLanguage(IAeExpressionDef aExpressionDef)
   {
      return getExpressionLanguage(aExpressionDef, AeDefUtil.getProcessDef((AeBaseDef) aExpressionDef));
   }

   /**
    * Gets the expression language to use when validating the given expression def.
    * 
    * @param aExpressionDef
    * @param aProcessDef
    */
   public static String getExpressionLanguage(IAeExpressionDef aExpressionDef, AeProcessDef aProcessDef)
   {
      String expressionLanguage = aExpressionDef.getExpressionLanguage();
      if (AeUtil.isNullOrEmpty(expressionLanguage))
      {
         expressionLanguage = aProcessDef.getExpressionLanguage();
      }
      return expressionLanguage;
   }
   
   /**
    * Return true if the exitOnStandardFault is set to yes, false if set to no or have no definition in process/scope, false if .
    * @param aDef
    */
   public static boolean isExitOnStandardFaultEnabled(AeBaseXmlDef aDef)
   {	
      AeBaseXmlDef parent = aDef.getParentXmlDef();
      Boolean value = null;      
      while (parent != null)
      {
         if (parent instanceof AeActivityScopeDef)
         {
            AeActivityScopeDef scopeDef = (AeActivityScopeDef)parent;
            value = scopeDef.getExitOnStandardFault();
         }
         else if (parent instanceof AeProcessDef)
         {
            AeProcessDef processDef = (AeProcessDef)parent;
            value = processDef.getExitOnStandardFault();
         }
         if ( value != null )
         {
            return value.booleanValue();
         }
         parent = parent.getParentXmlDef(); 
      }
      return false;      
   }

   /**
    * Utility method for replacing an activity within the list.
    * @param aListOfChildren
    * @param aOldActivityDef
    * @param aNewActivityDef
    */
   public static void replaceActivityDef(List aListOfChildren, AeActivityDef aOldActivityDef, AeActivityDef aNewActivityDef)
   {
      boolean replaced = false;
      for (ListIterator liter = aListOfChildren.listIterator(); liter.hasNext() && !replaced; )
      {
         AeActivityDef activityDef = (AeActivityDef) liter.next();
         if (activityDef == aOldActivityDef)
         {
            liter.set(aNewActivityDef);
            replaced = true;
         }
      }
      
      if (!replaced)
         throw new IllegalArgumentException(AeMessages.getString("AeDefUtil.ActivityNotFound")); //$NON-NLS-1$
   }
}
