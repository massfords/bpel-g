//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/copy/AeEmptySelectionStrategy.java,v 1.1.16.1 2008/04/21 16:09:43 ppatruni Exp $
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
import org.activebpel.rt.bpel.impl.AeSelectionFailureException;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyStrategy;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 * Selecting a null value in a &lt;from&gt; typically results in a bpel:selectionFailure.
 * This class is used to report that error UNLESS the emptyQuerySelection config
 * is enabled at which point it removes the target node.
 */
public class AeEmptySelectionStrategy implements IAeCopyStrategy
{
   /**
    * Either removes the target node if emptyQuerySelection is allowed or reports bpel:selectionFailure
    * 
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyStrategy#copy(org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation, java.lang.Object, java.lang.Object)
    */
   public void copy(IAeCopyOperation aCopyOperation, Object aFromData, Object aToData) throws AeBpelException
   {
      boolean removed = false;
      if (aCopyOperation.getContext().isEmptyQuerySelectionAllowed())
      {
         if (aToData instanceof Attr)
         {
            Attr attr = (Attr) aToData;
            attr.getOwnerElement().removeAttributeNode(attr);
            removed = true;
         }
         else if (aToData instanceof Node)
         {
            Node node = (Node) aToData;
            node.getParentNode().removeChild(node);
            removed = true;
         }
      }
      
      if (!removed)
         throw new AeSelectionFailureException(aCopyOperation.getContext().getBPELNamespace(), 0);
   }
}
 