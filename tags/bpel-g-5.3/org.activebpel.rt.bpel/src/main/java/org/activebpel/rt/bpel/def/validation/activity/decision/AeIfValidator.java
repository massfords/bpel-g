//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/decision/AeIfValidator.java,v 1.2.16.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.decision; 

import org.activebpel.rt.bpel.def.activity.support.AeIfDef;

/**
 * model provides validation for the if def of the &lt;if&gt; activity 
 */
public class AeIfValidator extends AeElseIfValidator
{
   /**
    * Ctor accepts the def
    * @param aDef
    */
   public AeIfValidator(AeIfDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Getter for the if def
    */
   public AeIfDef getIfDef()
   {
      return (AeIfDef) getDefinition();
   }
}
 