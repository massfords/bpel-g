//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/writers/AeMashupDefWriterRegistry.java,v 1.4 2008/02/17 21:09:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.io.writers;

import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.AeMessages;
import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Wrapper for multiple participating Writer Registries
 */
public class AeMashupDefWriterRegistry implements IAeDefWriterRegistry
{

   /** map of participating write registries */
   private List mWriteRegistries;

   /** currently active write registry */
   private IAeDefWriterRegistry mCurrentRegistry;

   /**
    * Constructor
    * @param aWriteRegistries map of participating writer registries
    */
   public AeMashupDefWriterRegistry(List aWriteRegistries)
   {
      setWriteRegistries(aWriteRegistries);
   }

   /**
    * @see org.activebpel.rt.xml.def.io.writers.IAeDefWriterRegistry#getWriter(java.lang.Class,
    *      org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   public IAeDefWriter getWriter(Class aParentClass, AeBaseXmlDef aDef)
   {
      return getRegistry(aDef).getWriter(aParentClass, aDef);
   }

   /**
    * @see org.activebpel.rt.xml.def.io.writers.IAeDefWriterRegistry#isSupported(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   public boolean isSupported(AeBaseXmlDef aDef)
   {
      return findRegistry(aDef) == null ? false : true;
   }

   /**
    * @return the writeRegistries
    */
   protected List getWriteRegistries()
   {
      return mWriteRegistries;
   }

   /**
    * @param aWriteRegistries the writeRegistries to set
    */
   protected void setWriteRegistries(List aWriteRegistries)
   {
      mWriteRegistries = aWriteRegistries;
   }

   /**
    * Sets and returns the active registry based on the Def or parent Def class type.
    * @param aDef
    * @return the current active write registry
    */
   protected IAeDefWriterRegistry getRegistry(AeBaseXmlDef aDef)
   {
      IAeDefWriterRegistry reg = findRegistry(aDef);
      if ( reg == null )
      {
         reg = findRegistry(aDef.getParentXmlDef());
         if ( reg == null )
         {
            throw new UnsupportedOperationException(AeMessages.format("AeMashupDefWriterRegistry.ERROR_NO_REGISTRY", aDef.getClass().getName())); //$NON-NLS-1$
         }
      }
      setCurrentRegistry(reg);
      return getCurrentRegistry();
   }

   /**
    * @return the currentRegistry
    */
   public IAeDefWriterRegistry getCurrentRegistry()
   {
      return mCurrentRegistry;
   }

   /**
    * @param aCurrentRegistry the currentRegistry to set
    */
   public void setCurrentRegistry(IAeDefWriterRegistry aCurrentRegistry)
   {
      mCurrentRegistry = aCurrentRegistry;
   }

   /**
    * finds a write registry that supports the Def class
    * @param aDef
    * @return the IAeDefWriterRegistry object or null when there is no suppoting write registry
    */
   private IAeDefWriterRegistry findRegistry(AeBaseXmlDef aDef)
   {
      for (Iterator itr = getWriteRegistries().iterator(); itr.hasNext();)
      {
         IAeDefWriterRegistry reg = (IAeDefWriterRegistry)itr.next();
         if ( reg.isSupported(aDef) )
         {
            return reg;
         }
      }
      return null;
   }

}
