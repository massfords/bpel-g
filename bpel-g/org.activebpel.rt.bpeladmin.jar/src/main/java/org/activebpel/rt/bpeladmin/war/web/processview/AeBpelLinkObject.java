//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeBpelLinkObject.java,v 1.3 2005/12/03 01:10:55 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//                   PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpeladmin.war.web.processview;

import org.activebpel.rt.bpel.def.activity.AeActivityFlowDef;
import org.activebpel.rt.bpel.def.activity.support.AeLinkDef;
import org.activebpel.rt.util.AeUtil;

/**
 * Represents a BPEL link between two endpoints.
 */
public class AeBpelLinkObject extends AeBpelActivityObject
{
   /** Source activity ("from") of this link.    */
   private  AeBpelActivityObject mSource = null;
   
   /** Target activity ("to") of this link.    */
   private  AeBpelActivityObject mTarget = null;
   
   /** Indicates the link status - true, false or unknown. */
   private String mStatus = "unknown";//$NON-NLS-1$
   
   /** Indicates is this link was evaluated based on the Process state information. */
   private boolean mEvaluated = false;
   
   /**
    * Ctor.
    * @param aDef link definition
    */
   public AeBpelLinkObject(AeLinkDef aDef)
   {
      super(AeActivityFlowDef.TAG_LINK, aDef);
   }
   
   /** 
    * @return The link's source activity.
    */
   public AeBpelActivityObject getSource()
   {
      return mSource;
   }

   /**
    * Sets the link source.
    * @param aSource
    */
   public void setSource(AeBpelActivityObject aSource)
   {
      mSource = aSource;
   }

   /**
    * @return Returns the links target.
    */
   public AeBpelActivityObject getTarget()
   {
      return mTarget;
   }
   
   /**
    * Sets the links target.
    * @param aTarget
    */
   public void setTarget(AeBpelActivityObject aTarget)
   {
      mTarget = aTarget;
   }   
   
   /**
    * @return Returns the status.
    */
   public String getStatus()
   {
      return mStatus;
   }
   
   /**
    * @param aStatus The status to set.
    */
   public void setStatus(String aStatus)
   {
      mStatus = aStatus;
      if ("true".equalsIgnoreCase(aStatus))  //$NON-NLS-1$
      {
         setEvaluated(true);
      }      
   }
   
   /**  
    * @return True if this link was evaluated by the engine.
    */
   public boolean isEvaluated()
   {
      return mEvaluated;
   }
   
   /**
    * Sets the links evaluated state.
    * @param aEval
    */
   public void setEvaluated(boolean aEval)
   {
      mEvaluated = aEval;      
   }
   
   /**
    * @return true if this link has a transistion condition.
    */
   public boolean hasTransistionCondition()
   {
      return !(AeUtil.isNullOrEmpty(getCondition()) );
   }
}
