//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityAssignWSBPELImpl.java,v 1.6 2008/02/29 04:09:36 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.AeActivityAssignDef;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.activity.assign.AeAtomicCopyOperationContext;

/**
 * Assign impl for 2.0. Extends the base class by adding support for the validate attribute
 * as well as an additional hook for locking variables referenced by extension operations. 
 */
public class AeActivityAssignWSBPELImpl extends AeActivityAssignImpl
{
   /**
    * Ctor accepts the def and parent
    * @param aAssign
    * @param aParent
    */
   public AeActivityAssignWSBPELImpl(AeActivityAssignDef aAssign, IAeActivityParent aParent)
   {
      super(aAssign, aParent);
   }

   /**
    * Looks for optional validate flag and validates all of the variables assigned to after the 
    * copy operations have completed without an error.
    * 
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityAssignImpl#executeOperations()
    */
   protected void executeOperations() throws AeBusinessProcessException
   {
      super.executeOperations();
      
      // if we got here, then there were no errors
      if (getDef().isValidate())
      {
         validate();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityImpl#acquireResourceLocks()
    */
   protected boolean acquireResourceLocks()
   {
      // fixme extensibleAssign: need to walk the copy operations to lock any variables used by extensible operations  
      return super.acquireResourceLocks();
   }

   /**
    * Validates all of the variables that have been modified by the assign's operations.
    */
   protected void validate() throws AeBpelException
   {
      // TODO (MF) change to validate all of the variables and then report all failures at once
      Map rollbackMap = ((AeAtomicCopyOperationContext)getCopyOperationContext()).getRollbackMap();
      for(Iterator it = rollbackMap.keySet().iterator(); it.hasNext();)
      {
         // might be a partner link
         Object modifiedData = it.next();
         if (modifiedData instanceof IAeVariable)
         {
            IAeVariable var = (IAeVariable) modifiedData;
            var.validate();
         }
      }
   }
   
   /**
    * Getter for the assign def.
    */
   protected AeActivityAssignDef getDef()
   {
      return (AeActivityAssignDef) getDefinition();
   }
}
 