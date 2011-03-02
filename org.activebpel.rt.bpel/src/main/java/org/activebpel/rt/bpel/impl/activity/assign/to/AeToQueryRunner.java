//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeToQueryRunner.java,v 1.5.4.1 2008/04/21 16:09:44 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.to; 

import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeFaultFactory;
import org.activebpel.rt.bpel.impl.AeSelectionFailureException;
import org.activebpel.rt.bpel.impl.activity.assign.AeCreateXPathUtil;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Helper class that selects a node from an element. This element will either be a message part
 * or an element variable. This behavior exists in BPEL 1.1 only since the 2.0 spec dropped the 
 * syntax for a query on a &lt;to&gt; in favor of using the new expression syntax. 
 */
public class AeToQueryRunner
{
   /**
    * Selects a node from an element within a message part or element variable using a query.
    * If the query fails and createXPath is turned on then we'll create the nodes required to
    * fulfill the query.
    * 
    * @param aCopyOperation
    * @param aQuery
    * @param targetDocElement
    */
   public static Node selectValue(IAeCopyOperation aCopyOperation, String aQuery, Element targetDocElement) throws AeBpelException
   {
      try
      {
         Node targetNode = null;
         
         // if we have a document element then search for the path
         if (targetDocElement != null)
         {
            // Locate the proper node from the query
            Object selection = aCopyOperation.getContext().executeQuery(aQuery, targetDocElement, true);
            if (selection instanceof List && ((List) selection).size() == 1)
               targetNode = (Node) ((List) selection).get(0);
            else if (selection instanceof List && ((List) selection).size() > 1)
               throw new AeSelectionFailureException(aCopyOperation.getContext().getBPELNamespace(),((List) selection).size());
         }
   
         // if we didn't find the node and the config says create then create it
         if (targetNode == null && aCopyOperation.getContext().isCreateXPathAllowed())
            targetNode = AeCreateXPathUtil.findOrCreateXPath(aQuery, targetDocElement.getOwnerDocument(), aCopyOperation.getContext(), aCopyOperation.getContext());
         return targetNode;
      }
      catch(AeBpelException e)
      {
         throw e;
      }
      catch(AeBusinessProcessException e)
      {
         throw new AeBpelException(e.getMessage(), AeFaultFactory.getSystemErrorFault(e));
      }
   }
}
 