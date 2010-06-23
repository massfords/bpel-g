//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/IAeGetBaseXmlDefAdapter.java,v 1.1 2007/11/15 02:20:48 mford Exp $
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
 * Adapter interface for extension objects that convert their extension content
 * into a def object.
 */
public interface IAeGetBaseXmlDefAdapter extends IAeAdapter
{
   /**
    * Getter for extension object content as a def.
    */
   public AeBaseXmlDef getExtensionAsBaseXmlDef();
}
 