// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/visitors/AeImplTraversingVisitor.java,v 1.16 2007/11/21 03:22:17 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.visitors;

import java.util.Iterator;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.activity.AeActivityImpl;
import org.activebpel.rt.bpel.impl.activity.support.AeLink;

/**
 * A visitor that traverses a BPEL implementation object tree.
 */
public class AeImplTraversingVisitor extends AeAbstractImplVisitor
{
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeAbstractImplVisitor#visitBase(org.activebpel.rt.bpel.impl.AeAbstractBpelObject)
    */
   protected void visitBase(AeAbstractBpelObject aImpl) throws AeBusinessProcessException
   {
      if (aImpl instanceof AeActivityImpl)
      {
         for (Iterator i = ((AeActivityImpl) aImpl).getSourceLinks(); i.hasNext(); )
         {
            AeLink link = (AeLink) i.next();
            link.accept(this);
         }
      }

      for (Iterator i = aImpl.getChildrenForStateChange(); i.hasNext(); )
      {
         AeAbstractBpelObject child = (AeAbstractBpelObject) i.next();
         child.accept(this);
      }
   }
}
