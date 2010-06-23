//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/expr/AeBPWSExpressionLanguageFactory.java,v 1.3 2007/06/10 19:07:57 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.expr;

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.expr.xpath.AeBPWSXPathExpressionAnalyzer;
import org.activebpel.rt.bpel.def.validation.expr.xpath.AeBPWSXPathExpressionValidator;
import org.activebpel.rt.bpel.impl.expr.xpath.AeBPWSXPathExpressionRunner;

/**
 * This implementation of the expression language factory uses the engine configuration file to
 * map expression languages to implementations of validators and runners.
 */
public class AeBPWSExpressionLanguageFactory extends AeAbstractBpelExpressionLanguageFactory
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
         map.put(AeAbstractBpelExpressionLanguageFactory.LANGUAGE_URI_KEY, IAeBPELConstants.BPWS_XPATH_EXPR_LANGUAGE_URI);
         map.put(AeAbstractBpelExpressionLanguageFactory.LANGUAGE_VALIDATOR_KEY, AeBPWSXPathExpressionValidator.class.getName());
         map.put(AeAbstractBpelExpressionLanguageFactory.LANGUAGE_RUNNER_KEY, AeBPWSXPathExpressionRunner.class.getName());
         map.put(AeAbstractBpelExpressionLanguageFactory.LANGUAGE_ANALYZER_KEY, AeBPWSXPathExpressionAnalyzer.class.getName());
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
   public AeBPWSExpressionLanguageFactory(Map aConfig, ClassLoader aClassloader)
   {
      super(aConfig, aClassloader);
   }

   /**
    * Constructs a default expression language factory that supports only XPath 1.0.
    *
    */
   public AeBPWSExpressionLanguageFactory()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.expr.AeAbstractBpelExpressionLanguageFactory#getDefaultLanguages()
    */
   protected Map getDefaultLanguages()
   {
      return sDefaultLanguages;
   }

   /**
    * @see org.activebpel.rt.bpel.expr.IAeBpelExpressionLanguageFactory#getBpelDefaultLanguage()
    */
   public String getBpelDefaultLanguage()
   {
      return IAeBPELConstants.BPWS_XPATH_EXPR_LANGUAGE_URI;
   }
}
