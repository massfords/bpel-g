//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/coord/AeCoordinationDetail.java,v 1.1 2006/01/25 20:43:45 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.coord;

/**
 * Contains instance information about a coordination.
 */
public class AeCoordinationDetail
{
   /** Process Id */
   private long mProcessId;
   /** Coordination Id */
   private String mCoordinationId;
   /** Current State */
   private String mState;
   /** Location path of activity. */
   private String mLocationPath;

   /**
    * Default ctor.
    * @param aProcessId
    * @param aCoordinationId
    * @param aState
    * @param aLocationPath
    */
   public AeCoordinationDetail(long aProcessId, String aCoordinationId, String aState, String aLocationPath)
   {
      setProcessId(aProcessId);
      setCoordinationId(aCoordinationId);
      setState(aState);
      setLocationPath(aLocationPath);
   }
   
   /**
    * Constructs the detail given a coordination.
    * @param aCoordinating
    */
   public AeCoordinationDetail(IAeCoordinating aCoordinating)
   {
      setProcessId(aCoordinating.getProcessId());
      setCoordinationId(aCoordinating.getCoordinationId());
      setState(aCoordinating.getState().getState());
      setLocationPath(aCoordinating.getLocationPath());
   }

   /**
    * @return Returns the coordinationId.
    */
   public String getCoordinationId()
   {
      return mCoordinationId;
   }

   /**
    * @param aCoordinationId The coordinationId to set.
    */
   public void setCoordinationId(String aCoordinationId)
   {
      mCoordinationId = aCoordinationId;
   }

   /**
    * @return Returns the locationPath.
    */
   public String getLocationPath()
   {
      return mLocationPath;
   }

   /**
    * @param aLocationPath The locationPath to set.
    */
   public void setLocationPath(String aLocationPath)
   {
      mLocationPath = aLocationPath;
   }

   /**
    * @return Returns the processId.
    */
   public long getProcessId()
   {
      return mProcessId;
   }

   /**
    * @param aProcessId The processId to set.
    */
   public void setProcessId(long aProcessId)
   {
      mProcessId = aProcessId;
   }

   /**
    * @return Returns the state.
    */
   public String getState()
   {
      return mState;
   }

   /**
    * @param aState The state to set.
    */
   public void setState(String aState)
   {
      mState = aState;
   }   
}
