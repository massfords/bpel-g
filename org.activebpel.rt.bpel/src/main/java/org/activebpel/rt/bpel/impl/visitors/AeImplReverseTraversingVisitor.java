// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/visitors/AeImplReverseTraversingVisitor.java,v 1.2 2007/11/21 03:22:17 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.visitors;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;

/**
 * A visitor that traverses a BPEL implementation object tree.
 */
public class AeImplReverseTraversingVisitor extends AeImplTraversingVisitor
{
   /**
    * Traverses the specified implementation object's children, if any.
    *
    * @param aImpl The implementation object to traverse.
    */
   protected void visitBase(AeAbstractBpelObject aImpl) throws AeBusinessProcessException
   {
      if (aImpl.getParent() != null && aImpl.getParent() instanceof IAeVisitable)
         ((IAeVisitable) aImpl.getParent()).accept(this);
   }
}
