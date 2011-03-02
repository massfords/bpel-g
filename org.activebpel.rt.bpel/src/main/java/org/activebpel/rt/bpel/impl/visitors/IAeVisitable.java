// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/visitors/IAeVisitable.java,v 1.3 2004/07/08 13:10:02 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.visitors;

import org.activebpel.rt.bpel.AeBusinessProcessException;

/**
 * Interface to define a visitable class.
 * 
 */
public interface IAeVisitable
{
   /**
    * Accept a visitor.
    * This method should NOT traverse children.
    * 
    * @param aVisitor The visitor to accept.
    */
   public void accept( IAeImplVisitor aVisitor ) throws AeBusinessProcessException;
}
