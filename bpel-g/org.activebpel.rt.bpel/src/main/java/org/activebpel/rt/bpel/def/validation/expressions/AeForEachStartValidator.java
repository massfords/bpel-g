//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/expressions/AeForEachStartValidator.java,v 1.1 2006/08/16 22:07:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.expressions; 

import org.activebpel.rt.bpel.def.activity.support.AeForEachStartDef;

/**
 * model provides validation for the forEach start def
 */
public class AeForEachStartValidator extends AeUnsignedIntExpressionValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeForEachStartValidator(AeForEachStartDef aDef)
   {
      super(aDef);
   }
}
 