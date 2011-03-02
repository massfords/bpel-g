//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/support/IAeSpecExtension.java,v 1.1 2008/03/11 21:42:09 vvelusamy Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity.support;

import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.IAeAdapter;

/**
 * Base interface for From and To spec adapters.
 */
public interface IAeSpecExtension extends IAeAdapter
{

   /**
    * Gets the spec extension attribute name.
    */
   public String getPropertyName();

   /**
    * Gets the spec extension attribute value.
    */
   public String getPropertyValue(AeBaseXmlDef aDef);

}