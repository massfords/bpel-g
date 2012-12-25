//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/IAeExtensionObject.java,v 1.4 2007/11/13 16:55:23 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def;

/**
 * Interface for extension objects to extension elements
 */
public interface IAeExtensionObject
{
   /**
    * Constructs and returns an Adapter when present for aClass
    * @param aClass
    * @return IAeAdapter 
    */
   public IAeAdapter getAdapter(Class aClass);
}
