//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeParticipantCompensator.java,v 1.5 2008/03/28 01:41:15 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.coord.IAeCoordinationManager;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
  
/**
 * AeParticipantCompensator implements an 'implicit' compensate activity
 * for the process under coordination. 
 */
public class AeParticipantCompensator implements IAeCompensationCallback
{
   /**
    * Coordination id.
    */
   private String mCoordinationId;
   
   /** Location path of this compensation activity. **/
   private String mLocationPath;
   
   /** Business process engine */
   private IAeBusinessProcessEngineInternal mEngine;
   
   /**
    *  Default ctor.
    */
   public AeParticipantCompensator(String aCoordinationId, IAeBusinessProcessEngineInternal aEngine)
   {
      mCoordinationId = aCoordinationId;
      setEngine( aEngine );
   }
      
   /**
    * @return Returns the engine.
    */
   protected IAeBusinessProcessEngineInternal getEngine()
   {
      return mEngine;
   }
   /**
    * @param aEngine The engine to set.
    */
   protected void setEngine(IAeBusinessProcessEngineInternal aEngine)
   {
      mEngine = aEngine;
   }
   /**
    * Returns the coordination context id.
    */
   public String getCoordinationId()
   {
      return mCoordinationId;
   }

   /** 
    * Returns true if this is a coordinated comp info. This is normally used by
    * during saving and restoring state information.
    * @return true if this is a coordinated comp info.
    */
   public boolean isCoordinated()
   { 
      return true;
   }   
   
   /**
    * Getter for the coordination manager
    */
   protected IAeCoordinationManager getCoordinationManager()
   {
      IAeCoordinationManager manager = getEngine().getCoordinationManager();
      return manager;
   }
   
   /**
    * Called by the participant process's compensationHandler when its compensation is complete.
    * @param aCompHandler
    */
   public void compensationComplete(AeCompensationHandler aCompHandler) throws AeBusinessProcessException
   {
      // Any clean up do be done?
      // notify coordinator with COMPENSATED message.
      getCoordinationManager().compensationCompleted(getCoordinationId(), null);
   }

   /**
    * Called by the participant process's compensationHandler when its compensation was interrupted by a fault.
    * @param aCompHandler
    * @param aFault
    */
   public void compensationCompleteWithFault(AeCompensationHandler aCompHandler, IAeFault aFault) throws AeBusinessProcessException
   {
      // Any clean up do be done?     
      // notify coordinator with FAULT_COMPENSATED message.
      getCoordinationManager().compensationCompleted(getCoordinationId(), aFault);
   }
   
   /** 
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeCompensationCallback#compensationTerminated(org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler)
    */
   public void compensationTerminated(AeCompensationHandler aCompHandler) throws AeBusinessProcessException
   {
      // subprocess's instance compHandler was terminated.
      compensationCompleteWithFault(aCompHandler, aCompHandler.getFaultFactory().getCoordinatedCompensationTerminated() );
      
   }
   /**
    * Returns the location path for the callback object. This will be used to
    * identify the callback object in the event that the process is asked to
    * persist itself.
    */
   public String getLocationPath()
   {
      if (mLocationPath == null)
      {
         mLocationPath = AeSupportActivityLocationPathSuffix.COORDINATION_COMPENSATE_ACTIVITY;
      }
      return mLocationPath;
   }

   /**
    * Sets the location path for the callback object. Normally set during deserialization.
    */
   public void setLocationPath(String aLocationPath)
   {
      mLocationPath = aLocationPath;
   }   
  
}

