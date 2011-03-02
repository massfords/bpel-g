//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/readers/def/IAeCopyOperationStrategyMatcher.java,v 1.4.16.1 2008/04/21 16:09:42 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.readers.def; 

import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;

/**
 * implements the matching logic for determining the strategy to use for a &lt;from&gt; or &lt;to&gt; 
 * def in a copy operation 
 */
public interface IAeCopyOperationStrategyMatcher
{
   /**
    * Gets the strategy to use for the from def
    * @param aFromDef
    * @param aVarDef optional since some defs don't reference variables directly (i.e. plink)
    */
   public AeSpecStrategyKey getStrategy(AeFromDef aFromDef, AeVariableDef aVarDef);

   /**
    * Gets the strategy to use for the to def
    * @param aToDef
    * @param aVarDef optional since some defs don't reference variables directly (i.e. plink or query)
    * @param aExpressionLanguageFactory
    */
   public AeSpecStrategyKey getStrategy(AeToDef aToDef, AeVariableDef aVarDef,
         IAeExpressionLanguageFactory aExpressionLanguageFactory);
}
 