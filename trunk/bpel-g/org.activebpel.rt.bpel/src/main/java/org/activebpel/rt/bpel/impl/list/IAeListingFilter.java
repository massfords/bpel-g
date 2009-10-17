//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/IAeListingFilter.java,v 1.1 2004/11/17 16:08:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.list; 

/**
 * Describes the methods necessary for a filter to be used with the <code>AeListingResultSetHandler</code>
 */
public interface IAeListingFilter
{

   /**
    * The position within the result set to start reading rows.
    */
   public int getListStart();

   /**
    * The max number of rows to return for display in the ui.
    */
   public int getMaxReturn();

}
 