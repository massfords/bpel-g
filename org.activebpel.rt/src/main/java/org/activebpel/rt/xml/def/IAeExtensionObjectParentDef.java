//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/IAeExtensionObjectParentDef.java,v 1.1 2007/11/27 02:47:28 mford Exp $
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
 * Interface for def objects that may contain extension objects.
 */
public interface IAeExtensionObjectParentDef
{
   /**
    * @return the extensionObject
    */
   public IAeExtensionObject getExtensionObject();

   /**
    * @param aExtensionObject the extensionObject to set
    */
   public void setExtensionObject(IAeExtensionObject aExtensionObject);

}
 