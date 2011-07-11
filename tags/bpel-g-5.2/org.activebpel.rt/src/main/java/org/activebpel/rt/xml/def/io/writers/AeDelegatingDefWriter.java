//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/writers/AeDelegatingDefWriter.java,v 1.1 2007/09/26 02:17:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.io.writers; 

import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.w3c.dom.Element;


/**
 * Delegates the actual writing to a def writer created by the provided factory.
 */
public class AeDelegatingDefWriter implements IAeDefWriter
{
   /** tag name for element*/
   private String mTagName;
   /** namespace for the element, defaults to the bpel namespace */
   private String mNamespace;
   /** Factory for creating writer def visitors to dispatch to. */
   private IAeDefWriterFactory mWriterFactory;
   
   /**
    * Ctor
    * @param aNamespaceUri
    * @param aTagName
    * @param aWriterFactory
    */
   public AeDelegatingDefWriter(String aNamespaceUri, String aTagName, IAeDefWriterFactory aWriterFactory)
   {
      setNamespace(aNamespaceUri);
      setTagName(aTagName);
      setWriterFactory(aWriterFactory);
   }

   /**
    * @see org.activebpel.rt.xml.def.io.writers.IAeDefWriter#createElement(AeBaseXmlDef, org.w3c.dom.Element)
    */
   public Element createElement(AeBaseXmlDef aBaseDef, Element aParentElement)
   {
      IAeDefWriter writer = getWriterFactory().createDefWriter(aBaseDef,
            aParentElement, getNamespace(), getTagName());
      return writer.createElement(aBaseDef, aParentElement);
   }

   /**
    * Setter for tag name
    * @param tagName
    */
   protected void setTagName(String tagName)
   {
      mTagName = tagName;
   }

   /**
    * Getter for tag name
    */
   protected String getTagName()
   {
      return mTagName;
   }

   /**
    * Setter for namespace
    * @param namespace
    */
   protected void setNamespace(String namespace)
   {
      mNamespace = namespace;
   }

   /**
    * Getter for namespace
    */
   protected String getNamespace()
   {
      return mNamespace;
   }

   /**
    * @return Returns the writerVisitorFactory.
    */
   protected IAeDefWriterFactory getWriterFactory()
   {
      return mWriterFactory;
   }

   /**
    * @param aWriterVisitorFactory The writerVisitorFactory to set.
    */
   protected void setWriterFactory(IAeDefWriterFactory aWriterVisitorFactory)
   {
      mWriterFactory = aWriterVisitorFactory;
   }
}
 