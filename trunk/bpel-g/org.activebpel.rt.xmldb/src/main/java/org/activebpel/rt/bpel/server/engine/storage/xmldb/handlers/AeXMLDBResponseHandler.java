//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/handlers/AeXMLDBResponseHandler.java,v 1.3 2008/02/17 21:59:05 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers;

import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeCommonElements;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse;
import org.activebpel.rt.util.AeIntSet;
import org.activebpel.rt.util.AeLongSet;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An abstract class for XMLDB response handlers.
 */
public abstract class AeXMLDBResponseHandler implements IAeXMLDBResponseHandler
{
   /** A response handler that returns nothing (null). */
   public static final IAeXMLDBResponseHandler NULL_XMLDB_RESPONSE_HANDLER = new IAeXMLDBResponseHandler()
   {
      /**
       * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler#handleResponse(org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse)
       */
      public Object handleResponse(IAeXMLDBXQueryResponse aResponse) throws AeXMLDBException
      {
         return null;
      }
   };

   /** A response handler that returns a String. */
   public static IAeXMLDBResponseHandler STRING_RESPONSE_HANDLER = new AeXMLDBSingleObjectResponseHandler()
      {
         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler#handleElement(org.w3c.dom.Element)
          */
         protected Object handleElement(Element aElement) throws AeXMLDBException
         {
            String str = AeXmlUtil.getText(aElement);
            if (AeUtil.isNullOrEmpty(str))
               return null;
            else
               return str;
         }
      };

   /** A response handler that returns a Date. */
   public static IAeXMLDBResponseHandler DATE_RESPONSE_HANDLER = new AeXMLDBSingleObjectResponseHandler()
      {
         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler#handleElement(org.w3c.dom.Element)
          */
         protected Object handleElement(Element aElement) throws AeXMLDBException
         {
            String str = AeXmlUtil.getText(aElement);
            if (AeUtil.isNullOrEmpty(str))
               return null;
            else
            {
               return new AeSchemaDateTime(str).toDate();
            }
         }
      };

   /** A response handler that returns an array of Strings. */
   public static IAeXMLDBResponseHandler STRINGARRAY_RESPONSE_HANDLER = new AeXMLDBArrayResponseHandler()
      {
         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBArrayResponseHandler#convertToArray(java.util.List)
          */
         protected Object convertToArray(List aList)
         {
            String [] rval = new String[aList.size()];
            return (String []) aList.toArray(rval);
         }

         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler#handleElement(org.w3c.dom.Element)
          */
         protected Object handleElement(Element aElement)
         {
            String str = AeXmlUtil.getText(aElement);
            if (AeUtil.isNullOrEmpty(str))
            {
               return null;
            }
            else
            {
               return str;
            }
         }
      };
      
   /** A response handler that returns a Long. */
   public static IAeXMLDBResponseHandler LONG_RESPONSE_HANDLER = new AeXMLDBSingleObjectResponseHandler()
      {
         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler#handleElement(org.w3c.dom.Element)
          */
         protected Object handleElement(Element aElement) throws AeXMLDBException
         {
            String str = AeXmlUtil.getText(aElement);
            if (AeUtil.isNullOrEmpty(str))
               return null;
            else
               return new Long(str);
         }
      };

   /** A response handler that returns an Integer. */
   public static IAeXMLDBResponseHandler INTEGER_RESPONSE_HANDLER = new AeXMLDBSingleObjectResponseHandler()
      {
         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler#handleElement(org.w3c.dom.Element)
          */
         protected Object handleElement(Element aElement) throws AeXMLDBException
         {
            String str = AeXmlUtil.getText(aElement);
            if (AeUtil.isNullOrEmpty(str))
               return null;
            else
               return new Integer(str);
         }
      };

   /** A response handler that returns a Boolean. */
   public static IAeXMLDBResponseHandler BOOLEAN_RESPONSE_HANDLER = new AeXMLDBSingleObjectResponseHandler()
      {
         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler#handleElement(org.w3c.dom.Element)
          */
         protected Object handleElement(Element aElement) throws AeXMLDBException
         {
            String str = AeXmlUtil.getText(aElement);
            if (AeUtil.isNullOrEmpty(str))
               return null;
            else
               return new Boolean(str);
         }
      };

   /** A response handler that returns a QName. */
   public static IAeXMLDBResponseHandler QNAME_RESPONSE_HANDLER = new AeXMLDBSingleObjectResponseHandler()
      {
         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler#handleElement(org.w3c.dom.Element)
          */
         protected Object handleElement(Element aElement) throws AeXMLDBException
         {
            return getQName(aElement);
         }
      };

   /** A response handler that returns a AeLongSet. */
   public static IAeXMLDBResponseHandler LONGSET_RESPONSE_HANDLER = new AeXMLDBListResponseHandler()
      {
         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler#handleElement(org.w3c.dom.Element)
          */
         protected Object handleElement(Element aElement) throws AeXMLDBException
         {
            String str = AeXmlUtil.getText(aElement);
            if (AeUtil.isNullOrEmpty(str))
            {
               str = null;
            }
            return new Long(str);
         }
   
         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBCollectionResponseHandler#handleResponse(org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse)
          */
         public Object handleResponse(IAeXMLDBXQueryResponse aResponse) throws AeXMLDBException
         {
            List list = (List) super.handleResponse(aResponse);
            AeLongSet set = new AeLongSet();
            set.addAll(list);
            return set;
         }
      };

   /** A response handler that returns a AeIntSet. */
   public static IAeXMLDBResponseHandler INTSET_RESPONSE_HANDLER = new AeXMLDBListResponseHandler()
      {
         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler#handleElement(org.w3c.dom.Element)
          */
         protected Object handleElement(Element aElement) throws AeXMLDBException
         {
            String str = AeXmlUtil.getText(aElement);
            if (AeUtil.isNullOrEmpty(str))
            {
               str = null;
            }
            return new Integer(str);
         }

         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBCollectionResponseHandler#handleResponse(org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse)
          */
         public Object handleResponse(IAeXMLDBXQueryResponse aResponse) throws AeXMLDBException
         {
            List list = (List) super.handleResponse(aResponse);
            AeIntSet set = new AeIntSet();
            set.addAll(list);
            return set;
         }
      };

   /** A response handler that returns a AeIntSet. */
   public static IAeXMLDBResponseHandler INTMAP_RESPONSE_HANDLER = new AeXMLDBMapResponseHandler()
      {
         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBMapResponseHandler#getKey(org.w3c.dom.Element)
          */
         protected Object getKey(Element aElement) throws AeXMLDBException
         {
            return getIntFromElement(aElement, "Key"); //$NON-NLS-1$
         }
         
         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBMapResponseHandler#getValue(org.w3c.dom.Element)
          */
         protected Object getValue(Element aElement) throws AeXMLDBException
         {
            return getIntFromElement(aElement, "Value"); //$NON-NLS-1$
         }
      };

   /** A response handler that returns a Document. */
   public static IAeXMLDBResponseHandler DOCUMENT_RESPONSE_HANDLER = new AeXMLDBSingleObjectResponseHandler()
      {
         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler#handleElement(org.w3c.dom.Element)
          */
         protected Object handleElement(Element aElement) throws AeXMLDBException
         {
            Document doc = null;

            Element elem = AeXmlUtil.getFirstSubElement(aElement);
            if (elem != null)
            {
               doc = AeXmlUtil.newDocument();
               elem = (Element) doc.importNode(elem, true);
               doc.appendChild(elem);
            }

            return doc;
         }
      };

   /** A response handler that returns a Document. */
   public static IAeXMLDBResponseHandler ELEMENT_LIST_RESPONSE_HANDLER = new AeXMLDBListResponseHandler()
      {
         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler#handleElement(org.w3c.dom.Element)
          */
         protected Object handleElement(Element aElement) throws AeXMLDBException
         {
            return aElement;
         }
      };

   /**
    * Gets a long value from the doc instance at the given Element name.  The doc instance must have a 
    * child Element with the given name and it must be of type xsd:long or similar.  Returns null if
    * the value is missing or empty.
    * 
    * @param aDocInstance
    * @param aElementName
    */
   public static Long getLongFromElement(Element aDocInstance, String aElementName)
   {
      String val = getStringFromElement(aDocInstance, aElementName);
      if (val == null)
      {
         return null;
      }
      else
      {
         return new Long(val);
      }
   }

   /**
    * Gets an Integer value from the doc instance at the given Element name.  The doc instance must have a 
    * child Element with the given name and it must be of type xsd:int or similar.  Returns null
    * if the value is not found or empty.
    * 
    * @param aDocInstance
    * @param aElementName
    */
   public static Integer getIntFromElement(Element aDocInstance, String aElementName)
   {
      String val = getStringFromElement(aDocInstance, aElementName);
      if (val == null)
      {
         return null;
      }
      else
      {
         return new Integer(val);
      }
   }

   /**
    * Gets a Float value from the doc instance at the given Element name.  The doc instance must have a 
    * child Element with the given name and it must be of type xsd:float or similar.  Returns null
    * if the value is not found or empty.
    * 
    * @param aDocInstance
    * @param aElementName
    */
   public static Float getFloatFromElement(Element aDocInstance, String aElementName)
   {
      String val = getStringFromElement(aDocInstance, aElementName);
      if (val == null)
      {
         return null;
      }
      else
      {
         return new Float(val);
      }
   }

   /**
    * Gets a Double value from the doc instance at the given Element name.  The doc instance must have a 
    * child Element with the given name and it must be of type xsd:double or similar.  Returns null
    * if the value is not found or empty.
    * 
    * @param aDocInstance
    * @param aElementName
    */
   public static Double getDoubleFromElement(Element aDocInstance, String aElementName)
   {
      String val = getStringFromElement(aDocInstance, aElementName);
      if (val == null)
      {
         return null;
      }
      else
      {
         return new Double(val);
      }
   }

   /**
    * Gets a int value from the doc instance at the given Element name. The doc instance must have a child
    * Element with the given name and it must be of type xsd:boolean.  Returns null if the value is not found
    * or empty.
    * 
    * @param aDocInstance
    * @param aElementName
    */
   public static Boolean getBoolFromElement(Element aDocInstance, String aElementName)
   {
      String val = getStringFromElement(aDocInstance, aElementName);
      if (val == null)
      {
         return null;
      }
      else
      {
         return Boolean.valueOf(val);
      }
   }

   /**
    * Gets an Element from the doc instance.  The typical use of this is when XML fragments are stored as 
    * Elements in a doc type, such as is found in AeVariable or AeQueuedReceive.  In these cases, the
    * Element must have a single child Element which is the XML fragment.
    * 
    * @param aDocInstance
    * @param aElementName
    */
   public static Element getElementFromElement(Element aDocInstance, String aElementName)
   {
      Element elem = getElement(aDocInstance, aElementName);
      if (elem == null)
      {
         return null;
      }
      else
      {
         return AeXmlUtil.getFirstSubElement(elem);
      }
   }

   /**
    * Gets the doc instance's sub element with the given name.  If the sub element is missing
    * or is flagged with the 'ae-null' attribute (indicating a NULL value) then this method
    * returns null.  Otherwise it returns the element with the given name.
    * 
    * @param aDocInstance
    * @param aElementName
    */
   protected static Element getElement(Element aDocInstance, String aElementName)
   {
      NodeList nl = aDocInstance.getChildNodes();
      int length = nl.getLength();
      if (nl != null && length > 0)
      {
         for (int i = 0; i < length; i++)
         {
            Node node = nl.item(i);
            if (node instanceof Element)
            {
               Element element = (Element) node;
               if (AeUtil.compareObjects(aElementName, element.getLocalName()))
               {
                  if (!"true".equals(element.getAttribute(IAeCommonElements.AE_NULL_ATTR))) //$NON-NLS-1$
                  {
                     return element;
                  }
               }
            }
         }
      }
      
      return null;
   }
   
   /**
    * Gets a String value from the doc instance at the given Element name.  The doc instance must have a 
    * child Element with the given name and it must be of type xsd:string or similar.  Returns null
    * if the value is not found or empty.
    * 
    * @param aDocInstance
    * @param aElementName
    */
   public static String getStringFromElement(Element aDocInstance, String aElementName)
   {
      Element elem = getElement(aDocInstance, aElementName);
      if (elem == null)
      {
         return null;
      }
      else
      {
         return AeXmlUtil.getText(elem);
      }
   }

   /**
    * Gets a String value from the doc instance at the given Element name.  The doc instance must have a 
    * child Element with the given name and it must be of type ae:AeAny.  Returns null
    * if the value is not found or empty.
    * 
    * @param aDocInstance
    * @param aElementName
    */
   public static String getSerializedDocumentFromElement(Element aDocInstance, String aElementName)
   {
      Element elem = getElementFromElement(aDocInstance, aElementName);
      if (elem == null)
      {
         return null;
      }
      else
      {
         return AeXmlUtil.serialize(elem);
      }
   }

   /**
    * Gets a Date value from the doc instance at the given Element name.  The doc instance must have a 
    * child Element with the given name and it must be of type xsd:dateTime or similar.  Returns null if the
    * value is not present or empty.
    * 
    * @param aDocInstance
    * @param aElementName
    */
   public static Date getDateTimeFromElement(Element aDocInstance, String aElementName)
   {
      String val = getStringFromElement(aDocInstance, aElementName);
      if (val == null)
      {
         return null;
      }
      else
      {
         return new AeSchemaDateTime(val).toDate();
      }
   }

   /**
    * Gets a QName value from the doc instance at the given Element name.  The doc instance must have a 
    * child Element with the given name and that child element must be a complex type of the type 
    * tns:AeQName (as found in the ActiveBPEL XMLDB schema).  If the value is not present or is empty,
    * then null is returned.
    * 
    * @param aDocInstance
    * @param aElementName
    */
   public static QName getQNameFromElement(Element aDocInstance, String aElementName)
   {
      Element qnameElem = getElement(aDocInstance, aElementName);
      return getQName(qnameElem);
   }

   /**
    * Gets a QName from the given Element.  The element is assumed to be of type tns:AeQName as
    * defined in the ActiveBPEL XMLDB schema.
    * 
    * @param aElement
    */
   public static QName getQName(Element aElement)
   {
      if (aElement == null)
      {
         return null;
      }
      else
      {
         String ns = getStringFromElement(aElement, IAeCommonElements.NAMESPACE);
         String lp = getStringFromElement(aElement, IAeCommonElements.LOCAL_PART);
         return new QName(ns, lp);
      }
   }
   
   /**
    * Gets a Document value from the doc instance at the given Element name.  The doc instance must have
    * a child Element with the given name and it must be of type AeAny.  The named element must have a single
    * child element that is the root of the document fragment.  Returns null if the value is not found.
    * 
    * @param aDocInstance
    * @param aElementName
    */
   public static Document getDocumentFromElement(Element aDocInstance, String aElementName)
   {
      Element elem = getElementFromElement(aDocInstance, aElementName);
      return createDocument(elem);
   }

   /**
    * Creates a Document from an Element.
    * 
    * @param aElement
    */
   public static Document createDocument(Element aElement)
   {
      Document rval = null;

      if (aElement != null)
      {
         Document doc = AeXmlUtil.newDocument();
         aElement = (Element) doc.importNode(aElement, true);
         doc.appendChild(aElement);
         
         rval = doc;
      }

      return rval;
   }
}
