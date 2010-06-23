//$Header$
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.expr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzer;
import org.activebpel.rt.expr.validation.IAeExpressionValidator;
import org.activebpel.rt.util.AeUtil;

/**
 * This implementation of the expression language factory uses the engine configuration file to
 * map expression languages to implementations of validators and runners.
 */
public abstract class AeAbstractBpelExpressionLanguageFactory implements IAeBpelExpressionLanguageFactory
{
   /** The config map key to get the default language. */
   public static final String DEFAULT_LANGUAGE_KEY = "DefaultLanguage"; //$NON-NLS-1$
   /** The config map key to get the list of languages. */
   public static final String LANGUAGES_KEY = "Languages"; //$NON-NLS-1$
   /** The config map key to get the language URI. */
   public static final String LANGUAGE_URI_KEY = "Uri"; //$NON-NLS-1$
   /** The config map key to get the language validator classname. */
   public static final String LANGUAGE_VALIDATOR_KEY = "Validator"; //$NON-NLS-1$
   /** The config map key to get the language runner classname. */
   public static final String LANGUAGE_RUNNER_KEY = "Runner"; //$NON-NLS-1$
   /** The config map key to get the language classpath. */
   public static final String LANGUAGE_CLASSPATH_KEY = "Classpath"; //$NON-NLS-1$
   /** The config map key to get the language util classname. */
   public static final String LANGUAGE_ANALYZER_KEY = "Analyzer"; //$NON-NLS-1$
   
   /** The classloader to use when instantiating the objects. */
   private ClassLoader mClassloader;
   /** The map of language URI -> language info object. */
   private Map mLanguageMap;
   /** The default language to use if none is specified. */
   private String mDefaultLanguage;

   /**
    * Constructs an expression language factory using the given engine configuration map.
    * 
    * @param aConfig
    * @param aClassloader
    */
   public AeAbstractBpelExpressionLanguageFactory(Map aConfig, ClassLoader aClassloader)
   {
      setLanguageMap(new HashMap());
      setClassloader(aClassloader);

      // Set the default expression language.
      String defLanguage = (String) aConfig.get(DEFAULT_LANGUAGE_KEY);
      if (AeUtil.isNullOrEmpty(defLanguage))
      {
         defLanguage = getBpelDefaultLanguage();
      }
      setDefaultLanguage(defLanguage);

      // Iterate through all the languages.
      Map languages = (Map) aConfig.get(LANGUAGES_KEY);
      try
      {
         if (languages == null)
         {
            addDefaultLanguages();
         }
         else
         {
            addLanguages(languages);
         }
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
   public AeAbstractBpelExpressionLanguageFactory()
   {
      setLanguageMap(new HashMap());
      setDefaultLanguage(getBpelDefaultLanguage());

      try
      {
         addDefaultLanguages();
      }
      catch (AeException e)
      {
         e.logError();
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.expr.IAeBpelExpressionLanguageFactory#supportsLanguage(java.lang.String)
    */
   public boolean supportsLanguage(String aLanguageUri)
   {
      String lang = resolveLanguageUri(aLanguageUri);
      return getLanguageMap().containsKey(lang);
   }

   /**
    * @see org.activebpel.rt.bpel.expr.IAeBpelExpressionLanguageFactory#isBpelDefaultLanguage(java.lang.String)
    */
   public boolean isBpelDefaultLanguage(String aLanguageUri)
   {
      return getBpelDefaultLanguage().equals(aLanguageUri);
   }
   
   /**
    * Adds all of the languages found in the supplied Map of languages to the language map.
    * 
    * @param aLanguages
    */
   protected void addLanguages(Map aLanguages) throws AeException
   {
      for (Iterator iter = aLanguages.keySet().iterator(); iter.hasNext(); )
      {
         String entryName = (String) iter.next();
         Map map = (Map) aLanguages.get(entryName);
         addLanguage(entryName, map);
      }
   }

   /**
    * Adds the default languages to the language map.
    * 
    * @throws AeException
    */
   protected void addDefaultLanguages() throws AeException
   {
      for (Iterator iter = getDefaultLanguages().keySet().iterator(); iter.hasNext(); )
      {
         String name = (String) iter.next();
         Map language = (Map) getDefaultLanguages().get(name);
         addLanguage(name, language);
      }
   }
   
   /**
    * Returns the default languages map.
    */
   protected abstract Map getDefaultLanguages();

   /**
    * Adds a language to the factory.  The map contains all of the pieces of information (found
    * in the engine config) needed to fully describe support for a single expression language.
    * This includes the URL of the language, its name, and implementation classes for validation
    * and execution.
    * 
    * @param aMap
    */
   protected void addLanguage(String aName, Map aMap) throws AeException
   {
      String uri = (String) aMap.get(LANGUAGE_URI_KEY);

      getLanguageMap().put(uri, createLanguageInfo(aName, aMap));
   }

   /**
    * Creates a language info object for the language name and map of properties.
    * 
    * @param aName
    * @param aMap
    * @throws AeException
    */
   protected AeExpressionLanguageInfo createLanguageInfo(String aName, Map aMap) throws AeException
   {
      String uri = (String) aMap.get(LANGUAGE_URI_KEY);
      Object validator = aMap.get(LANGUAGE_VALIDATOR_KEY);
      Object runner = aMap.get(LANGUAGE_RUNNER_KEY);
      Object analyzer = aMap.get(LANGUAGE_ANALYZER_KEY);
      String classpath = (String) aMap.get(LANGUAGE_CLASSPATH_KEY);

      return createLanguageInfo(aName, uri, validator, runner, analyzer, classpath);
   }

   /**
    * Creates the language info object given the list of required items.
    * 
    * @param aName
    * @param aUri
    * @param aValidator
    * @param aRunner
    * @param aAnalyzer
    * @param aClasspath
    * @throws AeException
    */
   protected AeExpressionLanguageInfo createLanguageInfo(String aName, String aUri, Object aValidator, Object aRunner,
         Object aAnalyzer, String aClasspath) throws AeException
   {
      return new AeExpressionLanguageInfo(aName, aUri, aValidator, aRunner, aAnalyzer, aClasspath, getClassloader());
   }

   /**
    * @see org.activebpel.rt.bpel.expr.IAeBpelExpressionLanguageFactory#createExpressionValidator(java.lang.String)
    */
   public IAeExpressionValidator createExpressionValidator(String aLanguage) throws AeException
   {
      String language = resolveLanguageUri(aLanguage);

      AeExpressionLanguageInfo info = (AeExpressionLanguageInfo) getLanguageMap().get(language);
      if (info == null)
      {
         throw new AeException(AeMessages.format("AeAbstractBpelExpressionLanguageFactory.UNKNOWN_EXPR_LANGUAGE_ERROR", language)); //$NON-NLS-1$
      }
      IAeExpressionValidator validator = info.getValidator();
      if (validator == null)
      {
         throw new AeException(AeMessages.format("AeAbstractBpelExpressionLanguageFactory.NO_VALIDATOR_ERROR", language)); //$NON-NLS-1$
      }
      return validator;
   }

   /**
    * @see org.activebpel.rt.bpel.expr.IAeBpelExpressionLanguageFactory#createExpressionAnalyzer(java.lang.String)
    */
   public IAeExpressionAnalyzer createExpressionAnalyzer(String aLanguage) throws AeException
   {
      String language = resolveLanguageUri(aLanguage);

      AeExpressionLanguageInfo info = (AeExpressionLanguageInfo) getLanguageMap().get(language);
      if (info == null)
      {
         throw new AeException(AeMessages.format("AeAbstractBpelExpressionLanguageFactory.UNKNOWN_EXPR_LANGUAGE_ERROR", language)); //$NON-NLS-1$
      }
      IAeExpressionAnalyzer analyzer = info.getAnalyzer();
      if (analyzer == null)
      {
         throw new AeException(AeMessages.format("AeAbstractBpelExpressionLanguageFactory.COULD_NOT_FIND_ANALYZER_ERROR", language)); //$NON-NLS-1$
      }
      return analyzer;
   }

   /**
    * @see org.activebpel.rt.bpel.expr.IAeBpelExpressionLanguageFactory#createExpressionRunner(java.lang.String)
    */
   public IAeExpressionRunner createExpressionRunner(String aLanguage) throws AeException
   {
      String language = resolveLanguageUri(aLanguage);

      AeExpressionLanguageInfo info = (AeExpressionLanguageInfo) getLanguageMap().get(language);
      if (info == null)
      {
         throw new AeException(AeMessages.format("AeAbstractBpelExpressionLanguageFactory.UNKNOWN_EXPR_LANGUAGE_ERROR", language)); //$NON-NLS-1$
      }
      IAeExpressionRunner runner = info.getRunner();
      if (runner == null)
      {
         throw new AeException(AeMessages.format("AeAbstractBpelExpressionLanguageFactory.NO_RUNNER_ERROR", language)); //$NON-NLS-1$
      }
      return runner;
   }
   
   /**
    * This method resolves a language into a language URI.  The value that is passed in
    * to this method is whatever is in the 'expressionLanguage' attribute of the BPEL
    * 'process' element.  In most cases, the value of this attribute will be the URI of
    * the language itself, so nothing is done.  However, an implementation of this 
    * factory could map short names to URIs, for example.  In addition, this method is
    * used to determine the default expression language if no value is specified in the
    * 'expressionLanguage' attribute.  By default (and unless overridden in the engine
    * config), the default language will be XPath 1.0 (as written in the BPEL spec).
    * 
    * @param aLanguage
    */
   protected String resolveLanguageUri(String aLanguage)
   {
      if (AeUtil.isNullOrEmpty(aLanguage))
         return getDefaultLanguage();
      else
         return aLanguage;
   }
   
   /**
    * @return Returns the languageMap.
    */
   public Map getLanguageMap()
   {
      return mLanguageMap;
   }
   
   /**
    * @param aLanguageMap The languageMap to set.
    */
   public void setLanguageMap(Map aLanguageMap)
   {
      mLanguageMap = aLanguageMap;
   }

   /**
    * @return Returns the defaultLanguage.
    */
   public String getDefaultLanguage()
   {
      return mDefaultLanguage;
   }
   
   /**
    * @param aDefaultLanguage The defaultLanguage to set.
    */
   public void setDefaultLanguage(String aDefaultLanguage)
   {
      mDefaultLanguage = aDefaultLanguage;
   }

   /**
    * @return Returns the classloader.
    */
   protected ClassLoader getClassloader()
   {
      return mClassloader;
   }

   /**
    * @param aClassloader The classloader to set.
    */
   protected void setClassloader(ClassLoader aClassloader)
   {
      mClassloader = aClassloader;
   }
}
