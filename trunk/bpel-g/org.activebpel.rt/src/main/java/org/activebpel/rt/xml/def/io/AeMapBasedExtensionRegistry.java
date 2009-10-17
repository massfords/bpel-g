//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/AeMapBasedExtensionRegistry.java,v 1.1 2007/10/17 18:45:13 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.io;

import java.util.Map;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.def.IAeExtensionObject;

/**
 * An implementation of the extension Registry for WS-BPEL 2.0. This registry contains a map
 * of extension element, attribute and activity Qnames to their class names that implement
 * IAeExtensionObject interface
 */
public class AeMapBasedExtensionRegistry implements IAeExtensionRegistry
{
   
   /** map of QNames to extension object class */
   private Map mExtensionObjectMap;
   
   /**
    * C'tor
    * @param aExtensionObjectMap
    */
   public AeMapBasedExtensionRegistry(Map aExtensionObjectMap)
   {
      super();
      setExtensionObjectMap(aExtensionObjectMap);
   }

   /**
    * @see org.activebpel.rt.xml.def.io.IAeExtensionRegistry#getExtensionObject(javax.xml.namespace.QName)
    */
   public IAeExtensionObject getExtensionObject(QName aQName)
   {
      Class aClass = getExtensionObjectClass(aQName);
      if (aClass == null)
         return null;
      
      try
      {
         return (IAeExtensionObject) aClass.newInstance();
      }
      catch (InstantiationException ex)
      {
         AeException.logError(ex, ex.getLocalizedMessage());
      }
      catch (IllegalAccessException ex)
      {
         AeException.logError(ex, ex.getLocalizedMessage());
      }
      return null;
   }

   /**
    * @return the extensionObjectMap
    */
   protected Map getExtensionObjectMap()
   {
      return mExtensionObjectMap;
   }

   /**
    * @param aExtensionObjectMap the extensionObjectMap to set
    */
   protected void setExtensionObjectMap(Map aExtensionObjectMap)
   {
      mExtensionObjectMap = aExtensionObjectMap;
   }

   /**
    * @param aQName
    * @return aClass ExtensionObject class that is mapped to aQName
    */
   private Class getExtensionObjectClass(QName aQName)
   {
      if (AeUtil.notNullOrEmpty(getExtensionObjectMap()) && getExtensionObjectMap().containsKey(aQName))
         return (Class) getExtensionObjectMap().get(aQName);
      else
         return null;
   }
}
