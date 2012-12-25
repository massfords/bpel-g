//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/copy/AeSelectionFailureStrategy.java,v 1.4 2006/09/07 15:06:26 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.activity.assign.copy;

import java.util.List;

import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeSelectionFailureException;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyStrategy;

/**
 * This strategy is used whenever a bpel:selectionFailure is guaranteed, such as when multiple nodes
 * are selected.
 */
public class AeSelectionFailureStrategy implements IAeCopyStrategy
{
   /**
    * Reports bpel:selectionFailure.
    * 
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyStrategy#copy(org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation, java.lang.Object, java.lang.Object)
    */
   public void copy(IAeCopyOperation aCopyOperation, Object aFromData, Object aToData) throws AeBpelException
   {
      // The problem might be with the from data OR the to data.
      int selectionCount = 0;
      if (isMultiSelection(aFromData))
         selectionCount = getSelectionCount(aFromData);
      else if (isMultiSelection(aToData))
         selectionCount = getSelectionCount(aToData);
      throw new AeSelectionFailureException(aCopyOperation.getContext().getBPELNamespace(), selectionCount);
   }
   
   /**
    * Returns true if the given data is a sized object (List or NodeList) with size greater than 1.
    * 
    * @param aData
    */
   protected boolean isMultiSelection(Object aData)
   {
      if (aData instanceof List)
      {
         return ((List) aData).size() > 1;
      }
      else
      {
         return false;
      }
   }
   
   /**
    * Returns the selection count of the given sized object (either a List or a NodeList).
    * 
    * @param aData
    */
   protected int getSelectionCount(Object aData)
   {
      if (aData instanceof List)
      {
         return ((List) aData).size();
      }
      else
      {
         return 0;
      }
   }
}
 