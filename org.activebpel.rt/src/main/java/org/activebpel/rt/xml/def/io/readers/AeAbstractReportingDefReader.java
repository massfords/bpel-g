//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/readers/AeAbstractReportingDefReader.java,v 1.5 2007/11/28 17:18:43 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.io.readers; 

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Base class for readers that will report errors relating to reading an element
 * into a def object.
 * 
 * This impl tracks each attribute that was read so any others will result in
 * extension attributes.
 */
public abstract class AeAbstractReportingDefReader implements IAeReportingDefReader
{
   /** list of errors encountered during parse */
   private List mErrors;
   /** A set of consumed attributes. */
   private Set mConsumedAttributes = new HashSet();
   /** current element being read */
   private Element mCurrentElement;
   /** Parent def object */
   private AeBaseXmlDef mParentDef;
   
   /**
    * ctor
    * @param aParentDef
    * @param aElement
    */
   public AeAbstractReportingDefReader(AeBaseXmlDef aParentDef, Element aElement)
   {
      mParentDef = aParentDef;
      mCurrentElement = aElement;
   }

   /**
    * @see org.activebpel.rt.xml.def.io.readers.IAeReportingDefReader#hasErrors()
    */
   public boolean hasErrors()
   {
      return mErrors != null;
   }

   /**
    * @see org.activebpel.rt.xml.def.io.readers.IAeReportingDefReader#getErrors()
    */
   public List getErrors()
   {
      if (mErrors == null)
         mErrors = new ArrayList();
      return mErrors;
   }

   /**
    * @return Returns the consumedAttributes.
    */
   public Set getConsumedAttributes()
   {
      return mConsumedAttributes;
   }

   /**
    * Convenience method for accessing the current element.
    * @return current element being read
    */
   protected Element getCurrentElement()
   {
      return mCurrentElement;
   }

   /**
    * Getter for the parent xml def
    */
   protected AeBaseXmlDef getParentXmlDef()
   {
      return mParentDef;
   }

   /**
    * Given the current node element in a DOM tree, this method will read the
    * attributes into memory. This base element is responsible for obtaining
    * a map of all namespaces referenced in the node element.
    * 
    * @param aCurrentDefObj the current node within the DOM tree
    */
   protected void readNamespaceDeclarations(AeBaseXmlDef aCurrentDefObj)
   {
      if (getCurrentElement().hasAttributes())
      {
         // Loop through and add all attributes which are part of the xmlns to the map
         NamedNodeMap attrNodes = getCurrentElement().getAttributes();
         for (int i = 0, length = attrNodes.getLength(); i < length; i++)
         {
            Attr attr = (Attr) attrNodes.item(i);
            if (IAeConstants.W3C_XMLNS.equals(attr.getNamespaceURI()))
            {
               String prefix = attr.getLocalName();
               String namespaceURI = attr.getNodeValue();
               aCurrentDefObj.addNamespace(prefix, namespaceURI);
               getConsumedAttributes().add(attr);
            }
         }
      }
   }

   /**
    * Gets the attribute as an int, also recording the reading of the attribute so
    * it won't be considered an extension attribute.
    * 
    * @param aAttributeName
    * @param aDefault
    */
   protected int getAttributeInt(String aAttributeName, int aDefault)
   {
      String attr = getAttribute(aAttributeName);
      if ("".equals(attr)) //$NON-NLS-1$
      {
         return aDefault;
      }
      try
      {
         return Integer.parseInt(attr);
      }
      catch (NumberFormatException e)
      {
         return aDefault;
      }
   }

   /**
    * Gets the attribute as a long, also recording the reading of the attribute so
    * it won't be considered an extension attribute.
    * 
    * @param aAttributeName
    * @param aDefault
    */
   protected long getAttributeLong(String aAttributeName, long aDefault)
   {
      String attr = getAttribute(aAttributeName);
      if ("".equals(attr)) //$NON-NLS-1$
      {
         return aDefault;
      }
      try
      {
         return Long.parseLong(attr);
      }
      catch (NumberFormatException e)
      {
         return aDefault;
      }
   }

   /**
    * Convenience method for accessing an attribute from
    * the current context element.
    * @param aAttributeName
    * @return the attribute value
    */
   protected String getAttribute(String aAttributeName)
   {
      return getAttributeNS(null, aAttributeName); 
   }
   
   /**
    * Convenience method for accessing a qualified attribute from
    * the current context element.
    * 
    * @param aAttributeNamespace
    * @param aAttributeName
    * @return the attribute value
    */
   protected String getAttributeNS(String aAttributeNamespace, String aAttributeName)
   {
      Attr attr = getCurrentElement().getAttributeNodeNS(aAttributeNamespace, aAttributeName);
      if (attr != null)
      {
         getConsumedAttributes().add(attr);
         return attr.getValue();
      }
      else
      {
         return ""; //$NON-NLS-1$
      }
   }
   
   /**
    * Gets the language attribute from the current element
    */
   protected String getLanguage()
   {
      Attr attr = getCurrentElement().getAttributeNodeNS(IAeConstants.W3C_XML_NAMESPACE, "lang"); //$NON-NLS-1$
      if (attr != null)
      {
         getConsumedAttributes().add(attr);
         return attr.getValue();
      }
      else
      {
         return ""; //$NON-NLS-1$
      }
   }

   /**
    * Convenience method for accessing an attribute QName from
    * the current context element.
    * @param aAttributeName
    * @return the QName
    */
   protected QName getAttributeQName(String aAttributeName)
   {
      if (getCurrentElement().hasAttribute(aAttributeName))
      {
         return AeXmlUtil.createQName(getCurrentElement(), getAttribute(aAttributeName));
      }
      else
         return null;
   }

   /**
    * Convenience method for accessing an attribute with a
    * boolean value from the current context element.
    * @param aAttributeName
    * @return boolean flag to indicate attribute state 
    */
   protected boolean getAttributeBoolean(String aAttributeName)
   {
      String value = getAttribute(aAttributeName);
      return isTrue(value);
   }

   /**
    * Convenience method for accessing a qualified attribute with a boolean value from
    * the current context element.
    * 
    * @param aAttributeNamespace
    * @param aAttributeName
    */
   protected boolean getAttributeBooleanNS(String aAttributeNamespace, String aAttributeName)
   {
      String value = getAttributeNS(aAttributeNamespace, aAttributeName);
      return isTrue(value);
   }

   /**
    * Facilitates the retrieval of a boolean attribute from the current element 
    * This method makes provisions for attributes which may be null, in which 
    * case a null object is returned.
    * 
    * @param aAttributeName the name of the attribute we are looking for
    * @return Boolean flag to indicate attribute state
    */
   protected Boolean getAttributeBoolOptional(String aAttributeName)
   {
      Boolean flag = null;
      String attr = getAttribute(aAttributeName);
      if (attr.length() > 0)
         flag = new Boolean(isTrue(attr));
      
      return flag;
   }

   /**
    * Returns true if the value indicates true, false otherwise.
    * For this base class, "yes" means true.
    * @param value
    */
   protected boolean isTrue(String value)
   {
      return "yes".equalsIgnoreCase(value); //$NON-NLS-1$
   }

   /**
    * Reads the common attributes (namespace decls, extension attributes).
    * 
    * @param aCurrentDef
    */
   protected void readCommonAttributes(AeBaseXmlDef aCurrentDef)
   {
      readNamespaceDeclarations(aCurrentDef);
   }

   /**
    * Reads the standard attributes from the current element and sets them on
    * the given def object.
    * 
    * @param aCurrentDefObj
    */
   protected void readAttributes(AeBaseXmlDef aCurrentDefObj)
   {
      readCommonAttributes(aCurrentDefObj);
   }


   /**
    * Returns a list of child nodes that should be added to a literal def.
    * 
    * @param aElement
    */
   protected List getChildrenForLiteral(Element aElement)
   {
      List childNodes = new ArrayList();
      
      aElement.normalize();
      if (aElement.hasChildNodes())
      {
         NodeList children = aElement.getChildNodes();
         for (int i = 0; i < children.getLength(); i++)
         {
            Node child = children.item(i);
            short type = child.getNodeType();
            if (type == Node.TEXT_NODE)
            {
               if (AeUtil.notNullOrEmpty(child.getNodeValue()))
               {
                  childNodes.add(child);
               }
            }
            else if (type == Node.ELEMENT_NODE || type == Node.CDATA_SECTION_NODE)
            {
               childNodes.add(child);
            }
         }
      }
      return childNodes;
   }
}
 