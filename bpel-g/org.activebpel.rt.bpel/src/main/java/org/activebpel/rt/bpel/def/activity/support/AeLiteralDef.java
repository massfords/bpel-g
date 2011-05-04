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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Models the new literal bpel construct wrapper introduced in WS-BPEL 2.0.
 */
public class AeLiteralDef extends AeBaseDef
{
   /**
     * 
     */
    private static final long serialVersionUID = 380062235992130912L;
/** The list of child nodes in the literal. */
   private transient List<Node> mChildNodes = new ArrayList();

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
   public List<Node> getChildNodes()
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

   private void writeObject(java.io.ObjectOutputStream out)
   throws IOException {
       Document doc = AeXmlUtil.newDocument();
       Element e = AeXmlUtil.addElementNS(doc, IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI, "literal");
       for (Node node : mChildNodes ) {
           Node n = doc.importNode(node, true);
           e.appendChild(n);
       }
       String xml = AeXMLParserBase.documentToString(doc);
       out.writeUTF(xml);
   }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        String xml = in.readUTF();
        try {
            Document doc = new AeXMLParserBase(true,false).loadDocumentFromString(xml, null);
            mChildNodes = new ArrayList();
            NodeList childNodes = doc.getDocumentElement().getChildNodes();
            for(int i=0; i<childNodes.getLength(); i++) {
                addChildNode(childNodes.item(i));
            }
        } catch (AeException e) {
            throw new IOException(e);
        }
    }
}
