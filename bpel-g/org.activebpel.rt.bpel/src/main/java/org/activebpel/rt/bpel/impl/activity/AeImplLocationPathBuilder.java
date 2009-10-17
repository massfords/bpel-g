//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeImplLocationPathBuilder.java,v 1.2 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.bpel.def.visitors.AeDynamicInstancePathBuilder;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.IAePathSegmentBuilder;

/**
 * Used during the creation or restoration of dynamic scope instances for a
 * parallel forEach, onEvent, or repeating onAlarm.
 */
public class AeImplLocationPathBuilder extends AeDynamicInstancePathBuilder
{
   /** map of def objects to their path information */
   private Map mDefToPathMap = new HashMap();
   /** flag that tells us if we're in create mode or not */
   private boolean mCreateMode;
   
   /**
    * Ctor
    * @param aSegmentBuilder
    * @param aDef
    * @param aCreateFlag
    */
   public AeImplLocationPathBuilder(IAePathSegmentBuilder aSegmentBuilder, AeBaseXmlDef aDef, boolean aCreateFlag)
   {
      super(aSegmentBuilder, aDef);
      setCreateMode(aCreateFlag);
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.AeLocationPathVisitor#recordLocationPathAndId(org.activebpel.rt.xml.def.AeBaseXmlDef, java.lang.String, int)
    */
   protected void recordLocationPathAndId(AeBaseXmlDef aDef,
         String aLocationPath, int aLocationId)
   {
      // the mapping of location path to id will be done already
      // we just need to record the mapping of the def to its location path
      getDefToPathMap().put(aDef, aLocationPath);
   }

   /**
    * If we're being called as part of the forEach's/onEvent's execution, then we create
    * the objects and set their location ids in place. We callback onto the
    * process to record each new location id since the process will need to track
    * the ids to ensure that they're unique.
    * 
    * If we're being called from the restoreChildren() method then we create
    * the child instances with a location id of -1 since the real id will be
    * set by the restore visitor from the state document. 
    * 
    * @see org.activebpel.rt.xml.def.visitors.AeLocationPathVisitor#setNextLocationId(int)
    */
   public void setNextLocationId(int aId)
   {
      if (isCreateMode())
      {
         super.setNextLocationId(aId);
      }
      else
      {
         super.setNextLocationId(-1);
      }
   }

   /**
    * Getter for the location path
    * 
    * @param aDef
    */
   protected String getLocationPath(AeBaseXmlDef aDef)
   {
      return (String) getDefToPathMap().get(aDef);
   }

   /**
    * @return the defToPathMap
    */
   protected Map getDefToPathMap()
   {
      return mDefToPathMap;
   }
   
   /**
    * @return Returns the createMode.
    */
   public boolean isCreateMode()
   {
      return mCreateMode;
   }

   /**
    * @param aCreateMode The createMode to set.
    */
   public void setCreateMode(boolean aCreateMode)
   {
      mCreateMode = aCreateMode;
   }
} 