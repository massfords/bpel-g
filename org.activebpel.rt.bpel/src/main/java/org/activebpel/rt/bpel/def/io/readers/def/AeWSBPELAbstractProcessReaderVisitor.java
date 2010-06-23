//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/readers/def/AeWSBPELAbstractProcessReaderVisitor.java,v 1.1 2006/10/24 21:23:56 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.readers.def;

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.w3c.dom.Element;

/**
 * Implements a WS-BPEL 2.0 abstract process version of the def reader visitor.
 */
public class AeWSBPELAbstractProcessReaderVisitor extends AeWSBPELReaderVisitor
{
   /**
    * Constructor.
    */
   public AeWSBPELAbstractProcessReaderVisitor(AeBaseDef aParentDef, Element aElement)
   {
      super(aParentDef, aElement);
   }

   /**
    * Overrides method to read abstract process profile attribute.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      super.visit(aDef);
      aDef.setAbstractProcessProfile(getAttribute(TAG_ABSTRACT_PROCESS_PROFILE));
   }
}
