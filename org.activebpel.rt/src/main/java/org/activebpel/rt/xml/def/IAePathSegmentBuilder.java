//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/IAePathSegmentBuilder.java,v 1.1 2007/10/01 16:59:12 mford Exp $
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
 * Interface for building a single segment in a location path. The def objects
 * use an XPath like location path to uniquely identify their location within
 * the object model. This interface will construct a single segment within the
 * location path. 
 */
public interface IAePathSegmentBuilder
{
   /**
    * Returns a string to use for this def's segment in the path. 
    * @param aDef
    */
   public String createPathSegment(AeBaseXmlDef aDef);
}
 