//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/recovered/coord/AeRecoveredCompensateOrCancelItem.java,v 1.1 2008/03/28 01:46:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.recovered.coord; 

import org.activebpel.rt.bpel.coord.AeCoordinationException;
import org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal;

/**
 * Recovered item that sends the compensateOrCancel signal to the participant. 
 */
public class AeRecoveredCompensateOrCancelItem extends AeAbstractRecoveredCoordinationItem
{
   /**
    * Ctor
    * @param aCoordId
    */
   public AeRecoveredCompensateOrCancelItem(String aCoordId)
   {
      super(aCoordId);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.recovered.coord.AeAbstractRecoveredCoordinationItem#queueCoordinationSignal(org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal)
    */
   protected void queueCoordinationSignal(IAeCoordinationManagerInternal aCoordManager) throws AeCoordinationException
   {
      aCoordManager.compensateOrCancel(getCoordId());
   }
}
 