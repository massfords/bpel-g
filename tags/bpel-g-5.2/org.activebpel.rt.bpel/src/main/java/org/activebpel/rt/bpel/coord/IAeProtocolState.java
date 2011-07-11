//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/coord/IAeProtocolState.java,v 1.1 2005/10/28 21:07:18 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.coord;

/**
 * Interface to indicate WS-Coordination protocol state.  
 * Note: This is an internal implementation tailored to be used with requirement 111.
 */
public interface IAeProtocolState
{
   /**
    * @return Returns the protocol specific state.
    */
   public String getState();
}
