// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.activity.support;

import java.util.ArrayList;
import java.util.List;

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Models the new literal bpel construct wrapper introduced in WS-BPEL 2.0.
 */
public class AeLiteralDef extends AeBaseDef
{
   /** The list of child nodes in the literal. */
   private List mChildNodes = new ArrayList();

   /**
    * Default c'tor.
    */
   public AeLiteralDef()
   {
      super();
   }

   /**
    * The literal can only have a single child node but we're allowing for multiple here in order to preserve any
    * extra child nodes that we may have read in. We'll produce an error message for multiple children during validation.
    * @return Returns the childNodes.
    */
   public List getChildNodes()
   {
      return mChildNodes;
   }

   /**
    * Adds a child node to the list of child nodes.
    * 
    * @param aNode
    */
   public void addChildNode(Node aNode)
   {
      if (aNode instanceof Element)
      {
         mChildNodes.add(AeXmlUtil.cloneElement((Element) aNode));
      }
      else
      {
         mChildNodes.add(aNode.cloneNode(true));
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
