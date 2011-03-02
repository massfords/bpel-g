//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeWSBPELAssignExtObjVisitor.java,v 1.2 2007/10/15 21:42:33 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;
import org.activebpel.rt.xml.def.io.IAeExtensionRegistry;

/**
 * A def visitor that will consult extension registry to see if a extension
 * object is available for this extension def and will assign it to the
 * extension def
 */
public class AeWSBPELAssignExtObjVisitor extends AeAbstractDefVisitor
{
   /** Extension registry. */
   private IAeExtensionRegistry mExtensionRegistry;

   /**
    * C'tor
    * @param aExtensionRegistry
    */
   public AeWSBPELAssignExtObjVisitor(IAeExtensionRegistry aExtensionRegistry)
   {
      setTraversalVisitor( new AeTraversalVisitor(new AeDefTraverser(), this));
      setExtensionRegistry(aExtensionRegistry);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef)
    */
   public void visit(AeChildExtensionActivityDef aDef)
   {
      if (mExtensionRegistry != null)
         aDef.setExtensionObject(mExtensionRegistry.getExtensionObject(aDef.getElementName()));
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.AeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionAttributeDef)
    */
   public void visit(AeExtensionAttributeDef aDef)
   {
      if (mExtensionRegistry != null)
         aDef.setExtensionObject(mExtensionRegistry.getExtensionObject(aDef.getQName()));
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.AeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionElementDef)
    */
   public void visit(AeExtensionElementDef aDef)
   {
      if (mExtensionRegistry != null)
         aDef.setExtensionObject(mExtensionRegistry.getExtensionObject(aDef.getElementQName()));
      super.visit(aDef);
   }

   /**
    * @return the extensionRegistry
    */
   protected IAeExtensionRegistry getExtensionRegistry()
   {
      return mExtensionRegistry;
   }

   /**
    * @param aExtensionRegistry the extensionRegistry to set
    */
   protected void setExtensionRegistry(IAeExtensionRegistry aExtensionRegistry)
   {
      mExtensionRegistry = aExtensionRegistry;
   }
}
