//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/support/IAeFromSpecExtension.java,v 1.2 2008/03/11 21:42:09 vvelusamy Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity.support; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation;

/**
 * Defines the interface that an extension attribute will need to implement in
 * order to produce the R-value for a copy operation. The R-value must be an 
 * an EII, TII, or AII.
 */
public interface IAeFromSpecExtension extends IAeSpecExtension
{
   /**
    * Executes whatever behavior is defined by the extension in order to create
    * an object for the copy operation.
    * @param aCopyOperation
    * @param aDef
    * @throws AeBusinessProcessException
    */
   public Object executeFromSpec(IAeCopyOperation aCopyOperation, AeFromDef aDef) throws AeBusinessProcessException;
}
 