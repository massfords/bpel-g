//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/support/IAeToSpecExtension.java,v 1.4 2008/03/11 21:42:09 vvelusamy Exp $
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
import org.activebpel.rt.bpel.impl.activity.assign.IAeExtensionCopyStrategy;

/**
 * Provides an interface for an extension attribute that will be responsible
 * for creating an L-value for the copy operation as well as performing the
 * actual copy strategy. 
 */
public interface IAeToSpecExtension extends IAeSpecExtension
{
   /**
    * Creates a strategy that will be used to copy data from a from-spec into
    * the L-value targeted by this to-spec.
    * 
    * @param aCopyOperation
    * @param aToDef
    */
   public IAeExtensionCopyStrategy createCopyStrategy(IAeCopyOperation aCopyOperation, AeToDef aToDef) throws AeBusinessProcessException;
}
 