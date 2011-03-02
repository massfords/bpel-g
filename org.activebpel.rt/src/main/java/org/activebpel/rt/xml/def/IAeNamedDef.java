//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/IAeNamedDef.java,v 1.2 2008/01/11 18:41:56 rnaylor Exp $
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
 * Interface for a def with a name.
 */
public interface IAeNamedDef
{
   /**
    * Getter for the name
    */
   public String getName();
   
   /**
    * Setter for the name
    */
   public void setName(String aName);
}
 