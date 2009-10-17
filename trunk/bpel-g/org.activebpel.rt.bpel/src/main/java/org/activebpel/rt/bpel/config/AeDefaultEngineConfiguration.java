// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/config/AeDefaultEngineConfiguration.java,v 1.41.4.1 2008/04/21 16:09:44 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.config;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeExpressionLanguageFactory;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.def.validation.expr.functions.AeFunctionValidatorFactory;
import org.activebpel.rt.bpel.function.AeFunctionContextContainer;
import org.activebpel.rt.bpel.function.AeFunctionContextLocator;
import org.activebpel.rt.bpel.function.AeUnresolvableException;
import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.bpel.function.IAeFunctionContext;
import org.activebpel.rt.bpel.function.IAeFunctionContextLocator;
import org.activebpel.rt.bpel.impl.function.AeBPWSBpelFunctionContext;
import org.activebpel.rt.bpel.impl.function.AeExtensionFunctionContext;
import org.activebpel.rt.bpel.impl.function.AeInvalidFunctionContextException;
import org.activebpel.rt.bpel.impl.function.AeWSBPELBpelFunctionContext;
import org.activebpel.rt.config.AeConfiguration;
import org.activebpel.rt.config.AeConfigurationUtil;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidator;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidatorFactory;
import org.activebpel.rt.util.AeUtil;

/**
 * Default engine configuration implementation.
 */
public class AeDefaultEngineConfiguration extends AeConfiguration implements IAeEngineConfiguration,
      IAeUpdatableEngineConfig, Cloneable
{
   private static final String ERROR_LOADING_FUNCTION_CONTEXT_LOCATOR = "AeDefaultEngineConfiguration.ERROR_17"; //$NON-NLS-1$
   private static final String ERROR_LOADING_FUNCTION_CONTEXT_CLASS = "AeDefaultEngineConfiguration.ERROR_18"; //$NON-NLS-1$

   /** Default name for static engine config file. */
   public static final String DEFAULT_CONFIG_FILE = "aeEngineConfig.xml"; //$NON-NLS-1$
   //////////////////////////////////////////////////////
   // default values for some of the main entries.
   //////////////////////////////////////////////////////
   /** max number of join style correlationSet combinations that get computed for dispatching inbound receives (persistence only) */
   private static final int DEFAULT_MAX_JOIN_COMBINATIONS = 3;
   /** Default description of the configuration. */
   private static final String DEFAULT_CONFIG_DESCRIPTION = ""; //$NON-NLS-1$
   /** default timeout value for unmatched correlated receives */
   public static final int UNMATCHED_RECEIVE_TIMEOUT_DEFAULT = 30;
   /** default timeout value for web service operations */
   public static final int WEB_SERVICE_TIMEOUT_DEFAULT = 600; // 10 minutes
   /** default min number of threads for the work manager's thread pool */
   public static final int WORKMANAGER_THREAD_MIN_DEFAULT = 1;
   /** default max number of threads for the work manager's thread pool */
   public static final int WORKMANAGER_THREAD_MAX_DEFAULT = 10;
   /** default maximum number of work requests to schedule per-process */
   public static final int PROCESS_WORK_COUNT_DEFAULT = 10;
   /** default resource cache maximum value */
   private static final int DEFAULT_RESOURCE_CACHE_MAX = 100;
   /** default partner defn address factory */
   private static final String PDEF_FACTORY_DEFAULT = "org.activebpel.rt.bpel.server.addressing.pdef.AeDefaultPartnerAddressingFactory"; //$NON-NLS-1$
   /** default process manager */
   private static final String PROCESS_MANAGER_DEFAULT = "org.activebpel.rt.bpel.impl.AeInMemoryProcessManager"; //$NON-NLS-1$
   /** Default logging dir - defaults to {user.home}/AeBpelEngine*/
   private static final String LOGGING_DIR_DEFAULT = new File(System.getProperty("user.home"), "AeBpelEngine").getPath(); //$NON-NLS-1$ //$NON-NLS-2$
   /** Configuration change listeners. */
   protected List mListeners = new ArrayList();
   /** Container for custom function contexts. */
   protected AeFunctionContextContainer mContextContainer;
   /** A cached expression language factory. */
   protected IAeExpressionLanguageFactory mExpressionLanguageFactory;
   /** A cached function validator factory. */
   protected IAeFunctionValidatorFactory mFunctionValidatorFactory;
   /** Storage listeners  */
   protected List mStorageListeners = new ArrayList();

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#addNewFunctionContext(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    */
   public synchronized void addNewFunctionContext(String aName, String aNamespace, String aClassName,
         String aLocation) throws AeFunctionContextExistsException, AeInvalidFunctionContextException
   {
      if (getFunctionContextContainer().getFunctionContext(aName) != null)
      {
         throw new AeFunctionContextExistsException(AeFunctionContextExistsException.DUPLICATE_NAME);
      }

      if (getFunctionContextContainer().getFunctionContext(aNamespace) != null)
      {
         throw new AeFunctionContextExistsException(AeFunctionContextExistsException.DUPLICATE_PREFIX_OR_NAMESPACE);
      }

      try
      {
         IAeFunctionContext functionContext = getFunctionContextContainer().loadFunctionContext(aNamespace, aLocation, aClassName);
         addFunctionContext(aName, aNamespace, functionContext);
         // update the map because this is what drives what will be persisted
         updateEntriesMap(aName, aNamespace, aClassName, aLocation);
      }
      catch( AeInvalidFunctionContextException ae )
      {
         ae.logError();
         throw ae;
      }
   }

   /**
    * Update the internal (in-memory) data storage.
    * @param aName
    * @param aNamespace
    * @param aClassName
    * @param aLocation
    */
   protected void updateEntriesMap( String aName, String aNamespace, String aClassName, String aLocation )
   {
      Map customFunctionsMap = getMapEntry( IAeEngineConfiguration.FUNCTION_CONTEXTS_ENTRY );
      if( customFunctionsMap == null )
      {
         customFunctionsMap = new HashMap();
         getEntries().put( IAeEngineConfiguration.FUNCTION_CONTEXTS_ENTRY, customFunctionsMap );
      }

      Map contextMap = new HashMap();
      customFunctionsMap.put( aName, contextMap );
      contextMap.put( IAeEngineConfiguration.CLASS_ENTRY, aClassName );

      if( AeUtil.notNullOrEmpty(aNamespace) )
      {
         contextMap.put( IAeEngineConfiguration.FUNCTION_CONTEXT_NAMESPACE_ENTRY, aNamespace );
      }

      if( AeUtil.notNullOrEmpty(aLocation) )
      {
         contextMap.put( IAeEngineConfiguration.FUNCTION_CONTEXT_CLASSPATH_ENTRY, aLocation );
      }
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#deleteFunctionContexts(java.util.Collection)
    */
   public synchronized void deleteFunctionContexts( Collection aContextNames )
   {
      if( aContextNames != null )
      {
         for( Iterator iter = aContextNames.iterator(); iter.hasNext(); )
         {
            String name = (String)iter.next();
            getFunctionContextContainer().remove( name );
            Map contextMap = getMapEntry( IAeEngineConfiguration.FUNCTION_CONTEXTS_ENTRY );
            if( contextMap != null )
            {
               contextMap.remove( name );
            }
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getExpressionLanguageFactory()
    */
   public IAeExpressionLanguageFactory getExpressionLanguageFactory() throws AeException
   {
      return mExpressionLanguageFactory;
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getFunctionValidatorFactory()
    */
   public IAeFunctionValidatorFactory getFunctionValidatorFactory() throws AeException
   {
      return mFunctionValidatorFactory;
   }

   /**
    * Creates the function validator factory using the engine config to load additional
    * custom validators.  This method is called on config load and the function validator
    * factory is cached at that point.
    *
    * @throws AeException
    */
   public void createFunctionValidatorFactory() throws AeException
   {
      AeFunctionValidatorFactory factory = new AeFunctionValidatorFactory();
      registerFunctionValidators(getMapEntry(FUNCTION_VALIDATORS), factory);
      mFunctionValidatorFactory = factory;
   }

   /**
    * Creates the expression language factory using the current configuration map.  Defaults to the
    * standard AeExpressionLanguageFactory which includes support for XPath 1.0 as required by the
    * BPEL spec.  This method is called on config load and the expression language factory is
    * cached at that point.
    *
    * @throws AeException
    */
   protected void createExpressionLanguageFactory() throws AeException
   {
      try
      {
         IAeExpressionLanguageFactory factory = (IAeExpressionLanguageFactory) createConfigSpecificClass(EXPRESSION_FACTORY);
         if (factory == null)
         {
            mExpressionLanguageFactory = createDefaultExpressionLanguageFactory();
         }
         else
         {
            mExpressionLanguageFactory = factory;
         }
      }
      catch (Throwable t)
      {
         throw new AeException(AeMessages.getString("AeDefaultEngineConfiguration.FAILED_TO_CREATE_EXPR_LANG_FACTORY_ERROR"), t); //$NON-NLS-1$
      }
   }

   /**
    * Creates the default expression language factory when none is found in the configuration
    * file.
    */
   protected IAeExpressionLanguageFactory createDefaultExpressionLanguageFactory()
   {
      return new AeExpressionLanguageFactory();
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getFunction(java.lang.String, java.lang.String)
    */
   public IAeFunction getFunction(String aFunctionName, String aNamespaceUri) throws AeUnresolvableException
   {
      IAeFunctionContext context = getFunctionContextContainer().getFunctionContext(aNamespaceUri);
      if (context == null)
      {
         return null;
      }

      try
      {
         return context.getFunction(aFunctionName);
      }
      catch( AeUnresolvableException ure )
      {
         AeException.logError(ure, ure.getLocalizedMessage());
         throw new AeUnresolvableException(ure.getLocalizedMessage());
      }
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getFunctionContextNamespaceList()
    */
   public Set getFunctionContextNamespaceList()
   {
      return new LinkedHashSet(getFunctionContextContainer().getFunctionContextNamespaces());
   }

   /**
    * Adds a new function context.
    *
    * @param aName Name of the context group.
    * @param aNamespace namepsace for context, null if no default namespace for this context.
    * @param aContext The function context for the passed prefix or namespace.
    */
   protected void addFunctionContext(String aName, String aNamespace, IAeFunctionContext aContext )
   {
      getFunctionContextContainer().addFunctionContext(aName, aNamespace, aContext);
   }

   /**
    * Process all the function context entries in the passed map.  Note that this method is
    * public for unit testing purposes (the engine unit test framework calls it - see
    * AeEngineTestCase).
    *
    * @param aContextEntries The map of xpath function context entries.
    */
   public void processFunctionContexts(Map aContextEntries)
   {
      for(Iterator iter = aContextEntries.keySet().iterator(); iter.hasNext(); )
      {
         Object key = iter.next();
         Object contextObj = aContextEntries.get(key);
         if (contextObj instanceof Map)
         {
            Map context = (Map)contextObj;
            String namespace = (String)context.get(FUNCTION_CONTEXT_NAMESPACE_ENTRY);
            String contextClassName = (String)context.get(CLASS_ENTRY);
            String location = (String)context.get(FUNCTION_CONTEXT_CLASSPATH_ENTRY);

            try
            {
               IAeFunctionContext fc = getFunctionContextContainer().loadFunctionContext( namespace, location, contextClassName );
               getFunctionContextContainer().addFunctionContext( (String)key, namespace, fc );
            }
            catch( AeException ae )
            {
               ae.logError();
            }
         }
         else
         {
            AeException.logError( new AeException(AeMessages.getString("AeDefaultEngineConfiguration.11")), AeMessages.getString("AeDefaultEngineConfiguration.ERROR_14") + key); //$NON-NLS-1$ //$NON-NLS-2$
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getPartnerAddressingFactoryClassName()
    */
   public String getPartnerAddressingFactoryClassName()
   {
      return getEntry(PDEF_FACTORY_ENTRY, PDEF_FACTORY_DEFAULT);
   }

   /**
    * Setter for the partner addressing factory class name.
    * @param aClassName
    */
   public void setPartnerAddressingFactoryClassName( String aClassName )
   {
      setEntry(PDEF_FACTORY_ENTRY, aClassName);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getProcessManagerClassName()
    */
   public String getProcessManagerClassName()
   {
      return getEntry(PROCESS_MANAGER_ENTRY, PROCESS_MANAGER_DEFAULT);
   }

   /**
    * Setter for the process manager class name.
    *
    * @param aClassName
    */
   public void setProcessManagerClassName(String aClassName)
   {
      setEntry(PROCESS_MANAGER_ENTRY, aClassName);
   }

   /**
    * Load the engine configuration from the xml in the passed stream.
    * @param aConfigStream a stream containing the configuration xml.
    * @param aClassLoader the classloader for function contexts.
    * @return the config created from the passed stream.
    */
   public static AeDefaultEngineConfiguration loadConfig(InputStream aConfigStream, ClassLoader aClassLoader)
   {
      AeDefaultEngineConfiguration config = new AeDefaultEngineConfiguration();
      loadConfig(config, aConfigStream, aClassLoader);
      return config;
   }

   /**
    * Load the engine configuration from the xml in the passed stream.
    * @param aDefaultConfig the config to load
    * @param aConfigStream a stream containing the configuration xml.
    * @param aClassLoader the classloader for function contexts.
    */
   public static void loadConfig(AeDefaultEngineConfiguration aDefaultConfig, InputStream aConfigStream, ClassLoader aClassLoader)
   {
      if (aConfigStream == null)
         return;

      try
      {
         Map entries = AeConfigurationUtil.loadConfig(new InputStreamReader(aConfigStream));
         aDefaultConfig.setEntries( entries );
         aDefaultConfig.createExpressionLanguageFactory();
         aDefaultConfig.createFunctionValidatorFactory();
         aDefaultConfig.initFunctionContexts(aClassLoader);
      }
      catch(Throwable ex)
      {
         AeException.logError(ex, AeMessages.getString("AeDefaultEngineConfiguration.ERROR_16")); //$NON-NLS-1$
      }

      return;
   }

   /**
    * Initialize the function context container, the standard function contexts
    * and any custom function contexts.
    * @param aClassLoader
    */
   protected void initFunctionContexts( ClassLoader aClassLoader )
   {
      if( getFunctionContextContainer() == null )
      {
         initFunctionContextContainer( aClassLoader );
         initStandardFunctionContexts();
      }
      initCustomFunctionContexts();
   }

   /**
    * Initialize the function context container.  If a custom  <code>IAeFunctionContextLocator</code>
    * has been specified in the config, attempt to create it for the container.  If none has
    * been specified or if the creation of the custom impl fails, use the default
    * <code>AeFunctionContextLocator</code>.
    * @param aClassLoader
    */
   protected void initFunctionContextContainer( ClassLoader aClassLoader )
   {
      IAeFunctionContextLocator locator = null;
      String locatorClassName = getEntry( FUNCTION_CONTEXT_LOCATOR_ENTRY );
      try
      {
         if( AeUtil.notNullOrEmpty(locatorClassName) )
         {
            locator = (IAeFunctionContextLocator)Class.forName(locatorClassName, true, aClassLoader).newInstance();
         }
      }
      catch( Throwable t )
      {
         AeException.logError( t, AeMessages.format(ERROR_LOADING_FUNCTION_CONTEXT_LOCATOR, locatorClassName) );
      }

      if( locator == null )
      {
         locator = new AeFunctionContextLocator();
      }

      if( locator instanceof AeFunctionContextLocator )
      {
         ((AeFunctionContextLocator)locator).setDefaultClassLoader( aClassLoader );
      }

      mContextContainer = new AeFunctionContextContainer( locator );
   }

   /**
    * Initialize the standard (default, BPEL and BPEL extension) <code>FunctionContext</code>.
    */
   protected void initStandardFunctionContexts()
   {
      initBpelFunctionContexts();
      initBpelExtFunctionContext();
   }

   /**
    * Initialize the BPEL function context.  Attempt to load the BPEL
    * context from the engine config if one was specified, otherwise, use
    * the default (which is <code>AeBpelFunctionContext</code>).
    */
   protected void initBpelFunctionContexts()
   {
      Map functionContexts = getMapEntry(STANDARD_FUNCTION_CONTEXTS_ENTRY);
      IAeFunctionContext bpelFc = null;
      IAeFunctionContext bpel20Fc = null;

      if (functionContexts != null)
      {
         Map bpelContextMap = (Map) functionContexts.get(IAeEngineConfiguration.BPEL_FUNCTION_CONTEXT);
         bpelFc = createStandardContext(bpelContextMap);
         bpel20Fc = createStandardContext(bpelContextMap);
      }

      if (bpelFc == null)
         bpelFc = new AeBPWSBpelFunctionContext();
      if (bpel20Fc == null)
         bpel20Fc = new AeWSBPELBpelFunctionContext();

      getFunctionContextContainer().setBpelContext(bpelFc);
      getFunctionContextContainer().setBpel20Context(bpel20Fc);
   }

   /**
    * Initialize the BPEL extension function context.  Attempt to load the
    * BPEL extension context from the engine config if one was specified,
    * otherwise, use the default (which is <code>AeExtensionFunctionContext</code>).
    */
   protected void initBpelExtFunctionContext()
   {
      Map functionContexts = getMapEntry( STANDARD_FUNCTION_CONTEXTS_ENTRY );
      IAeFunctionContext bpelExtFc = null;

      if( functionContexts != null )
      {
         Map bpelExtContextMap = (Map)functionContexts.get(IAeEngineConfiguration.BPEL_EXT_FUNCTION_CONTEXT);
         bpelExtFc = createStandardContext( bpelExtContextMap );
      }

      if( bpelExtFc == null )
      {
         bpelExtFc = new AeExtensionFunctionContext();
      }
      getFunctionContextContainer().setBpelExtContext( bpelExtFc );
   }

   /**
    * Utility method for creating the function context from the class and
    * location entries in the given map.  Null is returned if the map is null,
    * empty, does not contain a class entry, or if there is a problem creating
    * the function context class.
    * @param aDefaultContextMap
    */
   protected IAeFunctionContext createStandardContext( Map aDefaultContextMap )
   {
      IAeFunctionContext retVal = null;

      if( aDefaultContextMap != null )
      {
         String namespace = (String) aDefaultContextMap.get(IAeEngineConfiguration.FUNCTION_CONTEXT_NAMESPACE_ENTRY);
         String className = (String) aDefaultContextMap.get(IAeEngineConfiguration.CLASS_ENTRY);
         String location = (String) aDefaultContextMap.get(IAeEngineConfiguration.FUNCTION_CONTEXT_CLASSPATH_ENTRY);

         try
         {
            if( AeUtil.notNullOrEmpty(className) )
            {
               retVal = getFunctionContextContainer().loadFunctionContext( namespace, location, className );
            }
         }
         catch( AeException ae )
         {
            AeException.logError( ae.getCause(), AeMessages.format(ERROR_LOADING_FUNCTION_CONTEXT_CLASS, className) );
         }
      }
      return retVal;
   }

   /**
    * Initialize any custom <code>FunctionContext</code> impls specified in the config.
    */
   protected void initCustomFunctionContexts()
   {
      getFunctionContextContainer().clearCustomFunctions();
      Map customContexts = getMapEntry(FUNCTION_CONTEXTS_ENTRY);
      if(customContexts != null)
      {
         processFunctionContexts( customContexts );
      }
   }

   /**
    * Setter for the logging base dir.
    * @param aBaseDir The base directory for all system log files.
    */
   protected void setLoggingBaseDir( String aBaseDir )
   {
      setEntry( LOGGING_DIR_ENTRY, aBaseDir );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getLoggingBaseDir()
    */
   public String getLoggingBaseDir()
   {
      return getEntry( LOGGING_DIR_ENTRY, LOGGING_DIR_DEFAULT );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#isAutoStart()
    */
   public boolean isAutoStart()
   {
      return getBooleanEntry(AUTO_START_ENTRY, true);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getCatalogFactoryClassName()
    */
   public String getCatalogFactoryClassName()
   {
      Map wsdlParams = getMapEntry( CATALOG_ENTRY );
      return (String)getEntryInternal(wsdlParams, CLASS_ENTRY, String.class, null);
   }

   /**
    * Sets the description for this configuration.
    */
   public void setDescription(String aDescription)
   {
      setEntry( CONFIG_DESCRIPTION_ENTRY, aDescription );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getDescription()
    */
   public String getDescription()
   {
      return getEntry( CONFIG_DESCRIPTION_ENTRY, DEFAULT_CONFIG_DESCRIPTION );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getMaxCorrelationCombinations()
    */
   public int getMaxCorrelationCombinations()
   {
      Map queueManagerMap = getMapEntry(IAeEngineConfiguration.QUEUE_MANAGER_ENTRY);
      return getIntegerEntryInternal(queueManagerMap, "MaxCorrelationCombinations", DEFAULT_MAX_JOIN_COMBINATIONS); //$NON-NLS-1$
   }

   //----------[ MUTABLE CONFIGURATION SETTINGS ]-------------------------------

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getUnmatchedCorrelatedReceiveTimeout()
    */
   public int getUnmatchedCorrelatedReceiveTimeout()
   {
      return getIntegerEntry(UNMATCHED_RECEIVE_TIMEOUT_ENTRY, UNMATCHED_RECEIVE_TIMEOUT_DEFAULT);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getWebServiceInvokeTimeout()
    */
   public int getWebServiceInvokeTimeout()
   {
      return getIntegerEntry(WEB_SERVICE_TIMEOUT, WEB_SERVICE_TIMEOUT_DEFAULT);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#setWebServiceInvokeTimeout(int)
    */
   public void setWebServiceInvokeTimeout(int aTimeout)
   {
      setIntegerEntry(WEB_SERVICE_TIMEOUT, aTimeout);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getWebServiceReceiveTimeout()
    */
   public int getWebServiceReceiveTimeout()
   {
      return getIntegerEntry(WEB_SERVICE_RECEIVE_TIMEOUT, WEB_SERVICE_TIMEOUT_DEFAULT);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#setWebServiceReceiveTimeout(int)
    */
   public void setWebServiceReceiveTimeout(int aTimeout)
   {
      setIntegerEntry(WEB_SERVICE_RECEIVE_TIMEOUT, aTimeout);
   }

   /**
    * Sets the timeout value for unmatched correlated receives.
    * @param aTimeoutValue
    */
   public void setUnmatchedCorrelatedReceiveTimeout(int aTimeoutValue)
   {
      setIntegerEntry(UNMATCHED_RECEIVE_TIMEOUT_ENTRY, aTimeoutValue);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getWorkManagerThreadPoolMax()
    */
   public int getWorkManagerThreadPoolMax()
   {
      Map workMgrMap = getMapEntry(WORK_MANAGER_ENTRY);
      return getIntegerEntryInternal( workMgrMap, WORKMANAGER_THREAD_MAX_ENTRY, WORKMANAGER_THREAD_MAX_DEFAULT );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getWorkManagerThreadPoolMin()
    */
   public int getWorkManagerThreadPoolMin()
   {
      Map workMgrMap = getMapEntry(WORK_MANAGER_ENTRY);
      return getIntegerEntryInternal( workMgrMap, WORKMANAGER_THREAD_MIN_ENTRY, WORKMANAGER_THREAD_MIN_DEFAULT );
   }

   /**
    * Setter for the work manager thread pool max thread count
    * @param aInt
    */
   public void setWorkManagerThreadPoolMax(int aInt)
   {
      Map workMgrMap = getMapEntry(WORK_MANAGER_ENTRY, true);
      workMgrMap.put(WORKMANAGER_THREAD_MAX_ENTRY, Integer.toString(aInt));
   }

   /**
    * Setter for the work manager thread pool min thread count
    * @param aInt
    */
   public void setWorkManagerThreadPoolMin(int aInt)
   {
      Map workMgrMap = getMapEntry(WORK_MANAGER_ENTRY, true);
      workMgrMap.put(WORKMANAGER_THREAD_MIN_ENTRY, Integer.toString(aInt));
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#allowEmptyQuerySelection()
    */
   public boolean allowEmptyQuerySelection()
   {
      return getBooleanEntry(ALLOW_EMPTY_QUERY_SELECTION_ENTRY, false);
   }

   /**
    * Sets the allow empty query selection results on or off.
    */
   public void setAllowEmptyQuerySelection(boolean aBoolean)
   {
      setBooleanEntry(ALLOW_EMPTY_QUERY_SELECTION_ENTRY, aBoolean);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#allowCreateXPath()
    */
   public boolean allowCreateXPath()
   {
      return getBooleanEntry(ALLOW_CREATE_XPATH_ENTRY, false);
   }

   /**
    * Sets the allow create xpath option on or off.
    */
   public void setAllowCreateXPath(boolean aBoolean)
   {
      setBooleanEntry(ALLOW_CREATE_XPATH_ENTRY, aBoolean);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#validateServiceMessages()
    */
   public boolean validateServiceMessages()
   {
      return getBooleanEntry(VALIDATE_SERVICE_MESSAGES_ENTRY, false);
   }

   /**
    * Sets whether inbound and outbound messages are validated.
    */
   public void setValidateServiceMessages(boolean aBoolean)
   {
      setBooleanEntry(VALIDATE_SERVICE_MESSAGES_ENTRY, aBoolean);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getLoggingFilter()
    */
   public String getLoggingFilter()
   {
      return getEntry(LOGGING_ENTRY);
   }

   /**
    * Setter for logging property.
    * @param aFilterName
    */
   public void setLoggingFilter(String aFilterName)
   {
      setEntry(LOGGING_ENTRY, aFilterName);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#setResourceCacheMax(int)
    */
   public void setResourceCacheMax(int aMax)
   {
      Map params = getMapEntry(CATALOG_ENTRY);
      params.put( RESOURCE_FACTORY_CACHE_SIZE_ENTRY, String.valueOf(aMax) );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getResourceCacheMax()
    */
   public int getResourceCacheMax()
   {
      Map params = getMapEntry( CATALOG_ENTRY );
      return getIntegerEntryInternal( params, RESOURCE_FACTORY_CACHE_SIZE_ENTRY, DEFAULT_RESOURCE_CACHE_MAX );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#isAllowedRolesEnforced()
    */
   public boolean isAllowedRolesEnforced()
   {
      return getBooleanEntry(ALLOWED_ROLES_ENFORCED, true);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#setAllowedRolesEnforced(boolean)
    */
   public void setAllowedRolesEnforced(boolean aFlag)
   {
      setBooleanEntry(ALLOWED_ROLES_ENFORCED, aFlag);
   }

   //----------[ CHANGE LISTENER METHODS ]--------------------------------------

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#update()
    */
   public void update()
   {
      notifyListeners();
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#addConfigChangeListener(org.activebpel.rt.bpel.config.IAeConfigChangeListener)
    */
   public synchronized void addConfigChangeListener(IAeConfigChangeListener aListener)
   {
      mListeners.add( aListener );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#removeConfigChangeListener(org.activebpel.rt.bpel.config.IAeConfigChangeListener)
    */
   public synchronized void removeConfigChangeListener(IAeConfigChangeListener aListener)
   {
      mListeners.remove(aListener);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#addStorageChangeListener(org.activebpel.rt.bpel.config.IAeStorageChangeListener)
    */
   public synchronized void addStorageChangeListener(IAeStorageChangeListener aListener)
   {
      mStorageListeners.add( aListener );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#removeStorageChangeListener(org.activebpel.rt.bpel.config.IAeStorageChangeListener)
    */
   public synchronized void removeStorageChangeListener(IAeStorageChangeListener aListener)
   {
      mStorageListeners.remove(aListener);
   }

   /**
    * Notify any registered listeners of a config change.
    */
   protected void notifyListeners()
   {
      List listeners = null;
      synchronized( mListeners )
      {
         if( !mListeners.isEmpty() )
         {
            listeners = new ArrayList(mListeners);
         }
      }

      if( listeners != null )
      {
         for( Iterator iter = listeners.iterator(); iter.hasNext(); )
         {
            IAeConfigChangeListener listener = (IAeConfigChangeListener) iter.next();
            listener.updateConfig( this );
         }
      }
   }

   /**
    * Walk all of the listeners and notify them of the change.
    *
    * @param aMap
    */
   protected void notifyStorageListeners(Map aMap)
   {
      List listeners = null;
      synchronized( mStorageListeners )
      {
         if( !mStorageListeners.isEmpty() )
         {
            listeners = new ArrayList(mStorageListeners);
         }
      }

      if( listeners != null )
      {
         for( Iterator iter = listeners.iterator(); iter.hasNext(); )
         {
            IAeStorageChangeListener listener = (IAeStorageChangeListener) iter.next();
            listener.storageConstantsChanged( aMap );
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getUpdatableEngineConfig()
    */
   public IAeUpdatableEngineConfig getUpdatableEngineConfig()
   {
      return this;
   }

   /**
    * Accessor for <code>AeFunctionContextContainer</code>.
    */
   protected AeFunctionContextContainer getFunctionContextContainer()
   {
      return mContextContainer;
   }

   /**
    * This clone simply returns a config that contains the same map entries. It
    * does not include the listeners or the function context container.
    *
    * @see java.lang.Object#clone()
    */
   public Object clone()
   {
      AeDefaultEngineConfiguration copy = new AeDefaultEngineConfiguration();
      HashMap map = new HashMap(getEntries());
      copy.setEntries(map);
      return copy;
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#isResourceReplaceEnabled()
    */
   public boolean isResourceReplaceEnabled()
   {
      Map wsdlParams = getMapEntry( CATALOG_ENTRY );
      return getBooleanEntryInternal( wsdlParams, REPLACE_EXISTING_ENTRY, false );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#setResourceReplaceEnabled(boolean)
    */
   public void setResourceReplaceEnabled(boolean aFlag)
   {
      Map params = getMapEntry(CATALOG_ENTRY);
      params.put( REPLACE_EXISTING_ENTRY, Boolean.toString(aFlag) );
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#isSuspendProcessOnUncaughtFault()
    */
   public boolean isSuspendProcessOnUncaughtFault()
   {
      return getBooleanEntry(SUSPEND_PROCESS_ON_UNCAUGHT_FAULT_ENTRY, false);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#setSuspendProcessOnUncaughtFault(boolean)
    */
   public void setSuspendProcessOnUncaughtFault(boolean aFlag)
   {
      setBooleanEntry(SUSPEND_PROCESS_ON_UNCAUGHT_FAULT_ENTRY, aFlag);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#getProcessWorkCount()
    */
   public int getProcessWorkCount()
   {
      Map map = getMapEntry(PROCESS_WORK_MANAGER_ENTRY);
      return getIntegerEntryInternal(map, PROCESS_WORK_COUNT_ENTRY, PROCESS_WORK_COUNT_DEFAULT);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#setProcessWorkCount(int)
    */
   public void setProcessWorkCount(int aProcessWorkCount)
   {
      Map map = getMapEntry(PROCESS_WORK_MANAGER_ENTRY);
      map.put(PROCESS_WORK_COUNT_ENTRY, String.valueOf(aProcessWorkCount));
   }

   /**
    * This method takes a configuration map for a config class and instantiates that
    * class.  This involves some simple java reflection to find the proper
    * constructor and then calling that constructor.  Any class can use this method,
    * as long as the class in question has a constructor that takes the configuration
    * map.  Returns null if no entry is found.
    *
    * @param aConfigName The name of a MapEntry in the config.
    * @throws AeException
    * @return A config class.
    */
   public Object createConfigSpecificClass(String aConfigName) throws AeException
   {
      return AeConfigurationUtil.createConfigSpecificClass(getMapEntry(aConfigName));
   }

   /**
    * Register function validators where object is of the type IAeFunctionValidator.
    *
    * @param aConfigMap configuration map of function validator entries.
    * @param aFactory
    * @throws AeException
    */
   public void registerFunctionValidators(Map aConfigMap, AeFunctionValidatorFactory aFactory) throws AeException
   {
      try
      {
         if (aConfigMap != null)
         {
            // loop over the entries in the configMap that represent the BPEL versions
            for (Iterator typeIter = aConfigMap.entrySet().iterator(); typeIter.hasNext();)
            {
               Entry type = (Entry) typeIter.next();
               String bpelNamespace = String.valueOf(type.getKey());

               // get the function contexts and loop over those for access to the functions within that context
               Map functionContextsMap = (Map) type.getValue();
               for (Iterator functionContextsIter = functionContextsMap.entrySet().iterator(); functionContextsIter.hasNext();)
               {
                  Entry functionContextEntry = (Entry) functionContextsIter.next();
                  // get the function context namespace to be used when creating the QName
                  String functionContextNamespace = (String) functionContextEntry.getKey();

                  // get the functions within a context
                  Map functionsMap = (Map) functionContextEntry.getValue();
                  for(Iterator functionsMapIter = functionsMap.entrySet().iterator(); functionsMapIter.hasNext();)
                  {
                     Entry entry = (Entry) functionsMapIter.next();

                     // get the function name and instance of the validator
                     String functionLocalName = (String) entry.getKey();
                     Object validatorInstance = AeConfigurationUtil.createConfigSpecificClass((Map) entry.getValue());

                     // this validator instance will only be added if it is an instance of IAeFunctionValidator
                     if (validatorInstance != null && (validatorInstance instanceof IAeFunctionValidator))
                     {
                        QName functionQName = new QName(functionContextNamespace, functionLocalName);

                        // register the validator to the correct map in the factory.  If the bpelNamespace
                        // is not present in the configMap, then register the function to all bpel namespaces
                        // in the factory map.
                        if(AeUtil.isNullOrEmpty(bpelNamespace))
                        {
                           aFactory.registerValidator(functionQName, (IAeFunctionValidator) validatorInstance);
                        }
                        else
                        {
                           aFactory.registerValidator(bpelNamespace, functionQName, (IAeFunctionValidator) validatorInstance);
                        }
                     }
                  }
               }
            }
         }
      }
      catch(Exception ex)
      {
         throw new AeException(ex.getMessage(), ex);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#isSuspendProcessOnInvokeRecovery()
    */
   public boolean isSuspendProcessOnInvokeRecovery()
   {
      return getBooleanEntry(SUSPEND_PROCESS_ON_INVOKE_RECOVERY_ENTRY, false);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#setSuspendProcessOnInvokeRecovery(boolean)
    */
   public void setSuspendProcessOnInvokeRecovery(boolean aFlag)
   {
      setBooleanEntry(SUSPEND_PROCESS_ON_INVOKE_RECOVERY_ENTRY, aFlag);
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeEngineConfiguration#isProcessRestartEnabled()
    */
   public boolean isProcessRestartEnabled()
   {
      return getBooleanEntry(RESTART_SUSPENDED_PROCESS, false);
   }
}
