// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDefExtensionFinderVisitor.java,v 1.4 2007/11/13 17:02:04 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * This visitor will look for extension elements/attributes.  If any are found
 */
public class AeDefExtensionFinderVisitor extends AeAbstractSearchVisitor
{
   /** Flag indicating whether an extension was found while visiting. */
   private boolean mExtensionFound;
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractSearchVisitor#isFound()
    */
   public boolean isFound()
   {
      return mExtensionFound;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#traverse(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   protected void traverse(AeBaseXmlDef aDef)
   {
      mExtensionFound = aDef.getExtensionAttributeDefs().iterator().hasNext() || aDef.getExtensionElementDefs().iterator().hasNext();

      super.traverse(aDef);
   }
}
