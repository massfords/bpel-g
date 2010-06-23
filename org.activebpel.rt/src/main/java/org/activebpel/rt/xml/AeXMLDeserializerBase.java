//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/AeXMLDeserializerBase.java,v 1.2 2008/02/17 21:09:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Base class used in create xml element deserializers. Implementations of this
 * classes should be designed to be re-entrant.
 */
public abstract class AeXMLDeserializerBase
{
   /** Namespace map. */
   private Map mNamespaceMap;

   /** Root element. */
   private Element mElement;

   /**
    * @return the element
    */
   protected Element getElement()
   {
      return mElement;
   }

   /**
    * @param aElement the element to set
    */
   protected void setElement(Element aElement)
   {
      mElement = aElement;
   }

   /**
    * Returns the document namespace map.
    */
   protected Map getNamespaceMap()
   {
      if (mNamespaceMap == null)
      {
         mNamespaceMap = new HashMap();
         initNamespaceMap(mNamespaceMap);
      }
      return mNamespaceMap;
   }

   /**
    * Initializes. Subclasses should override this method to set the mapping.
    * @param aMap prefix to namespace map.
    */
   protected void initNamespaceMap(Map aMap)
   {
   }

   /**
    * Returns the text value for the given xpath.
    * @param aNode
    * @param aXPath
    */
   protected String getText(Node aNode, String aXPath)
   {
      return AeXPathUtil.selectText(aNode, aXPath, getNamespaceMap());
   }

   /**
    * Returns a list of strings of text nodes identified by the give xpath.
    * @param aNode
    * @param aXPath
    * @return list of strings for each node or empty list otherwise.
    */
   protected List getTextList(Node aNode, String aXPath)
   {
      return AeXPathUtil.selectTextList(aNode, aXPath, getNamespaceMap());
   }

   /**
    * Returns schema date time.
    * @param aNode
    * @param aXPath
    */
   protected AeSchemaDateTime getDateTime(Node aNode, String aXPath)
   {
      return AeXPathUtil.selectDateTime(aNode, aXPath, getNamespaceMap());
   }
   
   /**
    * Selects and returns a single element.
    * @param aNode
    * @param aXPath
    * @throws AeException
    */
   protected Element selectElement(Node aNode, String aXPath) throws AeException
   {
      return (Element) AeXPathUtil.selectSingleNode(aNode, aXPath, getNamespaceMap());
   }

}
