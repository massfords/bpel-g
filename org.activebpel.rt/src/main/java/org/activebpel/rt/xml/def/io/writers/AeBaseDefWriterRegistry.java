//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/writers/AeBaseDefWriterRegistry.java,v 1.6 2008/01/11 01:48:57 mford Exp $
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
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeMessages;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;
import org.activebpel.rt.xml.def.io.AeCommentIO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Provides simple registry that maps defs to writers
 */
public class AeBaseDefWriterRegistry implements IAeDefWriterRegistry
{
   /** A Skip Writer */
   public static final AeSkipWriter SKIP_WRITER = new AeSkipWriter();

   /** writer registry */
   private Map mRegistry = new HashMap();
   /** The writer def visitor factory. */
   private IAeDefWriterFactory mWriterFactory;
   /** default namespace for elements if not specified */
   private String mDefaultNamespace;
   
   /**
    * Ctor
    * @param aDefaultNamespace
    * @param aFactory
    */
   public AeBaseDefWriterRegistry(String aDefaultNamespace, IAeDefWriterFactory aFactory)
   {
      setDefaultNamespace(aDefaultNamespace);
      setWriterFactory(aFactory);
      init();
   }

   /**
    * Initializes the registry.
    */
   protected void init()
   {
      registerWriter( AeExtensionElementDef.class, new AeExtensionElementWriter());
      // writers produce elements, so use the skip writer here 
      registerWriter( AeExtensionAttributeDef.class, SKIP_WRITER);
   }

   /**
    * Registers a dispatch writer.
    *
    * @param aClass
    * @param aTagName
    */
   protected void registerWriter(Class aClass, String aTagName)
   {
      registerWriter(aClass, new QName(getDefaultNamespace(), aTagName));
   }

   /**
    * Registers a dispatch writer.
    *
    * @param aClass
    * @param aTagName
    */
   protected void registerWriter(Class aClass, QName aTagName)
   {
      registerWriter(aClass, createWriter(aTagName.getNamespaceURI(), aTagName.getLocalPart()));
   }

   /**
    * Registers a bpel def writer.
    * 
    * @param aClass
    * @param aWriter
    */
   protected void registerWriter(Class aClass, IAeDefWriter aWriter)
   {
      getRegistry().put(aClass, aWriter);
   }

   /**
    * Creates a dispatch writer with the given tag name (in the bpel namespace).
    *
    * @param aTagName
    */
   protected IAeDefWriter createWriter(String aTagName)
   {
      return createWriter(getDefaultNamespace(), aTagName);
   }

   /**
    * Creates a dispatch writer with the given tag name (in the given namespace).
    *
    * @param aNamespace
    * @param aTagName
    */
   protected IAeDefWriter createWriter(String aNamespace, String aTagName)
   {
      return new AeDelegatingDefWriter(aNamespace, aTagName, getWriterFactory());
   }

   /**
    * Gets the writer visitor factory.
    */
   protected IAeDefWriterFactory getWriterFactory()
   {
      return mWriterFactory;
   }

   /**
    * Retrieve the writer class for the AeDef object.  In this
    * impl, the parent class is ignored as no special mappings
    * were required.
    * @param aParentClass ignored
    * @param aDef the base def object to be serialized
    * @return the appropriate writer
    */
   public IAeDefWriter getWriter(Class aParentClass, AeBaseXmlDef aDef)
   {
      IAeDefWriter writer = (IAeDefWriter)mRegistry.get(aDef.getClass());
      if( writer == null )
      {
         throw new UnsupportedOperationException(AeMessages.format("AeBaseDefWriterRegistry.WriterNotFound",  //$NON-NLS-1$
               aDef.getClass().getName() ));
      }
      return writer;
   }

   /**
    * @see org.activebpel.rt.xml.def.io.writers.IAeDefWriterRegistry#isSupported(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   public boolean isSupported(AeBaseXmlDef aDef)
   {
      return getRegistry().containsKey(aDef.getClass());
   }
   
   /**
    * NOTE: publicly exposed to enable participation of multiple registries in a namespace based registry.
    * @return Returns the registry.
    */
   protected Map getRegistry()
   {
      return mRegistry;
   }

   /**
    * @param aRegistry The registry to set.
    */
   protected void setRegistry(Map aRegistry)
   {
      mRegistry = aRegistry;
   }

   /**
    * @param aWriterVisitorFactory The writerVisitorFactory to set.
    */
   protected void setWriterFactory(IAeDefWriterFactory aWriterVisitorFactory)
   {
      mWriterFactory = aWriterVisitorFactory;
   }

   /**
    * A bpel def writer that will skip the creation of an element and instead return the parent
    * element.  This is useful for writing 1.1 bpel because the AE Def model includes some Defs
    * for constructs that aren't really there in the 1.1 bpel source.
    */
   protected static class AeSkipWriter implements IAeDefWriter
   {
      /**
       * Default c'tor.
       */
      public AeSkipWriter()
      {
         super();
      }

      /**
       * @see org.activebpel.rt.xml.def.io.writers.IAeDefWriter#createElement(AeBaseXmlDef, org.w3c.dom.Element)
       */
      public Element createElement(AeBaseXmlDef aBaseDef, Element aParentElement)
      {
         return aParentElement;
      }
   }

   /**
    * Writes an extension element.
    */
   public class AeExtensionElementWriter implements IAeDefWriter
   {
      /**
       * @see org.activebpel.rt.xml.def.io.writers.IAeDefWriter#createElement(AeBaseXmlDef, org.w3c.dom.Element)
       */
      public Element createElement(AeBaseXmlDef aBaseDef, Element aParentElement)
      {
         AeExtensionElementDef extensionElementDef = (AeExtensionElementDef) aBaseDef;
         Document ownerDoc = aParentElement.getOwnerDocument();
         // copy the existing node set over
         Element extNode = (Element) ownerDoc.importNode(extensionElementDef.getExtensionElement(), true);
         aParentElement.appendChild(extNode);
         AeCommentIO.writeFormattedComments(extNode, extensionElementDef.getComments());
         return extNode;
      }
   }

   /**
    * @return the defaultNamespace
    */
   protected String getDefaultNamespace()
   {
      return mDefaultNamespace;
   }

   /**
    * @param aDefaultNamespace the defaultNamespace to set
    */
   protected void setDefaultNamespace(String aDefaultNamespace)
   {
      mDefaultNamespace = aDefaultNamespace;
   }
}
 