//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeExpressionLanguageFactory.java,v 1.3 2008/01/25 21:01:17 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzer;
import org.activebpel.rt.expr.validation.IAeExpressionValidator;

/**
 * This interface must be implemented by any classes desiring to function as an expression
 * language factory.  An expression language factory is responsible for create expression
 * validators and expression runners for different types of expression languages.
 */
public interface IAeExpressionLanguageFactory
{
   /**
    * Given a language identifier found in the 'expressionLanguage' attribute of the BPEL 'process'
    * element (or in a child BPEL constructs in 2.0), returns an expression validator capable of 
    * validating expressions written in that language.  The BPEL process namespace must also be
    * passed, since the behavior is different for different BPEL versions.
    * 
    * @param aBpelNamespace
    * @param aLanguageUri
    * @throws AeException
    */
   public IAeExpressionValidator createExpressionValidator(String aBpelNamespace, String aLanguageUri) throws AeException;

   /**
    * Given a language identifier found in the 'expressionLanguage' attribute of the BPEL 'process'
    * element (or in a child BPEL constructs in 2.0), returns an expression runner capable of 
    * executing expressions written in that language.  The BPEL process namespace must also be
    * passed, since the behavior is different for different BPEL versions.
    * 
    * @param aBpelNamespace
    * @param aLanguageUri
    * @throws AeException
    */
   public IAeExpressionRunner createExpressionRunner(String aBpelNamespace, String aLanguageUri) throws AeException;

   /**
    * Given a language identifier found in the 'expressionLanguage' attribute of the BPEL 'process'
    * element (or in a child BPEL constructs in 2.0), returns an expression analyzer capable of 
    * parsing and analyzing expressions written in that language.  The BPEL process namespace 
    * must also be passed, since the behavior is different for different BPEL versions.
    * 
    * @param aBpelNamespace
    * @param aLanguageUri
    * @throws AeException
    */
   public IAeExpressionAnalyzer createExpressionAnalyzer(String aBpelNamespace, String aLanguageUri) throws AeException;

   /**
    * Returns true if the given language is supported by this expression language factory.  If
    * null or empty string is passed in, then this method should return true if the BPEL default
    * language is supported (xpath 1.0).
    * 
    * @param aBpelNamespace
    * @param aLanguageUri
    */
   public boolean supportsLanguage(String aBpelNamespace, String aLanguageUri);
   
   /**
    * Returns true if the given language URI is the default language URI for the version of BPEL
    * identified by the given BPEL namespace.
    * 
    * @param aBpelNamespace
    * @param aLanguageUri
    */
   public boolean isBpelDefaultLanguage(String aBpelNamespace, String aLanguageUri);
   
   /**
    * Returns the default language URI for the version of BPEL indicated by the given bpel namespace.
    * 
    * @param aBpelNamespace
    */
   public String getBpelDefaultLanguage(String aBpelNamespace);
}
