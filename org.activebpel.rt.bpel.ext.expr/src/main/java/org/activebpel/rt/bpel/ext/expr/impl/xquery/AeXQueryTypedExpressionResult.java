//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/impl/xquery/AeXQueryTypedExpressionResult.java,v 1.4 2007/08/21 14:59:20 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.impl.xquery;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A helper class for creating a List of objects from the result of a Saxon XQuery.
 */
public class AeXQueryTypedExpressionResult
{
   private static QName RESULT_TEXT_QNAME = new QName("http://saxon.sf.net/xquery-results", "text"); //$NON-NLS-1$ //$NON-NLS-2$
   
   /**
    * Creates a List of AeXQueryTypedExpressionResult objects from the given result Document.  The
    * result Document is obtained by executing a XQuery using Saxon.
    * 
    * @param aResultDoc
    */
   public static List createResultList(Document aResultDoc)
   {
      List rval = new ArrayList();
      
      Element rootElem = aResultDoc.getDocumentElement();
      NodeList children = rootElem.getChildNodes();
      for (int i = 0; i < children.getLength(); i++)
      {
         Node child = children.item(i);
         if (child instanceof Element)
         {
            Element elem = (Element) child;
            Object obj = null;
            if ("element".equals(elem.getLocalName()) || "document".equals(elem.getLocalName())) //$NON-NLS-1$ //$NON-NLS-2$
            {
               obj = AeXmlUtil.cloneElement(AeXmlUtil.getFirstSubElement(elem));
            }
            else if ("atomic-value".equals(elem.getLocalName())) //$NON-NLS-1$
            {
               String str = AeXmlUtil.getText(elem);
               QName type = AeXmlUtil.getXSIType(elem);
               obj = new AeTypeMapping().deserialize(type, str);
            }
            else if (AeUtil.compareObjects(RESULT_TEXT_QNAME.getNamespaceURI(), elem.getNamespaceURI()) &&
                  AeUtil.compareObjects(RESULT_TEXT_QNAME.getLocalPart(), elem.getLocalName()))
            {
               obj = AeXmlUtil.getText(elem);
            }
            else
            {
               throw new RuntimeException("Error: unknown Saxon return type."); //$NON-NLS-1$
            }
            rval.add(obj);
         }
      }
      
      return rval;
   }
   
   /**
    * Private c'tor.
    */
   private AeXQueryTypedExpressionResult()
   {
   }
}
