//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/IAeImplicitVariablesAdapter.java,v 1.1 2008/01/15 18:18:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.xml.def.IAeAdapter;

/**
 * adapter for creating implicit variables
 */
public interface IAeImplicitVariablesAdapter extends IAeAdapter
{
   /**
    * Enables the extension activity to create whatever implicit variables it wants
    * 
    * @param aDef
    */
   public void createImplicitVariables(AeChildExtensionActivityDef aDef);
}
 