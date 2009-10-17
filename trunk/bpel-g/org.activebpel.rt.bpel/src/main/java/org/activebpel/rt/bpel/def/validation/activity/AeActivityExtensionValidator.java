//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityExtensionValidator.java,v 1.4 2007/10/12 16:09:48 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;

/**
 * model for validating an extension activity
 */
public class AeActivityExtensionValidator extends AeActivityValidator
{

   /**
    * ctor
    * @param aDef
    */
   public AeActivityExtensionValidator(AeChildExtensionActivityDef aDef)
   {
      super(aDef);
   }

}
 