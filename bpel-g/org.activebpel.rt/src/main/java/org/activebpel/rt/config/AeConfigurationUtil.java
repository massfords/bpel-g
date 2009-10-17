// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/config/AeConfigurationUtil.java,v 1.9 2007/12/11 22:26:48 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.config;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A static helper/util class for dealing with "entry" based
 * configuration files, such as the ActiveBPEL Engine Config
 * format.
 */
public class AeConfigurationUtil
{
   ///////////////////////////////////////////////////////
   // constants for config file parsing
   ///////////////////////////////////////////////////////
   public static final String CONFIG_TAG = "config"; //$NON-NLS-1$
   public static final String ENTRY_TAG  = "entry"; //$NON-NLS-1$
   public static final String NAME_ATTR  = "name"; //$NON-NLS-1$
   public static final String VALUE_ATTR = "value"; //$NON-NLS-1$

   /** Name of entry for loading class related entries*/
   public static final String CLASS_ENTRY = "Class"; //$NON-NLS-1$


   /**
    * Loads the configuration information found at the given stream.
    *
    * @param aReader
    * @throws AeException
    */
   public static Map loadConfig(Reader aReader) throws AeException
   {
      try
      {
         AeXMLParserBase parser = new AeXMLParserBase();
         parser.setValidating(false);
         parser.setNamespaceAware(true);
         Document doc = parser.loadDocument(aReader, null);
         return loadConfig(doc);
      }
      catch(AeException aex)
      {
         throw aex;
      }
      catch (Throwable ex)
      {
         throw new AeException(AeMessages.getString("AeConfigurationUtil.ErrorLoadingConfigInfo"), ex); //$NON-NLS-1$
      }
   }

   /**
    * Loads the configuration information from the xml configuration document.
    *
    * @param aDocument
    * @throws AeException
    */
   public static Map loadConfig(Document aDocument) throws AeException
   {
      try
      {
         return loadEntries(aDocument.getDocumentElement());
      }
      catch (Throwable ex)
      {
         throw new AeException(AeMessages.getString("AeConfigurationUtil.ErrorLoadingConfigInfo"), ex); //$NON-NLS-1$
      }
   }

   /**
    * Writes the configuration information to the passed writer.
    *
    * @param aWriter The writer to write the document to.
    * @throws AeException If an error occurs saving the config to the writer.
    */
   public static void saveConfig(Writer aWriter, Map aEntries) throws AeException
   {
      Document doc = (new AeXMLParserBase()).createDocument();
      Element root = doc.createElement(CONFIG_TAG);
      doc.appendChild(root);
      writeEntries(root, aEntries);
      AeXMLParserBase.saveDocument(doc, aWriter);
   }

   /**
    * This method takes a configuration map for a config class and instantiates that
    * class.  This involves some simple java reflection to find the proper
    * constructor and then calling that constructor.  Any class can use this method,
    * as long as the class in question has a constructor that takes the configuration
    * map, or a default no-arg constructor.  Returns null if no config is specified.
    *
    * @param aConfig The engine configuration map for the class.
    * @return A config class.
    */
   public static Object createConfigSpecificClass(Map aConfig) throws AeException
   {
      return createConfigSpecificClass(aConfig, (Object) null, (Class) null);
   }

   /**
    * This method takes a configuration map for a config class and second argument and instantiates that
    * class.  This involves some simple java reflection to find the proper
    * constructor and then calling that constructor.  Any class can use this method,
    * as long as the class in question has a constructor that takes the configuration
    * map and a second argument, or a default no-arg constructor.  Returns null if no config is specified.
    *
    * @param aConfig The engine configuration map for the class.
    * @param aCtorArg The second argument (after config map).
    * @param aCtorArgClass The second argument type class.
    * @return A config class.
    */
   public static Object createConfigSpecificClass(Map aConfig, Object aCtorArg, Class aCtorArgClass) throws AeException
   {
      if (aCtorArgClass == null)
         return createConfigSpecificClass(aConfig, (Object[]) null, (Class []) null);
      else
         return createConfigSpecificClass(aConfig, new Object[] { aCtorArg }, new Class[] { aCtorArgClass });
   }

   /**
    * This method takes a configuration map for a config class and second
    * argument and instantiates that class. This involves some simple java
    * reflection to find the proper constructor and then calling that
    * constructor. Any class can use this method, as long as the class in
    * question has a constructor that takes the configuration map and a second
    * argument, or a default no-arg constructor. Returns null if no config is
    * specified.
    *
    * @param aConfig
    * @param aArgs
    * @param aClasses
    */
   public static Object createConfigSpecificClass(Map aConfig, Object[] aArgs, Class[] aClasses) throws AeException
   {
      if (AeUtil.isNullOrEmpty(aConfig))
      {
         return null;
      }
      String className = (String) aConfig.get(CLASS_ENTRY);
      if (className == null)
      {
         throw new AeException(AeMessages.getString("AeConfigurationUtil.ErrorCreatingConfigClass")); //$NON-NLS-1$
      }

      try
      {
         List argsList = new ArrayList();
         Class clazz = Class.forName(className);
         Constructor cons = null;
         // check for multi-arg ctor
         if (aArgs != null && aClasses != null)
         {
            try
            {
               // Find a constructor with a Map
               ArrayList classList = new ArrayList();
               classList.add(Map.class);
               for (int i = 0; i < aClasses.length; i++)
                  classList.add(aClasses[i]);
               cons = clazz.getConstructor((Class []) classList.toArray(new Class[classList.size()]));
               
               // Add all args to the args list for use when we eventually call the c'tor.
               argsList.add(aConfig);
               for (int i = 0; i < aArgs.length; i++)
                  argsList.add(aArgs[i]);
            }
            catch (NoSuchMethodException nsme)
            {
               cons = null;
            }
         }

         // Check for 1-arg c'tor. (Map only)
         if (cons == null)
         {
            try
            {
               // Find a constructor with a Map
               cons = clazz.getConstructor(new Class[] { Map.class });
               argsList.add(aConfig);
            }
            catch (NoSuchMethodException nsme)
            {
               cons = null;
            }
         }
         
         // Check for no-arg c'tor.
         if (cons == null)
         {
            cons = clazz.getConstructor((Class[])null);
         }
         
         if (cons != null)
         {
            if (argsList.isEmpty())
            {
               return cons.newInstance((Object[])null);
            }
            else
            {
               return cons.newInstance(argsList.toArray());
            }
         }
         return null;
      }
      catch (Throwable e)
      {
         String message = AeMessages.format("AeConfigurationUtil.ErrorInstantiating", className); //$NON-NLS-1$
         AeException.logError(e, message);
         throw new AeException(message);
      }
   }


   /**
    * Load entries from the passed element, note this routine is recursive.  It
    * builds maps for any nested config entries.
    *
    * @param aElement The element to fill in the passed entry map for.
    */
   protected static Map loadEntries(Element aElement)
   {
      Map entries = new LinkedHashMap();
      for (Node node = aElement.getFirstChild(); node != null; node = node.getNextSibling())
      {
         if (node.getNodeType() == Node.ELEMENT_NODE)
         {
            Element elem = (Element) node;
            if (ENTRY_TAG.equals(elem.getLocalName()))
            {
               String name = elem.getAttribute(NAME_ATTR);
               if (elem.hasAttribute(VALUE_ATTR))
               {
                  String value = elem.getAttribute(VALUE_ATTR);
                  entries.put(name, value);
               }
               else
               {
                  Map map = loadEntries(elem);
                  entries.put(name, map);
               }
            }
         }
      }
      return entries;
   }

   /**
    * Writes a map of entries as children of the passed element, recursively.
    * @param aParent The parent element to contain the child entries
    * @param aEntries the entries to write.
    */
   private static void writeEntries(Element aParent, Map aEntries)
   {
      for(Iterator iter = aEntries.entrySet().iterator(); iter.hasNext();)
      {
         Map.Entry entry = (Map.Entry)iter.next();
         Element entryElement = aParent.getOwnerDocument().createElement(ENTRY_TAG);
         aParent.appendChild(entryElement);
         entryElement.setAttribute(NAME_ATTR, entry.getKey().toString());
         if(entry.getValue() instanceof Map)
            writeEntries(entryElement, (Map)entry.getValue());
         else if(entry.getValue() != null)
            entryElement.setAttribute(VALUE_ATTR, entry.getValue().toString());
      }
   }

   /**
    * Recursively copies config entry map from the source to the target.
    * @param aSource source entry map
    * @param aTarget target entry map
    */
   public static void copyEntries(Map aSource, Map aTarget)
   {
      for (Iterator iter = aSource.entrySet().iterator(); iter.hasNext();)
      {
         Map.Entry entry = (Map.Entry) iter.next();
         Object key = entry.getKey();
         Object value = entry.getValue();

         if (value instanceof Map)
         {
            Map newCopy = new HashMap();
            copyEntries((Map) value, newCopy);
            aTarget.put(key, newCopy);
         }
         else
         {
            aTarget.put(key, value);
         }
      }
   }
}
