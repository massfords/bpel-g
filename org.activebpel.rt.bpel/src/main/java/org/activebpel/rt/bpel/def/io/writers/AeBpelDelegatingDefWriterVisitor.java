// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/writers/AeBpelDelegatingDefWriterVisitor.java,v 1.3 2008/02/17 21:37:11 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.writers;

import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefTraverser;
import org.activebpel.rt.bpel.def.visitors.AeTraversalVisitor;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.io.IAeDefRegistry;
import org.activebpel.rt.xml.def.io.writers.AeBaseXmlDefElementCreator;
import org.w3c.dom.Document;

/**
 * Work horse class for traversing and serializing the AeProcessDef
 * object model.
 * <br />
 * The traversal mechanism is driven an AeDefTraverser subclass, the
 * <code>AeInvokeWithScopeTraverser</code>. 
 */
public class AeBpelDelegatingDefWriterVisitor extends AeAbstractDefVisitor implements IAeBPELConstants
{
   private AeBaseXmlDefElementCreator mCreator;
   
   /**
    * Constructor.
    * @param aRegistry to retrieve IAeBpelDefWriter impls
    */
   public AeBpelDelegatingDefWriterVisitor(IAeDefRegistry aRegistry)
   {
      init();
      setCreator(new AeBaseXmlDefElementCreator(aRegistry, getTraversalVisitor()));
   }
   
   /**
    * Install the appropriate traversal visitor
    * <br />
    * Override this method to change the traversal behavior.
    */
   protected void init()
   {
      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
   }
   
   /**
    * Accessor for the serialized process.
    * @return the BPEL xml
    */
   public Document getProcessDoc()
   {
      return getCreator().getDocument();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#traverse(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   protected void traverse(AeBaseXmlDef aDef)
   {
      // call the traverse method with the current node from the stack which serves as the parent element
      // for the element we're going to create for this def.
      getCreator().createElementAndTraverse(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeScopeDef)
    */
   public void visit(AeScopeDef aDef)
   {
      // there is no xml to write for a scope def so we do a super.traverse() here.
      super.traverse(aDef);
   }

   /**
    * @return the creator
    */
   public AeBaseXmlDefElementCreator getCreator()
   {
      return mCreator;
   }

   /**
    * @param aCreator the creator to set
    */
   public void setCreator(AeBaseXmlDefElementCreator aCreator)
   {
      mCreator = aCreator;
   }
}
