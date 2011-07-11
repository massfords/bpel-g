//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/expressions/AeForEachCompletionConditionValidator.java,v 1.2 2006/10/05 21:15:31 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.expressions; 

import org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * model provides validation for completionCondition def
 */
public class AeForEachCompletionConditionValidator extends AeBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeForEachCompletionConditionValidator(AeForEachCompletionConditionDef aDef)
   {
      super(aDef);
   }
}
 