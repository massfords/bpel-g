// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDefEntryPointPropertiesVisitor.java,v 1.8 2006/10/05 21:15:31 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
import org.activebpel.rt.bpel.def.AeCorrelationsDef;
import org.activebpel.rt.bpel.def.AePartnerLinkOpKey;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;

/**
 * Visit all of the receives and picks and build a mapping of
 * partnerLinkName:portTypeQName:operationName to a set of 
 * correlation properties.
 */
public class AeDefEntryPointPropertiesVisitor extends AeAbstractDefVisitor
{
   /** map for correlation properties */
   private Map mPropertiesMap;
   /** map of plink/op keys to messagePartsMaps */
   private Map mMessagePartsMap;

   /**
    * Constructor.
    */
   public AeDefEntryPointPropertiesVisitor()
   {
      setTraversalVisitor( new AeTraversalVisitor( new AeDefTraverser(), this ) );
      mPropertiesMap = new HashMap();
      mMessagePartsMap = new HashMap();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef aDef)
   {
      if( aDef.hasCorrelationList() )
      {
         Map corrMap = findCorrelationSets(aDef);
         AePartnerLinkOpKey key = aDef.getPartnerLinkOperationKey();
         add(key, extractProperties(corrMap, aDef.getCorrelationsDef()));
         getMessagePartsMap().put(key, aDef.getConsumerMessagePartsMap());
      }
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef aDef)
   {
      if( aDef.hasCorrelationList() )
      {
         Map corrMap = findCorrelationSets(aDef);
         AePartnerLinkOpKey key = aDef.getPartnerLinkOperationKey();
         add(key, extractProperties(corrMap, aDef.getCorrelationsDef()));
         getMessagePartsMap().put(key, aDef.getConsumerMessagePartsMap());
      }
      super.visit(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      if( aDef.hasCorrelationList() )
      {
         Map corrMap = findCorrelationSets(aDef.getContext());
         AePartnerLinkOpKey key = aDef.getPartnerLinkOperationKey();
         add(key, extractProperties(corrMap, aDef.getCorrelationsDef()));
         getMessagePartsMap().put(key, aDef.getConsumerMessagePartsMap());
      }
      super.visit(aDef);
   }

   /**
    * @param aDef the current def object being visited
    * @return map of all applicable correlation sets keyed by name
    */
   protected Map findCorrelationSets( AeBaseDef aDef )
   {
      Map map = new HashMap();
      extractCorrelationSets( aDef, map );
      return map;
   }
   
   /**
    * Given a def object, walk UP the parent tree and look for any
    * defined correlation sets and add them to the map. 
    * <br />
    * Correlation sets closer to the given base def (lower in the tree) 
    * take precedence over sets defined with the same name at a 
    * higher level.  Given two sets with matching names, only the 
    * lowest level on will be added to the map.
    * @param aBaseDef point at which to start walking
    * @param aMap holds correlation sets keyed by name
    */
   protected void extractCorrelationSets( AeBaseDef aBaseDef, Map aMap )
   {
      if( aBaseDef instanceof AeProcessDef )
      {
         addCorrelationSets( ((AeProcessDef)aBaseDef).getCorrelationSetDefs(), aMap );         
         return;
      }
      else if( aBaseDef instanceof AeActivityScopeDef )
      {
         addCorrelationSets( ((AeActivityScopeDef)aBaseDef).getScopeDef().getCorrelationSetDefs(), aMap );
      }
      
      extractCorrelationSets( aBaseDef.getParent(), aMap );
   }
   
   /**
    * Iterator over the correlation sets and add any NEW ones
    * to the given map.
    * @param aIter backed by a collection of AeCorrelationSetDef objects
    * @param aMap hold correlation sets keyed by name
    */
   protected void addCorrelationSets( Iterator aIter, Map aMap )
   {
      while( aIter.hasNext() )
      {
         AeCorrelationSetDef corrSet = (AeCorrelationSetDef)aIter.next();
         if( !aMap.containsKey(corrSet.getName() ) )
         {
            aMap.put( corrSet.getName(), corrSet );
         }
      }
   }
   
   /**
    * Add any new properites to the map.
    * @param aKey
    * @param aPropertyQNames
    */
   protected void add( AePartnerLinkOpKey aKey, Set aPropertyQNames )
   {
      // receives/onMessages can have same partnerLink,
      // portType and operation - so the key value will
      // be the same 
      // so look for existing properties set with 
      // that key first - and add any new props
      // into the set
      // otherwise, add a new entry into the map
      Set props = (Set)mPropertiesMap.get( aKey );
      if( props == null )
      {
         mPropertiesMap.put( aKey, aPropertyQNames );               
      }
      else
      {
         props.addAll( aPropertyQNames );
      }
   }
   
   /**
    * Extract all of the correlations from the container,
    * find their corresponding correlation sets and "collect"
    * the properties in a set.
    * @param aContainer
    * @return set of all correlation properties
    */
   protected Set extractProperties( Map aMap, AeCorrelationsDef aContainer )
   {
      Set propertyQNames = new HashSet( aContainer.getSize() );
      for (Iterator iter = aContainer.getValues(); iter.hasNext();)
      {
         AeCorrelationDef  corrDef = (AeCorrelationDef) iter.next();
         String name = corrDef.getCorrelationSetName();
         AeCorrelationSetDef corrSetDef = (AeCorrelationSetDef)aMap.get( name );
         propertyQNames.addAll(corrSetDef.getProperties());      
      }
      return propertyQNames;      
   }
   
   /**
    * Accessor for correlation property mappings.
    */
   public Map getPropertiesMap()
   {
      return mPropertiesMap;
   }

   /**
    * @return Returns the messagePartsMap.
    */
   public Map getMessagePartsMap()
   {
      return mMessagePartsMap;
   }
}
