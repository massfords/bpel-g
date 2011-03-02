//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/IAeCopyOperation.java,v 1.4.16.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign; 

/**
 * Interface for the standard copy operation implementation.
 */
public interface IAeCopyOperation extends IAeAssignOperation
{
   /**
    * Getter for the copy operation context
    */
   public IAeCopyOperationContext getContext();
   
   /**
    * Setter for the context
    * @param aContext
    */
   public void setContext(IAeCopyOperationContext aContext);
   
   /**
    * Getter for the keepSrcElementName flag
    */
   public boolean isKeepSrcElementName();
   
   /**
    * Returns true if the copy operation is a virtual copy operation. These
    * copy operations exist to implement variable initialization and fromParts
    * and toParts processing. The only difference between these and the ones
    * in &lt;assign&gt; activities are that they do not cause faults for declaring the
    * keepSrcElementName behavior when the copy operation isn't an element to 
    * element style copy.
    */
   public boolean isVirtual();
   
   /**
    * If true, then a &lt;from&gt; that results in zero nodes will cause the &lt;copy&gt;
    * to be a no-op. The &lt;to&gt; MUST NOT be executed.
    */
   public boolean isIgnoreMissingFromData();

}
 