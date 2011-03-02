//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/IAeQueryDef.java,v 1.3.16.1 2008/04/21 16:09:42 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def; 

/**
 * Interface for defs that have queries (like &lt;to&gt;) 
 */
public interface IAeQueryDef
{
   /**
    * Gets the query
    */
   public String getQuery();
   
   /**
    * Gets the language for the query
    */
   public String getQueryLanguage();
   
   /**
    * Gets the bpel namespace.
    */
   public String getBpelNamespace();
}
 