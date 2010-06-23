//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/AeCreateContextRequest.java,v 1.1 2005/10/28 21:10:30 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCreateContextRequest;
import org.activebpel.rt.util.AeUtil;

/**
 * Basic implementation of the create context request.
 */
public class AeCreateContextRequest extends AeContextBase implements IAeCreateContextRequest
{
   /**
    * Default constructor.
    */
   public AeCreateContextRequest()
   {
      super();
   }

   /**
    * @return coordination type.
    */
   public String getCoordinationType()
   {
      return getProperty(IAeCoordinating.WSCOORD_TYPE);
   }
   
   /**
    * Sets the type of coordination.
    * @param aCoordinationType the type of coordination.
    */
   public void setCoordinationType(String aCoordinationType)
   {
      setProperty(IAeCoordinating.WSCOORD_TYPE, aCoordinationType);
   }
   
   /** 
    * @return returns the location path
    */
   public String getLocationPath()
   {
      return getProperty(IAeCoordinating.AE_COORD_LOCATION_PATH);
   }
   
   /** 
    * @return the process id or -1 if not available.
    */
   public long getProcessId()
   {
      long pid = -1;
      if (AeUtil.notNullOrEmpty( getProperty(IAeCoordinating.AE_COORD_PID) ))
      {
         try
         {
            pid = Long.parseLong( getProperty(IAeCoordinating.AE_COORD_PID) );
         }
         catch (Exception e)
         {
            //ignore
            pid = -1;
            AeException.logError(e,e.getMessage());
         }
      }       
      return pid;
   }
   
}
