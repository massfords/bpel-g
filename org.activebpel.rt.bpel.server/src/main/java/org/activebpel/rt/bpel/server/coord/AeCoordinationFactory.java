//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/AeCoordinationFactory.java,v 1.3 2007/09/07 20:52:13 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord;

import org.activebpel.rt.bpel.coord.AeCoordinationException;
import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinationContext;
import org.activebpel.rt.bpel.coord.IAeProtocolState;
import org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal;
import org.activebpel.rt.bpel.server.coord.subprocess.AeSpCoordinator;
import org.activebpel.rt.bpel.server.coord.subprocess.AeSpParticipant;
import org.activebpel.rt.util.AeUtil;


/**
 * Factory to create objects needed for the coordination framework.
 */
public class AeCoordinationFactory
{
   /** Singleton instance. */
   private static AeCoordinationFactory sInstance = null;
   
   /** Static accessor. */
   public static synchronized AeCoordinationFactory getInstance()
   {
      if (sInstance == null)
      {
         sInstance = new AeCoordinationFactory();
      }
      return sInstance;
   }

   /**
    * Creates and returns a Coordination object.
    * @param aManager coordination manager.
    * @param aContext coordination context.
    * @param aRole role.
    * @return coordinating object.
    * @throws AeCoordinationException
    */
   public IAeCoordinating createCoordination(IAeCoordinationManagerInternal aManager, 
         IAeCoordinationContext aContext, int aRole) throws AeCoordinationException
   {
      IAeProtocolState protocolState = null;
      return createCoordination(aManager, aContext, protocolState, aRole);      
   }   
   
   /**
    * Creates and returns a Coordination object.
    * @param aManager coordination manager.
    * @param aContext coordination context.
    * @param aState state.
    * @param aRole role.
    * @return coordinating object.
    * @throws AeCoordinationException
    */
   public IAeCoordinating createCoordination(IAeCoordinationManagerInternal aManager, 
         IAeCoordinationContext aContext, String aState, int aRole) throws AeCoordinationException
   {
      IAeProtocolState protocolState = null;
      if (AeUtil.notNullOrEmpty(aState))
      {
         protocolState = new AeProtocolState(aState);
      }
      return createCoordination(aManager, aContext, protocolState, aRole);      
   }
   
   /**
    * Creates and returns a Coordination object.
    * @param aManager coordination manager.
    * @param aContext coordination context.
    * @param aState state.
    * @param aRole role.
    * @return coordinating object.
    * @throws AeCoordinationException
    */
   public IAeCoordinating createCoordination(IAeCoordinationManagerInternal aManager, 
         IAeCoordinationContext aContext, IAeProtocolState aState, int aRole) throws AeCoordinationException
   {
      AeCoordinatingBase coordinating = null;
      if (aRole == IAeCoordinating.COORDINATOR_ROLE)
      {
         coordinating = new AeSpCoordinator(aContext, aManager);            
      }
      else
      {
         coordinating = new AeSpParticipant(aContext, aManager);            
      }
      if (aState != null)
      {
         coordinating.setState(aState);
      }
      return coordinating;
   }
   
}
