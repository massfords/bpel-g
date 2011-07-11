//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/IAeExtensionRegistry.java,v 1.1 2007/10/12 16:06:42 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.io;

import javax.xml.namespace.QName;

import org.activebpel.rt.xml.def.IAeExtensionObject;

/**
 * Interface for retrieving IAeExtensionObject object for extension elements,  
 * attributes and activities
 */
public interface IAeExtensionRegistry
{
   /**
    * Given an extension's QName, return the extension object for it.
    * @param aQName
    */
   public IAeExtensionObject getExtensionObject(QName aQName);
}
