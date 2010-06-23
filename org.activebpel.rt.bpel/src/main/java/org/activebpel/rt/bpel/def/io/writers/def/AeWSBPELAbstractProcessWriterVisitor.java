//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/writers/def/AeWSBPELAbstractProcessWriterVisitor.java,v 1.4 2008/03/11 14:47:08 JPerrotto Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.writers.def;

import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.xml.IAeMutableNamespaceContext;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.w3c.dom.Element;

/**
 * A WS-BPEL 2.0  Abstract Process implementation of a writer visitor.
 */
public class AeWSBPELAbstractProcessWriterVisitor extends AeWSBPELWriterVisitor
{

   /**
    * Constructs a ws-bpel 2.0 abstract process writer visitor.
    *
    * @param aDef
    * @param aParentElement
    * @param aNamespace
    * @param aTagName
    * @param aWritePortTypeAttrib indicates the portType attribute should be written for the WSIO Activities. 
    */
   public AeWSBPELAbstractProcessWriterVisitor(AeBaseXmlDef aDef, Element aParentElement, String aNamespace, String aTagName,
                                               boolean aWritePortTypeAttrib)
   {
      super(aDef, aParentElement, aNamespace, aTagName, aWritePortTypeAttrib);
   }

   /**
    * Overrides method to write the attribute in the default (abstract process) namespace.
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWSBPELWriterVisitor#writeAbstractProcessProfileAttribute(org.activebpel.rt.bpel.def.AeProcessDef, org.activebpel.rt.xml.IAeMutableNamespaceContext)
    */
   protected void writeAbstractProcessProfileAttribute(AeProcessDef aDef, IAeMutableNamespaceContext aNsContext)
   {
      setAttribute(IAeBPELConstants.TAG_ABSTRACT_PROCESS_PROFILE, aDef.getAbstractProcessProfile());
   }
}
