//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/writers/AeAbstractDefWriter.java,v 1.10 2008/02/17 21:09:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.io.writers; 

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.IAeMutableNamespaceContext;
import org.activebpel.rt.xml.def.AeBaseDefNamespaceContext;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.io.AeCommentIO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Provides methods for creating an element. Details of writing attributes are
 * left to the concrete impls.
 */
public abstract class AeAbstractDefWriter implements IAeDefWriter
{
   /** The current element. */
   private Element mElement;
   /** map of namespace to preferred prefix */
   private Map mPreferredPrefixes;
   /** The def being written. */
   private AeBaseXmlDef mDef;

   /**
    * Creates a new element under the passed parent and starts visiting for it.
    * @param aDef The def that is being serialized
    * @param aParentElement The parent element of the objects created.
    * @param aNamespace The namespace of the element we're creating
    * @param aTagName The tag of the new element to create.
    * @param aPreferredPrefixesMap map of namespace URI's to prefix.
    */
   public AeAbstractDefWriter( AeBaseXmlDef aDef, Element aParentElement, String aNamespace, String aTagName, Map aPreferredPrefixesMap )
   {
      getPreferredPrefixes().putAll(aPreferredPrefixesMap);
      setDef(aDef);
      Element e = createElement(aParentElement, aNamespace, aTagName);
      setElement(e);
      // Check for null - parent element will be null when visiting the process root.
      if (aParentElement != null)
         addChildToParent( aParentElement );
   }

   /**
    * @return Element the current element being written to.
    */
   public Element getElement()
   {
      return mElement;
   }
   
   /**
    * Setter for the element
    * @param aElement
    */
   protected void setElement(Element aElement)
   {
      mElement = aElement;
   }

   /**
    * @param aNamespaceURI
    */
   public String getPreferredPrefix(String aNamespaceURI)
   {
      if (mPreferredPrefixes == null)
         return null;
      return (String) getPreferredPrefixes().get(aNamespaceURI);
   }

   /**
    * Adds a mapping
    * @param aNamespaceURI
    * @param aPrefix
    */
   public void addPreferredPrefix(String aNamespaceURI, String aPrefix)
   {
      getPreferredPrefixes().put(aNamespaceURI, aPrefix);
   }

   /**
    * @return the preferredPrefixes
    */
   protected Map getPreferredPrefixes()
   {
      if (mPreferredPrefixes == null)
         setPreferredPrefixes(new HashMap());
      return mPreferredPrefixes;
   }

   /**
    * @param aPreferredPrefixes the preferredPrefixes to set
    */
   protected void setPreferredPrefixes(Map aPreferredPrefixes)
   {
      mPreferredPrefixes = aPreferredPrefixes;
   }

   /**
    * @return Returns the def.
    */
   protected AeBaseXmlDef getDef()
   {
      return mDef;
   }

   /**
    * @param aDef the def to set
    */
   protected void setDef(AeBaseXmlDef aDef)
   {
      mDef = aDef;
   }

   /**
    * Utility method for creating a NS aware DOM element.
    * The element will be created with the default BPEL namespace.
    *
    * @param aParentElement used to get a reference to the owner doc
    * @param aNamespace the namespace of the element we're creating
    * @param aName the element tag name
    * @return the newly created element
    */
   protected Element createElement(Element aParentElement, String aNamespace, String aName)
   {
      Element element = null;
      if (aParentElement == null)
      {
         element = createRootElement(aNamespace, aName);
      }
      else
      {
         String currentDefaultNS = AeXmlUtil.findDefaultNamespace(aParentElement);
         if (!AeUtil.compareObjects(currentDefaultNS, aNamespace))
         {
            // if the element we're writing is in a namespace other than the bpel
            // namespace then we want to write the element with a prefix
   
            // check to see if there is a prefix already available for this namespace
            String prefix = AeXmlUtil.getOrCreatePrefix(aParentElement, aNamespace);
   
            // if we have a non-null prefix then create the element with that prefix
            String elementName = AeUtil.notNullOrEmpty(prefix) ? prefix + ":" + aName : aName; //$NON-NLS-1$
            element = aParentElement.getOwnerDocument().createElementNS(aNamespace, elementName);
         }
         else
         {
            element = aParentElement.getOwnerDocument().createElementNS(aNamespace, aName);
         }
      }
      return element;
   }

   /**
    * Create the xml doc first, then the process element. Appends the process element to the doc and returns
    * the process element.
    *
    * @param aNamespace
    * @param aName
    * @return process element
    */
   protected Element createRootElement(String aNamespace, String aName)
   {
      Document doc = AeXmlUtil.newDocument();
      String elementName = aName;
      String defaultNS = getDef().getDefaultNamespace();
      if (!AeUtil.compareObjects(defaultNS, aNamespace))
      {
         String preferredPrefix = getPreferredPrefix(aNamespace);
         String prefix = new AeBaseDefNamespaceContext(getDef()).getOrCreatePrefixForNamespace(
               preferredPrefix, aNamespace, true);
         elementName = prefix + ":" + aName; //$NON-NLS-1$
      }
      Element element = doc.createElementNS(aNamespace, elementName);
      doc.appendChild(element);
      return element;
   }

   /**
    * Sets (writes) a QName attribute to the Element.
    *
    * @param aAttributeName
    * @param aQName
    */
   protected void setAttribute(String aAttributeName, QName aQName)
   {
      AeXmlUtil.setAttributeQName(getElement(), aAttributeName, aQName);
   }
   
   /**
    * Sets a number attribute
    * @param aAttributeName
    * @param aNumber
    */
   protected void setAttribute(String aAttributeName, long aNumber)
   {
      setAttribute(aAttributeName, String.valueOf(aNumber));
   }

   /**
    * Convenience method for adding a qualified attribute (by name) to the current context
    * element.  A prefix is found or created for the attribute, and used as the qualified
    * name of it.
    * 
    * @param aMutableNSContext
    * @param aAttrNamespace
    * @param aAttrName
    * @param aAttrValue
    */
   protected void setAttributeNS(IAeMutableNamespaceContext aMutableNSContext, String aAttrNamespace, String aPreferredPrefix, String aAttrName, String aAttrValue)
   {
      if (AeUtil.notNullOrEmpty(aAttrValue))
      {
         if (AeUtil.isNullOrEmpty(aAttrNamespace))
         {
            getElement().setAttributeNS(null, aAttrName, aAttrValue);
         }
         else
         {
            String prefix = aMutableNSContext.getOrCreatePrefixForNamespace(aPreferredPrefix, aAttrNamespace);
            getElement().setAttributeNS(aAttrNamespace, prefix + ":" + aAttrName, aAttrValue); //$NON-NLS-1$
         }
      }
   }

   /**
    * Sets (writes) an attribute to the Element.
    *
    * @param aAttrName
    * @param aAttrValue
    */
   protected void setAttribute(String aAttrName, String aAttrValue)
   {
      if (aAttrValue != null && aAttrValue.length() > 0)
      {
         getElement().setAttributeNS(null, aAttrName, aAttrValue);
      }
   }
   
   /**
    * Setter for the language
    * 
    * @param aLanguage
    */
   protected void setLanguage(String aLanguage)
   {
      if (AeUtil.notNullOrEmpty(aLanguage))
      {
         getElement().setAttributeNS(IAeConstants.W3C_XML_NAMESPACE, "xml:lang", aLanguage); //$NON-NLS-1$
      }
   }

   /**
    * Facilitates setting a boolean attribute within the current context Element.
    * The aRequired flag will suppress the attribute generation if set to FALSE and if
    * the aAttrValue is false as well.
    *
    * @param aAttrName the name of the attribute we are setting
    * @param aValue the value of the attribute we are setting
    * @param aRequired flag indicating if the attribute is required in the document
    */
   protected void setAttribute(String aAttrName, boolean aValue, boolean aRequired)
   {
      if (aRequired || (!aRequired && aValue))
      {
         setAttribute(aAttrName, booleanToString(aValue));
      }
   }

   /**
    * Adds the current element to the passed parent.
    */
   protected void addChildToParent(Element aParentElement)
   {
      aParentElement.appendChild( getElement() );
   }

   /**
    * Facilitates setting a boolean attribute within the element node which was passed in.
    * The aRequired flag will suppress the attribute generation if set to FALSE and if
    * the aAttrValue is false as well.
    *
    * @param aMutableNSContext
    * @param aAttrNamespace the namespace of the attribute we are setting
    * @param aPreferredPrefix the preferred prefix to use when qualifying the attribute name
    * @param aAttrName the name of the attribute we are setting
    * @param aValue the value of the attribute we are setting
    * @param aRequired flag indicating if the attribute is required in the document
    */
   protected void setAttributeNS(IAeMutableNamespaceContext aMutableNSContext, String aAttrNamespace, String aPreferredPrefix, String aAttrName, boolean aValue, boolean aRequired)
   {
      // Set the attribute if it is required OR if it is optional and
      // value is TRUE.  Otherwise no need to set it.
      if (aRequired || (!aRequired && aValue))
      {
         setAttributeNS(aMutableNSContext, aAttrNamespace, aPreferredPrefix, aAttrName, booleanToString(aValue));
      }
   }

   /**
    * Responsible for writing out namespace attributes for the created element.
    * @param aElement current element node in the tree.
    */
   protected void writeNamespaceAttributes(AeBaseXmlDef aBaseDef, Element aElement)
   {
      for (Iterator iter = aBaseDef.getNamespacePrefixList().iterator(); iter.hasNext();)
      {
         String prefix   = (String)iter.next();
         aElement.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:" + prefix, aBaseDef.getNamespace(prefix)); //$NON-NLS-1$
      }
   }

   /**
    * Writes out all extension attributes found on the def.
    * 
    * @param aBaseDef
    * @param aElement
    */
   protected void writeExtensionAttributes(AeBaseXmlDef aBaseDef, Element aElement)
   {
      for (Iterator iter = aBaseDef.getExtensionAttributeDefs().iterator(); iter.hasNext(); )
      {
         AeExtensionAttributeDef extAttribute = (AeExtensionAttributeDef) iter.next();
         aElement.setAttributeNS(extAttribute.getNamespace(), extAttribute.getQualifiedName(), extAttribute.getValue());
      }
   }

   /**
    * Writes stored comments and namespace decls to the element.
    *
    * @param aBaseDef
    */
   protected void writeStandardAttributes(AeBaseXmlDef aBaseDef)
   {
      AeCommentIO.writeFormattedComments( getElement(), aBaseDef.getComment() );
      writeNamespaceAttributes( aBaseDef, getElement() );
      writeExtensionAttributes( aBaseDef, getElement() );
   }

   /**
    * @param aBoolean
    */
   protected String booleanToString(boolean aBoolean)
   {
      if (aBoolean)
         return "true"; //$NON-NLS-1$
      return "false"; //$NON-NLS-1$
   }

   /**
    * Appends text to the element if it isn't null or empty
    * @param aText
    */
   protected void appendText(String aText)
   {
      if (AeUtil.notNullOrEmpty(aText))
      {
         getElement().appendChild(getElement().getOwnerDocument().createTextNode(aText));
      }
   }

   /**
    * Creates and append a text node to the document
    * @param aValue
    */
   protected void writeText(String aValue)
   {
      if ( AeUtil.notNullOrEmpty(aValue) )
      {
         Text textNode = getElement().getOwnerDocument().createTextNode(aValue);
         getElement().appendChild(textNode);
      }
   }
}
 