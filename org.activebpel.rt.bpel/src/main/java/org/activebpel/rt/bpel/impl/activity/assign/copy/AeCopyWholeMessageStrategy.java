//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/copy/AeCopyWholeMessageStrategy.java,v 1.2 2006/06/26 16:50:43 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.copy; 

import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.activity.assign.AeMismatchedAssignmentException;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyStrategy;
import org.activebpel.rt.bpel.impl.activity.assign.IAeMessageVariableWrapper;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Copies the whole message variable from one variable to another.
 */
public class AeCopyWholeMessageStrategy implements IAeCopyStrategy
{
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyStrategy#copy(org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation, java.lang.Object, java.lang.Object)
    */
   public void copy(IAeCopyOperation aCopyOperation, Object aFromData, Object aToData) throws AeBpelException
   {
      IAeMessageVariableWrapper fromMessage = (IAeMessageVariableWrapper) aFromData;
      IAeMessageVariableWrapper toMessage = (IAeMessageVariableWrapper) aToData;
      
      if (fromMessage.getVariable().getMessageType().equals(toMessage.getVariable().getMessageType()))
      {
         IAeMessageData msgDataCopy = (IAeMessageData) fromMessage.getVariable().getMessageData().clone();
         toMessage.getVariable().setMessageData(msgDataCopy);
      }
      else
      {
         throw new AeMismatchedAssignmentException(aCopyOperation.getContext().getBPELNamespace());
      }
   }
}
 