//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/readers/AeDefReaderRegistry.java,v 1.7 2008/02/29 23:40:23 vvelusamy Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.io.readers; 

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.IAeBaseXmlDefConstants;

/**
 * Registry impl that provides two means of looking up a reader for an element.
 * The first is a parent/child map which maps the parent def object to the child
 * element. This mapping is useful if you want to have different def objects 
 * to model a construct based on its location within the model. The other map is
 * simply a map of element qname to reader. 
 */
public class AeDefReaderRegistry implements IAeDefReaderRegistry, IAeBaseXmlDefConstants
{
   /** map of QNames to readers */
   private Map mGenericReadersMap = new HashMap();
   /** maps def class to map of child QName to reader  */
   private Map mParentRegistryMap = new HashMap();
   /** The reader def visitor factory that the dispatch reader will use to create readers. */
   private IAeReaderFactory mReaderFactory;
   /** A reader that can read in an extension element. */
   private IAeDefReader mExtensionReader;
   /** The default namespace for the domain */
   private String mDefaultNamespace;
   
   /**
    * Ctor
    * @param aDefaultNamespace
    * @param aReaderFactory
    */
   public AeDefReaderRegistry(String aDefaultNamespace, IAeReaderFactory aReaderFactory)
   {
      setDefaultNamespace(aDefaultNamespace);
      setReaderFactory(aReaderFactory);
      
      init();
   }

   /**
    * Initializes the reader registry.
    */
   protected void init()
   {
      initParentRegistry();
      initGenericElementRegistry();
   }

   /**
    * Initializes the parent registry.
    */
   protected void initParentRegistry()
   {
   }

   /**
    * Initializes the generic element registry.
    */
   protected void initGenericElementRegistry()
   {
   }

   /**
    * @return Returns the genericReadersMap.
    */
   protected Map getGenericReadersMap()
   {
      return mGenericReadersMap;
   }

   /**
    * Accessor for parent registry map.
    * @return registry map
    */
   protected Map getParentRegistry()
   {
      return mParentRegistryMap;
   }

   /**
    * @return Returns the readerFactory.
    */
   protected IAeReaderFactory getReaderFactory()
   {
      return mReaderFactory;
   }

   /**
    * @param aReaderFactory The readerFactory to set.
    */
   protected void setReaderFactory(IAeReaderFactory aReaderFactory)
   {
      mReaderFactory = aReaderFactory;
   }

   /**
    * @param aExtensionReader
    */
   protected void setExtensionReader(IAeDefReader aExtensionReader)
   {
      mExtensionReader = aExtensionReader;
   }

   /**
    * Getter for the extension reader
    */
   public IAeDefReader getExtensionReader()
   {
      if (mExtensionReader == null)
         mExtensionReader = new AeExtensionElementReader();
      return mExtensionReader;
   }

   /**
    * Gets the registry's inner map for the given Class.  If the map does not already
    * exist, this method will create and add it.
    * 
    * @param aClass
    */
   protected Map getOrCreateInnerRegistryMap(Class aClass)
   {
      Map innerMap = (Map) getParentRegistry().get(aClass);
      if (innerMap == null)
      {
         innerMap = new HashMap();
         getParentRegistry().put(aClass, innerMap);
      }
      return innerMap;
   }

   /**
    * Adds the readers to the map with the specified qname, allowing for our extensions
    * not assuming that they're in the model's namespace.
    * @param aClass
    * @param aElementQName
    * @param aReader
    */
   protected void registerReader(Class aClass, QName aElementQName, IAeDefReader aReader)
   {
      Map innerMap = getOrCreateInnerRegistryMap(aClass);
      innerMap.put( aElementQName, aReader );
   }

  /**
   * Return the appropriate IAeDefReader impl for this
   * parent def and QName mapping.
   * @param aParentDef parent AeBaseXmlDef in the object model
   * @param aElementQName the child element QName
   * @return IAeReader impl for deserializing this element or null if not found
   * 
   * @see org.activebpel.rt.xml.def.io.readers.IAeDefReaderRegistry#getReader(org.activebpel.rt.xml.def.AeBaseXmlDef, javax.xml.namespace.QName)
   */
   public IAeDefReader getReader(AeBaseXmlDef aParentDef, QName aElementQName) throws UnsupportedOperationException
   {
      // look for any specific parent/qname reader mappings
      IAeDefReader reader = null;
      if (aParentDef != null)
      {
         Map childReadersMap = (Map)getParentRegistry().get(aParentDef.getClass());
         
         if( childReadersMap != null )
         {
            reader = (IAeDefReader)childReadersMap.get(aElementQName);
         }
         
         // if a reader was installed, return it
         // otherwise continue looking
         if( reader != null )
         {
            return reader;
         }
      }
   
      // look in the generic QName -> reader map
      return (IAeDefReader) getGenericReadersMap().get(aElementQName);
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

   /**
    * Convenience method for QName creation based on default 
    * namespace.
    * 
    * @param aElementName
    * @return QName
    */
   protected QName makeDefaultQName(String aElementName)
   {
      return new QName( getDefaultNamespace(), aElementName );
   }

   /**
    * Convenience method for adding readers to the <code>mRegistryMap</code>.
    * <br />
    * <code>mRegistryMap</code> is a map of maps where the inner map contains
    * the QName to IAeDefReader mappings.  The top level map contains the
    * AeDefXmlBase class to inner map mappings.
    * 
    * @param aClass the parent AeBaseXmlDef class
    * @param aElementName child element for this parent
    * @param aReader IAeDefReader impl
    */
   protected void registerReader(Class aClass, String aElementName, IAeDefReader aReader)
   {
      registerReader(aClass, makeDefaultQName(aElementName), aReader);
   }

   /**
    * Creates a dispatch reader for the given def class.
    * 
    * @param aClass
    */
   protected IAeDefReader createReader(Class aClass)
   {
      return new AeDelegatingDefReader(aClass, getReaderFactory());
   }
}
 