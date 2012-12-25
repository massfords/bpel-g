// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/AeDefIORegistry.java,v 1.2 2007/10/04 20:45:42 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.def.io;

import javax.xml.namespace.QName;

import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.io.readers.IAeDefReader;
import org.activebpel.rt.xml.def.io.readers.IAeDefReaderRegistry;
import org.activebpel.rt.xml.def.io.writers.IAeDefWriter;
import org.activebpel.rt.xml.def.io.writers.IAeDefWriterRegistry;

/**
 * An abstract base class for def registries.
 */
public class AeDefIORegistry implements IAeDefRegistry
{
   /** reader registry impl */
   private IAeDefReaderRegistry mReaderRegistry;
   /** writer registry impl */
   private IAeDefWriterRegistry mWriterRegistry;
   /** extension reader impl */
   private IAeDefReader mExtensionReader;

   /**
    * Default constructor - creates the reader and writer registries (delegates the actual
    * construction to subclasses).
    */
   public AeDefIORegistry(IAeDefReaderRegistry aReaderRegistry, IAeDefWriterRegistry aWriterRegistry)
   {
      setReaderRegistry(aReaderRegistry);
      setWriterRegistry(aWriterRegistry);
   }

   /**
    * @see org.activebpel.rt.xml.def.io.IAeDefRegistry#getReader(AeBaseXmlDef, javax.xml.namespace.QName)
    */
   public IAeDefReader getReader(AeBaseXmlDef aParentDef, QName aQName)
   {
      return getReaderRegistry().getReader(aParentDef, aQName);
   }
   
   /**
    * @see org.activebpel.rt.xml.def.io.IAeDefRegistry#getExtensionReader()
    */
   public IAeDefReader getExtensionReader()
   {
      if (mExtensionReader == null)
         mExtensionReader = getReaderRegistry().getExtensionReader();
      return mExtensionReader;
   }

   /**
    * @see org.activebpel.rt.xml.def.io.IAeDefRegistry#getWriter(java.lang.Class, AeBaseXmlDef)
    */
   public IAeDefWriter getWriter(Class aParentClass, AeBaseXmlDef aDef)
   {
      return getWriterRegistry().getWriter(aParentClass, aDef);
   }

   /**
    * @return Returns the readerRegistry.
    */
   public IAeDefReaderRegistry getReaderRegistry()
   {
      return mReaderRegistry;
   }

   /**
    * @param aReaderRegistry The readerRegistry to set.
    */
   protected void setReaderRegistry(IAeDefReaderRegistry aReaderRegistry)
   {
      mReaderRegistry = aReaderRegistry;
   }

   /**
    * @return Returns the writerRegistry.
    */
   public IAeDefWriterRegistry getWriterRegistry()
   {
      return mWriterRegistry;
   }

   /**
    * @param aWriterRegistry The writerRegistry to set.
    */
   protected void setWriterRegistry(IAeDefWriterRegistry aWriterRegistry)
   {
      mWriterRegistry = aWriterRegistry;
   }

   /**
    * @see org.activebpel.rt.xml.def.io.IAeDefRegistry#setExtensionReader(org.activebpel.rt.xml.def.io.readers.IAeDefReader)
    */
   public void setExtensionReader(IAeDefReader aExtensionReader)
   {
      mExtensionReader = aExtensionReader;
   }
}
