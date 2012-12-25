//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/IAeUncrossableLinkBoundary.java,v 1.1 2007/03/03 02:45:33 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def; 

/**
 * Interface used to mark defs that links cannot cross.
 */
public interface IAeUncrossableLinkBoundary
{
   // fixme (MF) refactor the link validation code to work with our validators and move this interface onto the validators instead of the defs
   /**
    * indicates if link can cross out of the def
    */
   public boolean canCrossOutbound();
   
   /**
    * indicates if the link can cross into the def
    */
   public boolean canCrossInbound();
}
 