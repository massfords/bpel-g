//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/from/AeFromLiteral.java,v 1.3 2006/07/14 15:46:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.from; 

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handles getting the literal value. 
 */
public class AeFromLiteral extends AeFromBase
{
   /** literal node */
   private Node mLiteral;
   
   /**
    * Ctor accepts def
    * 
    * @param aFromDef
    */
   public AeFromLiteral(AeFromDef aFromDef)
   {
      this(aFromDef.getLiteral());
   }
   
   /**
    * Ctor accepts node
    * @param aNode
    */
   public AeFromLiteral(Node aNode)
   {
      setLiteral(aNode);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeFrom#getFromData()
    */
   public Object getFromData() throws AeBpelException
   {
      Object data = null;
      // Need to synchronize on the document fragment we are copying, or bad things can happen under load
      synchronized(getLiteral())
      {
         Document doc = AeXmlUtil.newDocument();
         Node node = doc.importNode(getLiteral(), true);
         if (node instanceof Element)
         {
            Map namespaceMap = new HashMap();
            AeXmlUtil.getDeclaredNamespaces((Element)getLiteral(), namespaceMap);
            AeXmlUtil.declareNamespacePrefixes((Element)node, namespaceMap);

            doc.appendChild(node);
            data = doc.getDocumentElement();
         }
         else
         {
            data = node;
         }
      }
      return data;
   }

   /**
    * @return Returns the literal.
    */
   public Node getLiteral()
   {
      return mLiteral;
   }

   /**
    * @param aLiteral The literal to set.
    */
   public void setLiteral(Node aLiteral)
   {
      mLiteral = aLiteral;
   }

}
 