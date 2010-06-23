//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/expressions/AeForValidator.java,v 1.2.16.1 2008/04/21 16:09:44 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.expressions; 

import org.activebpel.rt.bpel.def.activity.support.AeForDef;

/**
 * model provides validation for the &lt;for&gt; def
 */
public class AeForValidator extends AeDurationExpressionValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeForValidator(AeForDef aDef)
   {
      super(aDef);
   }
}
 