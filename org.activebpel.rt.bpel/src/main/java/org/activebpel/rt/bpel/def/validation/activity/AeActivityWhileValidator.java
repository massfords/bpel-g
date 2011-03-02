//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityWhileValidator.java,v 1.2 2006/10/03 19:25:40 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.activity.AeActivityWhileDef;

/**
 * model provides validation for the while activity
 */
public class AeActivityWhileValidator extends AeBaseLoopingActivityValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeActivityWhileValidator(AeActivityWhileDef aDef)
   {
      super(aDef);
   }
}
 