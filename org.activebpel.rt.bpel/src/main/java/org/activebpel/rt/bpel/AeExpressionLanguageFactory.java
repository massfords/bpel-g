//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/AeExpressionLanguageFactory.java,v 1.10 2008/02/27 17:54:22 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.expr.AeBPWSExpressionLanguageFactory;
import org.activebpel.rt.bpel.expr.AeWSBPELExpressionLanguageFactory;
import org.activebpel.rt.bpel.expr.IAeBpelExpressionLanguageFactory;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzer;
import org.activebpel.rt.expr.validation.IAeExpressionValidator;
import org.activebpel.rt.util.AeUtil;

/**
 * This implementation of the expression language factory uses the engine configuration file to
 * map expression languages to implementations of validators and runners.
 */
public class AeExpressionLanguageFactory implements IAeExpressionLanguageFactory
{
   /** The config map key to get the list of factories. */
   public static final String FACTORIES_KEY = "Factories"; //$NON-NLS-1$

   /** The map/list of default languages when no config info is found in aeEngineConfig.xml. */
   private static Map sDefaultFactories = new HashMap();

   /**
    * Initializes above languages map.
    */
   static
   {
      try
      {
         Map map = new HashMap();
         map.put("Class", AeBPWSExpressionLanguageFactory.class.getName()); //$NON-NLS-1$
         sDefaultFactories.put(IAeBPELConstants.BPWS_NAMESPACE_URI, map);

         map = new HashMap();
         map.put("Class", AeWSBPELExpressionLanguageFactory.class.getName()); //$NON-NLS-1$
         sDefaultFactories.put(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI, map);
      }
      catch (Throwable t)
      {
         AeException.logError(t, t.getLocalizedMessage());
      }
   }
   
   /** Maps one namespace to another when looking up factories. */
   private static Map sNamespaceMappings = new HashMap();
   
   /**
    * Initializes above sNamespaceMappings map.
    */
   static
   {
      // map abstract-process ns to executable-process ns.
      sNamespaceMappings.put(IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI,IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI);
   }   

   /** The map of BPEL Namespace URI -> expression language factory. */
   private Map mFactoryMap;

   /**
    * Constructs an expression language factory using the given engine configuration map.
    *
    * @param aConfig
    */
   public AeExpressionLanguageFactory(Map aConfig)
   {
      setFactoryMap(new HashMap());

      // Iterate through all the factories.
      Map factories = (Map) aConfig.get(FACTORIES_KEY);
      try
      {
         if (factories == null)
            addDefaultFactories();
         else
            addFactories(factories);
      }
      catch (AeException e)
      {
         e.logError();
      }
   }

   /**
    * Constructs a default expression language factory that supports only XPath 1.0.
    *
    */
   public AeExpressionLanguageFactory()
   {
      setFactoryMap(new HashMap());

      try
      {
         addDefaultFactories();
      }
      catch (AeException e)
      {
         e.logError();
      }
   }

   /**
    * Gets the map of default languages supported by this expression language factory.
    */
   public static Map getDefaultFactories()
   {
      return sDefaultFactories;
   }
   
   /**
    * Returns a mapped (aliased) namespace if available otherwise returns the original namespace. 
    * @param aNamespace
    */
   public static String getMappedNamespace(String aNamespace)
   {
      String ns = (String) sNamespaceMappings.get(aNamespace);
      if (ns != null)
      {
         return ns;
      }
      else
      {
         return aNamespace;
      }
   }

   /**
    * Gets the configured factory for the given bpel namespace URI.
    *
    * @param aBpelNamespace
    */
   protected IAeBpelExpressionLanguageFactory getFactory(String aBpelNamespace)
   {
      IAeBpelExpressionLanguageFactory factory = (IAeBpelExpressionLanguageFactory) mFactoryMap.get(aBpelNamespace);
      if (factory == null)
      {
         // if the factory is not available, then look up using alias (mapped) namespace.
         // (e.g. ws-bpel-abstract-process NS is mapped to ws-bpel-executable-process NSM wrt these factories).
         factory = (IAeBpelExpressionLanguageFactory) mFactoryMap.get( getMappedNamespace(aBpelNamespace) );
      }
      return factory;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeExpressionLanguageFactory#supportsLanguage(java.lang.String, java.lang.String)
    */
   public boolean supportsLanguage(String aBpelNamespace, String aLanguageUri)
   {
      IAeBpelExpressionLanguageFactory factory = getFactory(aBpelNamespace);
      if (factory != null)
         return factory.supportsLanguage(aLanguageUri);
      else
         return false;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeExpressionLanguageFactory#isBpelDefaultLanguage(java.lang.String, java.lang.String)
    */
   public boolean isBpelDefaultLanguage(String aBpelNamespace, String aLanguageUri)
   {
      IAeBpelExpressionLanguageFactory factory = getFactory(aBpelNamespace);
      if (factory != null)
         return factory.isBpelDefaultLanguage(aLanguageUri);
      else
         return false;
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeExpressionLanguageFactory#getBpelDefaultLanguage(java.lang.String)
    */
   public String getBpelDefaultLanguage(String aBpelNamespace)
   {
      IAeBpelExpressionLanguageFactory factory = getFactory(aBpelNamespace);
      if (factory != null)
         return factory.getBpelDefaultLanguage();
      else
         return null;
   }

   /**
    * Adds all of the factories found in the supplied Map of factories to the factory map.
    *
    * @param aFactories
    */
   protected void addFactories(Map aFactories) throws AeException
   {
      for (Iterator iter = aFactories.keySet().iterator(); iter.hasNext(); )
      {
         String uri = (String) iter.next();
         Map langMap = (Map) aFactories.get(uri);
         addFactory(uri, langMap);
      }
   }

   /**
    * Adds the default languages to the language map.
    *
    * @throws AeException
    */
   protected void addDefaultFactories() throws AeException
   {
      addFactories(getDefaultFactories());
   }

   /**
    * Adds a language to the factory.  The map contains all of the pieces of information (found
    * in the engine config) needed to fully describe support for a single expression language.
    * This includes the URL of the language, its name, and implementation classes for validation
    * and execution.
    *
    * @param aNamespaceUri
    * @param aMap
    */
   protected void addFactory(String aNamespaceUri, Map aMap) throws AeException
   {
      getFactoryMap().put(aNamespaceUri, createFactory(aMap));
   }

   /**
    * Creates a bpel expression language factory using the given configuration info.
    *
    * @param aMap
    * @throws AeException
    */
   protected IAeBpelExpressionLanguageFactory createFactory(Map aMap) throws AeException
   {
      if (AeUtil.isNullOrEmpty(aMap))
      {
         return null;
      }
      String className = (String) aMap.get(IAeEngineConfiguration.CLASS_ENTRY);
      if (className == null)
      {
         throw new AeException(AeMessages.getString("AeExpressionLanguageFactory.NO_CLASS_SPECIFIED_FOR_FACTORY")); //$NON-NLS-1$
      }

      try
      {
         Class clazz = Class.forName(className);
         Constructor cons = clazz.getConstructor(new Class[] { Map.class, ClassLoader.class });
         return (IAeBpelExpressionLanguageFactory) cons.newInstance(new Object[] { aMap,
               getFactoryClassloader() });
      }
      catch (Exception e)
      {
         AeException.logError(e, AeMessages.format("AeExpressionLanguageFactory.ERROR_INSTANTIATING_FACTORY", className)); //$NON-NLS-1$
         throw new AeException(AeMessages.format("AeExpressionLanguageFactory.ERROR_INSTANTIATING_FACTORY", className)); //$NON-NLS-1$
      }
   }
   
   /**
    * The classloader that will be passed to the factory (that the factory will use to create 
    * the runner, analyzer, and validator).
    */
   protected ClassLoader getFactoryClassloader()
   {
      return getClass().getClassLoader();
   }

   /**
    * @see org.activebpel.rt.bpel.IAeExpressionLanguageFactory#createExpressionValidator(java.lang.String, java.lang.String)
    */
   public IAeExpressionValidator createExpressionValidator(String aBpelNamespace, String aLanguageUri) throws AeException
   {
      IAeBpelExpressionLanguageFactory factory = getFactory(aBpelNamespace);
      if (factory != null)
         return factory.createExpressionValidator(aLanguageUri);
      else
         throw new AeException(AeMessages.format("AeExpressionLanguageFactory.ERROR_MISSING_EXPRESSION_LANGUAGE_FACTORY", aBpelNamespace)); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.IAeExpressionLanguageFactory#createExpressionAnalyzer(java.lang.String, java.lang.String)
    */
   public IAeExpressionAnalyzer createExpressionAnalyzer(String aBpelNamespace, String aLanguageUri) throws AeException
   {
      IAeBpelExpressionLanguageFactory factory = getFactory(aBpelNamespace);
      if (factory != null)
         return factory.createExpressionAnalyzer(aLanguageUri);
      else
         throw new AeException(AeMessages.format("AeExpressionLanguageFactory.ERROR_MISSING_EXPRESSION_LANGUAGE_FACTORY", aBpelNamespace)); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.IAeExpressionLanguageFactory#createExpressionRunner(java.lang.String, java.lang.String)
    */
   public IAeExpressionRunner createExpressionRunner(String aBpelNamespace, String aLanguageUri) throws AeException
   {
      IAeBpelExpressionLanguageFactory factory = getFactory(aBpelNamespace);
      if (factory != null)
         return factory.createExpressionRunner(aLanguageUri);
      else
         throw new AeException(AeMessages.format("AeExpressionLanguageFactory.ERROR_MISSING_EXPRESSION_LANGUAGE_FACTORY", aBpelNamespace)); //$NON-NLS-1$
   }

   /**
    * @return Returns the factoryMap.
    */
   protected Map getFactoryMap()
   {
      return mFactoryMap;
   }

   /**
    * @param aFactoryMap The factoryMap to set.
    */
   protected void setFactoryMap(Map aFactoryMap)
   {
      mFactoryMap = aFactoryMap;
   }

}
