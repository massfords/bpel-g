package org.activebpel.rt.bpel.expr;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzer;
import org.activebpel.rt.expr.validation.IAeExpressionValidator;

/**
 * This internal class represents the information about a specific expression language as 
 * defined in the engine configuration.  Each expression language info will include the
 * URI of the language, the language name, and impl classes for validation and execution.
 */
public class AeExpressionLanguageInfo
{
   /** The name. */
   private String mName;
   /** The expression URI as found in the expressionLanguage attribute of the BPEL process element. */
   private String mUri;
   /** The classname of a class that implements IAeExpressionValidator. */
   private AeExpressionLanguageClassInfo mValidatorInfo;
   /** The classname of a class that implements IAeExpressionRunner. */
   private AeExpressionLanguageClassInfo mRunnerInfo;
   /** The classname of a class that implements IAeExpressionAnalyzer. */
   private AeExpressionLanguageClassInfo mAnalyzerInfo;

   /**
    * Constructs a language info with all the necessary info.  The validator and runner can be 
    * either a String or a Map.  If a String, it should be the fully qualified classname of a
    * class implementing the IAeExpressionValidator or IAeExpressionRunner interface (respectively).
    * If a Map, the map should contain an entry called "Class" which contains the aforementioned
    * class.  In addition, the Map can contain additional parameters (if the Map contains a map
    * entry called "Params") that will be passed to the constructor of the class.
    * 
    * @param aName
    * @param aUri
    * @param aValidator
    * @param aRunner
    * @param aAnalyzer
    * @param aClasspath
    * @param aClassloader
    * @throws AeException
    */
   public AeExpressionLanguageInfo(String aName, String aUri, Object aValidator, Object aRunner, Object aAnalyzer,
         String aClasspath, ClassLoader aClassloader) throws AeException
   {
      setName(aName);
      setUri(aUri);
      setValidatorInfo(createClassInfo(aValidator, aClasspath, aClassloader));
      setRunnerInfo(createClassInfo(aRunner, aClasspath, aClassloader));
      setAnalyzerInfo(createClassInfo(aAnalyzer, aClasspath, aClassloader));
   }

   /**
    * Creates a class info object from the given meta data and classpath.
    * 
    * @param aClassObj
    * @param aClasspath
    * @param aClassloader
    */
   protected AeExpressionLanguageClassInfo createClassInfo(Object aClassObj, String aClasspath,
         ClassLoader aClassloader) throws AeException
   {
      return new AeExpressionLanguageClassInfo(aClassObj, aClasspath, aClassloader);
   }

   /**
    * @return Returns the name.
    */
   public String getName()
   {
      return mName;
   }

   /**
    * @param aName The name to set.
    */
   protected void setName(String aName)
   {
      mName = aName;
   }

   /**
    * @return Returns the uri.
    */
   public String getUri()
   {
      return mUri;
   }

   /**
    * @param aUri The uri to set.
    */
   protected void setUri(String aUri)
   {
      mUri = aUri;
   }

   /**
    * Gets an expression runner for this expression language.
    */
   public IAeExpressionRunner getRunner()
   {
      try
      {
         return (IAeExpressionRunner) getRunnerInfo().newInstance();
      }
      catch (AeException ae)
      {
         ae.logError();
         return null;
      }
   }

   /**
    * Gets an expression validator for this expression language.
    */
   public IAeExpressionValidator getValidator()
   {
      try
      {
         return (IAeExpressionValidator) getValidatorInfo().newInstance();
      }
      catch (AeException ae)
      {
         ae.logError();
         return null;
      }
   }

   /**
    * Gets an expression analyzer for this expression language.
    */
   public IAeExpressionAnalyzer getAnalyzer()
   {
      try
      {
         return (IAeExpressionAnalyzer) getAnalyzerInfo().newInstance();
      }
      catch (AeException ae)
      {
         ae.logError();
         return null;
      }
   }
   
   /**
    * @return Returns the runnerInfo.
    */
   protected AeExpressionLanguageClassInfo getRunnerInfo()
   {
      return mRunnerInfo;
   }
   
   /**
    * @param aRunnerInfo The runnerInfo to set.
    */
   protected void setRunnerInfo(AeExpressionLanguageClassInfo aRunnerInfo)
   {
      mRunnerInfo = aRunnerInfo;
   }
   
   /**
    * @return Returns the validatorInfo.
    */
   protected AeExpressionLanguageClassInfo getValidatorInfo()
   {
      return mValidatorInfo;
   }
   
   /**
    * @param aValidatorInfo The validatorInfo to set.
    */
   protected void setValidatorInfo(AeExpressionLanguageClassInfo aValidatorInfo)
   {
      mValidatorInfo = aValidatorInfo;
   }
   
   /**
    * @return Returns the analyzerInfo.
    */
   protected AeExpressionLanguageClassInfo getAnalyzerInfo()
   {
      return mAnalyzerInfo;
   }
   
   /**
    * @param aAnalyzerInfo The analyzerInfo to set.
    */
   protected void setAnalyzerInfo(AeExpressionLanguageClassInfo aAnalyzerInfo)
   {
      mAnalyzerInfo = aAnalyzerInfo;
   }
}