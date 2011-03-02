//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/recovered/coord/AeAbstractRecoveredCoordinationItem.java,v 1.1 2008/03/28 01:46:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.recovered.coord; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.coord.AeCoordinationException;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal;
import org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem;

/**
 * Base class used to model coordination behavior that needs to be requeued 
 * through the coordination manager after the process is recovered.
 *
 */
public abstract class AeAbstractRecoveredCoordinationItem implements IAeRecoveredItem
{
   /** id for the coordination  */
   private String mCoordId;
   
   /**
    * Ctor
    * @param aCoordId
    */
   public AeAbstractRecoveredCoordinationItem(String aCoordId)
   {
      setCoordId(aCoordId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem#queueItem(org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal)
    */
   public void queueItem(IAeBusinessProcessEngineInternal aTargetEngine) throws AeBusinessProcessException
   {
      IAeCoordinationManagerInternal coordManager = aTargetEngine.getCoordinationManager();
      try
      {
         queueCoordinationSignal(coordManager);
      }
      catch (AeCoordinationException e)
      {
         // fixme (MF-coord) rethrow any exceptions once the missing journal items for coord messages having been transmitted are added. 
      }
   }

   /**
    * Queues the coordination signal through the manager during recovery.
    * @param aCoordManager
    * @throws AeCoordinationException
    */
   protected abstract void queueCoordinationSignal(IAeCoordinationManagerInternal aCoordManager) throws AeCoordinationException;

   /**
    * Location id's are used so this method always returns 0
    * @see org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem#getLocationId()
    */
   public int getLocationId()
   {
      return 0;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem#isRemoval()
    */
   public boolean isRemoval()
   {
      return false;
   }

   /**
    * @return the coordId
    */
   protected String getCoordId()
   {
      return mCoordId;
   }

   /**
    * @param aCoordId the coordId to set
    */
   protected void setCoordId(String aCoordId)
   {
      mCoordId = aCoordId;
   }
}
 