//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/expr/AeWSBPELExpressionLanguageFactory.java,v 1.2 2007/06/10 19:07:57 EWittmann Exp $
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
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.expr.xpath.AeWSBPELXPathExpressionAnalyzer;
import org.activebpel.rt.bpel.def.validation.expr.xpath.AeWSBPELXPathExpressionValidator;
import org.activebpel.rt.bpel.impl.expr.xpath.AeWSBPELXPathExpressionRunner;

/**
 * This implementation of the expression language factory uses the engine configuration file to
 * map expression languages to implementations of validators and runners.
 */
public class AeWSBPELExpressionLanguageFactory extends AeAbstractBpelExpressionLanguageFactory
{
   /** The map/list of default languages when no config info is found in aeEngineConfig.xml. */
   private static Map sDefaultLanguages = new HashMap();

   /**
    * Initializes above languages map.
    */
   static 
   {
      try
      {
         Map map = new HashMap();
         map.put(AeAbstractBpelExpressionLanguageFactory.LANGUAGE_URI_KEY, IAeBPELConstants.WSBPEL_EXPR_LANGUAGE_URI);
         map.put(AeAbstractBpelExpressionLanguageFactory.LANGUAGE_VALIDATOR_KEY, AeWSBPELXPathExpressionValidator.class.getName());
         map.put(AeAbstractBpelExpressionLanguageFactory.LANGUAGE_RUNNER_KEY, AeWSBPELXPathExpressionRunner.class.getName());
         map.put(AeAbstractBpelExpressionLanguageFactory.LANGUAGE_ANALYZER_KEY, AeWSBPELXPathExpressionAnalyzer.class.getName());
         sDefaultLanguages.put("XPath1.0", map); //$NON-NLS-1$
      }
      catch (Throwable t)
      {
         AeException.logError(t, t.getLocalizedMessage());
      }
   }
   

   /**
    * Constructs an expression language factory using the given engine configuration map.
    * 
    * @param aConfig
    * @param aClassloader
    */
   public AeWSBPELExpressionLanguageFactory(Map aConfig, ClassLoader aClassloader)
   {
      super(aConfig, aClassloader);
   }

   /**
    * Constructs a default expression language factory that supports only XPath 1.0.
    *
    */
   public AeWSBPELExpressionLanguageFactory()
   {
      super();
   }
   
   /**
    * @see org.activebpel.rt.bpel.expr.IAeBpelExpressionLanguageFactory#getBpelDefaultLanguage()
    */
   public String getBpelDefaultLanguage()
   {
      return IAeBPELConstants.WSBPEL_EXPR_LANGUAGE_URI;
   }
   
   /**
    * @see org.activebpel.rt.bpel.expr.AeAbstractBpelExpressionLanguageFactory#getDefaultLanguages()
    */
   protected Map getDefaultLanguages()
   {
      return sDefaultLanguages;
   }
}
