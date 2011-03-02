// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeScopeSnapshot.java,v 1.7 2006/06/26 16:50:40 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.impl.AePartnerLink;

/**
 * A snapshot of the process variables for use in compensation. Compensation handlers
 * expect to see and use the exact same variable contents that were in existence
 * at the time of the completion of the scope. In order to facilitate this,
 * we create a snapshot of the variable data and record all of the variable names
 * and their version numbers so we'll be able to pull the correct version of
 * the variable when asked.
 */
public class AeScopeSnapshot
{
   /** Maps the variable name to its version number */
   private final Map mVariableMap;

   /** Maps the correlation set to its version number */
   private final Map mCorrelationSetMap;
   
   /** Maps the partner link to its version number */
   private final Map mPartnerLinkMap;

   /**
    * Default constructor.
    */
   public AeScopeSnapshot()
   {
      this(Collections.EMPTY_MAP, Collections.EMPTY_MAP, Collections.EMPTY_MAP);
   }

   /**
    * Constructor.
    *
    * @param aVariableMap
    * @param aCorrelationSetMap
    */
   public AeScopeSnapshot(Map aVariableMap, Map aCorrelationSetMap, Map aPartnerLinkMap)
   {
      mVariableMap = new HashMap(aVariableMap);
      mCorrelationSetMap = new HashMap(aCorrelationSetMap);
      mPartnerLinkMap = new HashMap(aPartnerLinkMap);
   }

   /**
    * Adds the map of <code>AeVariable</code> by reference. The current map is cleared before
    * adding the new collection.
    * @param aVariableMap
    */
   public void addVariables(Map aVariableMap)
   {
      // Note: this method is used by the AeBusinessProcess when it needs to record the current
      // variable information for Process instance compensation.
      mVariableMap.clear();
      mVariableMap.putAll(aVariableMap);
   }

   /**
    * Adds the map of <code>CorrelationSet</code> by reference. The current correlation set map
    * is first cleared before this set is added.
    * @param aCorrelationSetMap
    */
   public void addCorrelationSets(Map aCorrelationSetMap)
   {
      // Note: this method is used by the AeBusinessProcess when it needs to record the current
      // correlation set information for Process instance compensation.
      mCorrelationSetMap.clear();
      mCorrelationSetMap.putAll(aCorrelationSetMap);
   }

   /**
    * Gets the variable version number from the map
    * @param aName
    */
   public IAeVariable getVariable(String aName)
   {
      return (IAeVariable) mVariableMap.get(aName);
   }

   /**
    * Returns the <code>Set</code> of this snapshot's variables.
    */
   public Set getVariables()
   {
      return new HashSet(mVariableMap.values());
   }

   /**
    * Gets the CorrelationSet version number from the map
    * @param aName
    */
   public AeCorrelationSet getCorrelationSet(String aName)
   {
      return (AeCorrelationSet) mCorrelationSetMap.get(aName);
   }

   /**
    * Returns the <code>Set</code> of this snapshot's correlation sets.
    */
   public Set getCorrelationSets()
   {
      return new HashSet(mCorrelationSetMap.values());
   }
   
   /**
    * Gets the partner link from the map.
    * 
    * @param aName
    */
   public AePartnerLink getPartnerLink(String aName)
   {
      return (AePartnerLink) mPartnerLinkMap.get(aName);
   }
   
   /**
    * Gets a set of the snapshot's partner links.
    */
   public Set getPartnerLinks()
   {
      return new HashSet(mPartnerLinkMap.values());
   }
   
   /**
    * Adds all of the partner links in the given map to the snapshot.
    * 
    * @param aPartnerLinkMap
    */
   public void addPartnerLinks(Map aPartnerLinkMap)
   {
      mPartnerLinkMap.clear();
      mPartnerLinkMap.putAll(aPartnerLinkMap);
   }
}
