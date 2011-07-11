//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/coord/IAeParticipant.java,v 1.2 2006/06/26 16:50:49 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.coord;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;

/**
 * Interface to indicate the participant in a coordinated activity.
 */
public interface IAeParticipant extends IAeCoordinating
{

   /**
    * Called by a process under coordination when its compensation has completed.
    */
   public void compensationComplete() throws AeBusinessProcessException;

   /**
    * Called when the compensation was interrupted by a fault.
    * @param aFault
    */
   public void compensationCompleteWithFault(IAeFault aFault) throws AeBusinessProcessException;   
}
