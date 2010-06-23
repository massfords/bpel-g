//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/IAeExtensionDefUnderstoodAdapter.java,v 1.1 2007/11/15 02:20:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def; 


/**
 * Allows the extension object to report whether it understands the elements
 * and attributes that have been set on it. 
 */
public interface IAeExtensionDefUnderstoodAdapter extends IAeAdapter
{
   /**
    * Returns true if the element is understood
    * @param aExtensionElementDef
    */
   public boolean isUnderstood(AeExtensionElementDef aExtensionElementDef);

   /**
    * Returns true if the attribute is understood
    * @param aExtensionAttributeDef
    */
   public boolean isUnderstood(AeExtensionAttributeDef aExtensionAttributeDef);

}
 