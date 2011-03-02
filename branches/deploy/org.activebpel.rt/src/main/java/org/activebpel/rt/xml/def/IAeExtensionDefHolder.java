//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/IAeExtensionDefHolder.java,v 1.1 2007/11/15 02:20:48 mford Exp $
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
 * Enables the xml IO layer to set the def which declared the extension
 * back on the extension object so it can read data from the def or create
 * its own internal representation of the data.
 * 
 * If the extension was an attribute, then the def will be an 
 * AeExtensionAttributeDef.
 * 
 * If the extension was an element, then it may be an AeExtensionElementDef.
 * In some cases (depending on the domain of the xml language) the def may
 * be a special type of extension. An example of this is the extension 
 * activity within ws-bpel 2.0. 
 */
public interface IAeExtensionDefHolder extends IAeAdapter
{
   /**
    * Setter for the def
    * 
    * @param aDef
    */
   public void setExtensionDef(AeBaseXmlDef aDef);
}
 