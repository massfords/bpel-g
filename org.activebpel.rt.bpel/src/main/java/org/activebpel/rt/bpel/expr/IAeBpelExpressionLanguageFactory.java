// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.expr;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzer;
import org.activebpel.rt.expr.validation.IAeExpressionValidator;

/**
 * Interface for getting expression validators, runners, analyzers, etc for expressions written
 * in various languages.  This interface is implemented by BPEL-version specific implementations.
 */
public interface IAeBpelExpressionLanguageFactory
{
   /**
    * Given a language identifier found in the 'expressionLanguage' attribute of the BPEL 'process'
    * element (or in a child BPEL constructs in 2.0), returns an expression validator capable of 
    * validating expressions written in that language.
    * 
    * @param aLanguageUri
    * @throws AeException
    */
   public IAeExpressionValidator createExpressionValidator(String aLanguageUri) throws AeException;

   /**
    * Given a language identifier found in the 'expressionLanguage' attribute of the BPEL 'process'
    * element (or in a child BPEL constructs in 2.0), returns an expression runner capable of 
    * executing expressions written in that language.
    * 
    * @param aLanguageUri
    * @throws AeException
    */
   public IAeExpressionRunner createExpressionRunner(String aLanguageUri) throws AeException;

   /**
    * Given a language identifier found in the 'expressionLanguage' attribute of the BPEL 'process'
    * element (or in a child BPEL constructs in 2.0), returns an expression analyzer capable of 
    * parsing and analyzing expressions written in that language.
    * 
    * @param aLanguageUri
    * @throws AeException
    */
   public IAeExpressionAnalyzer createExpressionAnalyzer(String aLanguageUri) throws AeException;

   /**
    * Returns true if the given language is supported by this expression language factory.  If
    * null or empty string is passed in, then this method should return true if the BPEL default
    * language is supported (xpath 1.0).
    * 
    * @param aLanguageUri
    */
   public boolean supportsLanguage(String aLanguageUri);
   
   /**
    * Returns true if the given language URI is the default language URI.
    * 
    * @param aLanguageUri
    */
   public boolean isBpelDefaultLanguage(String aLanguageUri);

   /**
    * Returns the default language URI.
    */
   public String getBpelDefaultLanguage();
}
