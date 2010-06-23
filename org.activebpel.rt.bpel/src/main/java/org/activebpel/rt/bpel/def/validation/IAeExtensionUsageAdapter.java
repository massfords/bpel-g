//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/IAeExtensionUsageAdapter.java,v 1.1 2008/02/29 04:09:36 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation; 

import java.util.List;

import org.activebpel.rt.xml.def.IAeAdapter;

/**
 * adapter for extension attributes and declarations that can report which 
 * extension declarations they require and whether they must be understood.
 */
public interface IAeExtensionUsageAdapter extends IAeAdapter
{
   /**
    * Returns the list of extensions required for this extension attribute or
    * element.
    */
   public List getRequiredExtensions();
}
 