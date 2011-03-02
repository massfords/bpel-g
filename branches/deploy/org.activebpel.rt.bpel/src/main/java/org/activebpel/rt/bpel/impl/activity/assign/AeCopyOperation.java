//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/AeCopyOperation.java,v 1.4.16.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign; 

import org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef;

/**
 * Impl of copy operation for &lt;assign&gt; activity. 
 * 
 * This implementation pairs an impl of a &lt;from&gt; and an impl of a &lt;to&gt; 
 * along with a strategy to handle the copy.  
 */
public class AeCopyOperation extends AeCopyOperationBase
{
   /**
    * Default ctor
    */
   public AeCopyOperation(AeAssignCopyDef aDef, IAeCopyOperationContext aContext)
   {
      super(aContext, aDef.isKeepSrcElementName(), aDef.isIgnoreMissingFromData());
   }
} 