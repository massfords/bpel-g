//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/IAeActivityCreateInstanceDef.java,v 1.2 2006/01/03 19:55:05 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

/**
 * Interface to define an activity which has a createInstance attribute.
 */
public interface IAeActivityCreateInstanceDef
{

   /**
    * Returns true if the activity's createInstance attribute flag is yes.
    */
   public boolean isCreateInstance();
}
