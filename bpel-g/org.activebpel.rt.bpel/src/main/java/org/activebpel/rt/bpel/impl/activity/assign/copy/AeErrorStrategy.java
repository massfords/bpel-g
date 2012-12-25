//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/copy/AeErrorStrategy.java,v 1.3 2006/09/07 15:06:26 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.copy; 

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeFaultFactory;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyStrategy;

/**
 * Strategy that throws an error since something unexpected has happened in the strategy lookup (e.g.
 * an unknown type is found in either the to or from).
 */
public class AeErrorStrategy implements IAeCopyStrategy
{
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyStrategy#copy(org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation, java.lang.Object, java.lang.Object)
    */
   public void copy(IAeCopyOperation aCopyOperation, Object aFromData, Object aToData) throws AeBpelException
   {
      IAeFault fault = AeFaultFactory.getSystemErrorFault();
      Object [] args = new Object[] { getClassName(aFromData), getClassName(aToData) };
      fault.setInfo(AeMessages.format("AeErrorStrategy.ErrorLookingUpStrategy", args)); //$NON-NLS-1$
      throw new AeBpelException(fault.getInfo(), fault);
   }

   /**
    * Returns the name of the class of the given object, or null if the object is null.
    * 
    * @param aObject
    */
   protected String getClassName(Object aObject)
   {
      return (aObject == null) ? null : aObject.getClass().getName();
   }
}
