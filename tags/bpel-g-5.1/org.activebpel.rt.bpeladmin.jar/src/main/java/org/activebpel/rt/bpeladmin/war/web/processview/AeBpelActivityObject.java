//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeBpelActivityObject.java,v 1.3 2006/07/25 17:56:39 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpeladmin.war.web.processview;

import java.util.ArrayList;
import java.util.List;

import org.activebpel.rt.bpel.def.AeBaseDef;

/**
 * Represents a BPEL "activity" as per BPEL v1.1. An activity in this case
 * is for example, Receive, Pick, Reply etc. which have the required
 * "standard elements" target and source.
 */
public class AeBpelActivityObject extends AeBpelObjectContainer
{
   /** List containing source links. */
   private List mSourceLinks = new ArrayList();
   
   /** List of target links */
   private List mTargetLinks = new ArrayList();
   
   /** BPEL activity specific condition attribute. This attribute may be used to
    *  display text next to the activity (as an adornment).
    */
   private String mCondition = ""; //$NON-NLS-1$
      
   /**
    * Default ctor.
    * @param aTagName activity tag name
    * @param aDef activity definition.
    */
   public AeBpelActivityObject(String aTagName, AeBaseDef aDef)
   {
      super(aTagName, aDef);
   }
      
   /**
    * Default ctor.
    * @param aTagName activity tag name
    * @param aIconName activity icon.
    * @param aDef activity definition.
    */   
   public AeBpelActivityObject(String aTagName, String aIconName, AeBaseDef aDef)
   {
      super(aTagName, aIconName, aDef);
   }
   
   /** 
    * @return List of source links.
    */
   public List getSourceLinks()
   {
      return mSourceLinks;
   }
   
   /**
    * Adds a source link (outbound) to this activity. 
    * @param aLink outbound source link.
    */
   public void addSourceLink(AeBpelLinkObject aLink)
   {
      if (!mSourceLinks.contains(aLink))
      {
         mSourceLinks.add(aLink);
         aLink.setSource(this);
      }
   }

   /** 
    * @return List of link targets associated with this activity.
    */
   public List getTargetLinks()
   {
      return mTargetLinks;
   }
   
   /**
    * Adds a target link.
    * @param aLink
    */
   public void addTargetLink(AeBpelLinkObject aLink)
   {
      if (!mTargetLinks.contains(aLink))
      {
         mTargetLinks.add(aLink);
         aLink.setTarget(this);
      }
   }
   
   /**
    * Returns, condition for the used in the BPEL activity to be displayed
    * as an adorment in the presentation layer. For example, the 'duration'
    * in a Wait activity, or 'condition' in a While activity.
    * 
    * @return activity condition.
    */
   public String getCondition()
   {
      return mCondition;
   }
   
   /**
    * Sets the BPEL activity condition, when applicable.
    * @param aCond
    */
   public void setCondition(String aCond)
   {
      mCondition = aCond;
   }
      
}
