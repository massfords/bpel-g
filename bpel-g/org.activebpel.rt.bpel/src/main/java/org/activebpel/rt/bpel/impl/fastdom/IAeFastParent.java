// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/fastdom/IAeFastParent.java,v 1.1 2004/09/07 22:08:24 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.fastdom;

import java.util.List;

/**
 * Defines the interface for nodes that can be parents of other nodes.
 */
public interface IAeFastParent
{
   /**
    * Appends the specified child node to this node's child nodes.
    */
   public void appendChild(AeFastNode aChild);

   /**
    * Returns <code>List</code> of this nodes's child nodes.
    */
   public List getChildNodes();

   /**
    * Removes the specified node from this node's child nodes.
    *
    * @param aChild
    * @return <code>true</code> if and only if the removal occurred.
    */
   public boolean removeChild(AeFastNode aChild);
}
