//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/IAeLoopControl.java,v 1.1 2005/08/18 21:35:46 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import org.activebpel.rt.bpel.impl.IAeBpelObject;

/**
 * Tagging interface for the loop controls continue and break that is used as
 * a param to the onContinue and onBreak methods to indicate the source
 * of the loop control that caused the change. This is used primarily for the 
 * parallel forEach since it needs to know which child instance is continuing
 * or breaking.  
 */
public interface IAeLoopControl extends IAeBpelObject
{

}
 