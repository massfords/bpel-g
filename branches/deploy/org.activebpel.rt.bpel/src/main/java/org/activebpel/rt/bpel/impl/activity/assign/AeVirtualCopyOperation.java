//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/AeVirtualCopyOperation.java,v 1.4.16.1 2008/04/21 16:09:43 ppatruni Exp $
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
 * Impl of copy operation for &lt;assign&gt; activity used in virtual copy. 
 * 
 * This implementation pairs an impl of a &lt;from&gt; and an impl of a &lt;to&gt; 
 * along with a strategy to handle the copy.  
 */
public class AeVirtualCopyOperation extends AeCopyOperationBase
{
   /**
    * Factory method for creating a virtual copy operation for use in a variable
    * intializer. 
    */
   public static AeVirtualCopyOperation createVariableInitializer()
   {
      return new AeVirtualCopyOperation(false);
   }
   
   /**
    * Factory method for creating a virtual copy operation for use in a fromPart
    * or toPart.
    */
   public static AeVirtualCopyOperation createFromPartToPartOperation()
   {
      return new AeVirtualCopyOperation(true);
   }
   
   /**
    * Constructs copy operation with the given explicit value for the "keep
    * source element name" flag.
    *
    * @param aKeepSrcElementName
    */
   protected AeVirtualCopyOperation(boolean aKeepSrcElementName)
   {
      super(aKeepSrcElementName, false);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.AeCopyOperationBase#isVirtual()
    */
   public final boolean isVirtual()
   {
      return true;
   }
} 