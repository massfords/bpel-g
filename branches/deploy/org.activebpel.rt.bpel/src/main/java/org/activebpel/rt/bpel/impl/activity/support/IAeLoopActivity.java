//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/IAeLoopActivity.java,v 1.3.22.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.activity.IAeLoopControl;

/**
 * An interface for containers that loop. This includes the &lt;while&gt; activity
 * and the &lt;foreach&gt; activity. These containers support nested &lt;aex:continue&gt;
 * and &lt;aex:break&gt; activities.
 */
public interface IAeLoopActivity extends IAeBpelObject
{
   /** default value indicating there is no early termination in play */
   public static final int REASON_NONE = 0;
   
   /** 
    * value that indicates that the loop container is terminating its children 
    * because it was asked to continue
    */
   public static final int REASON_CONTINUE = 1;
   
   /** 
    * value that indicates that the loop container is terminating its children 
    * because it was asked to break
    */
   public static final int REASON_BREAK = 2;
   
   /**
    * Signals the container that it should continue its loop.
    *  
    * @param aSource
    * @throws AeBusinessProcessException
    */
   public void onContinue(IAeLoopControl aSource) throws AeBusinessProcessException;
   
   /**
    * Signals the container that it should break out of its loop.
    *  
    * @param aSource
    * @throws AeBusinessProcessException
    */
   public void onBreak(IAeLoopControl aSource) throws AeBusinessProcessException;
}
 