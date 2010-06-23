//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/AeInMemoryCoordinationManager.java,v 1.8 2008/03/28 01:46:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.bpel.coord.AeCoordinationDetail;
import org.activebpel.rt.bpel.coord.AeCoordinationException;
import org.activebpel.rt.bpel.coord.AeCoordinationFaultException;
import org.activebpel.rt.bpel.coord.AeCoordinationNotFoundException;
import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinator;
import org.activebpel.rt.bpel.coord.IAeCreateContextRequest;
import org.activebpel.rt.bpel.coord.IAeParticipant;
import org.activebpel.rt.bpel.coord.IAeProtocolMessage;
import org.activebpel.rt.bpel.coord.IAeProtocolState;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.server.coord.subprocess.IAeSpCoordinating;
import org.activebpel.rt.util.AeUtil;

/**
 * In mememory implementation of a coordination manager.
 */
public class AeInMemoryCoordinationManager extends AeCoordinationManager
{
   /**
    * Map containing coordinatables (coordinator or participant), keyed by 
    * combination of process id and location path.
    */
   private Map mCoordinatingsPidMap = null;   

   /**
    * @param aConfig
    */
   public AeInMemoryCoordinationManager(Map aConfig)
   {
      super(aConfig);
   }
   
   /**
    * Creates and registers the given context given the context. The coordination id is normally null 
    * for Coordinators since this method will generate a new id.  
    * @param aCtxRequest
    * @param aId coordination id. Required for participants.
    * @param aRole 
    * @return coordination context.
    * @throws AeCoordinationException
    */   
   protected AeCoordinationContext createContext(IAeCreateContextRequest aCtxRequest, IAeCoordinationId aId, 
         IAeProtocolState aInitState, int aRole)
      throws AeCoordinationException   
   {
      AeCoordinationContext ctx = null;
      String pidStr = aCtxRequest.getProperty(IAeCoordinating.AE_COORD_PID);
      String locPath = aCtxRequest.getProperty(IAeCoordinating.AE_COORD_LOCATION_PATH);
      
      // create id if not given.
      if (aId == null)
      {
         aId = new AeInMemoryCoordinationId(pidStr, locPath);
      }
      
      // does the coordination id already exist?
      if (aRole == IAeCoordinating.COORDINATOR_ROLE && hasCoordinator(aId.getIdentifier()))
      {
         // throw, for now?
         throw new AeCoordinationFaultException(AeCoordinationFaultException.ALREADY_REGISTERED); 
      }
      
      ctx = new AeCoordinationContext(aId);
      // set the coordination type. In this case, its AE subprocess.
      ctx.setProperty(IAeCoordinating.WSCOORD_TYPE, IAeCoordinating.AE_SUBPROCESS_COORD_TYPE);
      // set the supported protocol. In this case, it is a version of AESP_PARTICIPANT_COMPLETION_PROTOCOL.
      ctx.setProperty(IAeCoordinating.WSCOORD_PROTOCOL, IAeSpCoordinating.AESP_PARTICIPANT_COMPLETION_PROTOCOL);
      // set other data need for the context.
      // pid
      ctx.setProperty(IAeCoordinating.AE_COORD_PID, pidStr);
      // location path of invoke (or scope).
      ctx.setProperty(IAeCoordinating.AE_COORD_LOCATION_PATH, locPath);
      
      // set protocol level data
      if (AeUtil.notNullOrEmpty(aCtxRequest.getProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_PROCESS_ID) ) )
      {
         ctx.setProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_PROCESS_ID, aCtxRequest.getProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_PROCESS_ID) );
      }
      if (AeUtil.notNullOrEmpty(aCtxRequest.getProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_LOCATION_PATH) ) )
      {
         ctx.setProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_LOCATION_PATH, aCtxRequest.getProperty(IAeSpCoordinating.PROTOCOL_DESTINATION_LOCATION_PATH) );
      }    
      
      // call base class to create coordinator or participant object.
      IAeCoordinating coord = createCoordination(ctx, aInitState, aRole);
      // add to in-memory collection.
      addCoordinating(coord);      
      return ctx;
   }
      
   /**
    * Save the current state information.
    * @param aCoordinating
    */
   public void persistState(IAeCoordinating aCoordinating) throws AeCoordinationException
   {
      // no-op
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.coord.AeCoordinationManager#persistContext(org.activebpel.rt.bpel.coord.IAeCoordinating)
    */
   protected void persistContext(IAeCoordinating aCoordinating) throws AeCoordinationException   
   {
      // no-op
   }

   /**
    * Adds the given coordinating object (coordinator or a participant) to the in memory collection.
    * @param aCoordinating coordinator or participant.
    */
   protected synchronized void addCoordinating(IAeCoordinating aCoordinating)
   {
      getCoordinatingsPidMap().put(getKey(aCoordinating), aCoordinating);
   }
   
   /**
    * Returns an iterator to coordinating activities matching the coordination id.
    * @param aCoordinationId
    * @return iterator of IAeCoordinating objects.
    */
   protected synchronized Iterator getCoordinatingIterator(String aCoordinationId) throws AeCoordinationNotFoundException
   {
      Set set = new HashSet();
      Iterator it = getCoordinatingsPidMap().values().iterator();
      while (it.hasNext())
      {
         IAeCoordinating c = (IAeCoordinating) it.next();
         if (c.getCoordinationId().equals(aCoordinationId))
         {
            set.add(c);
         }
      }
      return getCoordinatingIterator(set, aCoordinationId);
   }  
   
   /**
    * @see org.activebpel.rt.bpel.server.coord.AeCoordinationManager#getCoordinatingIterator(long)
    */
   protected synchronized Iterator getCoordinatingIterator(long aProcessId) throws AeCoordinationNotFoundException
   {
      Set set = new HashSet();
      Iterator it = getCoordinatingsPidMap().values().iterator();
      while (it.hasNext())
      {
         IAeCoordinating c = (IAeCoordinating) it.next();
         if (c.getProcessId() == aProcessId)
         {
            set.add(c);
         }
      }
      return getCoordinatingIterator(set, String.valueOf(aProcessId) );
   }    
   
   /**
    * @see org.activebpel.rt.bpel.server.coord.AeCoordinationManager#getCoordinating(java.lang.String, long)
    */
   protected synchronized IAeCoordinating getCoordinating(String aCoordinationId, long aPid) throws AeCoordinationNotFoundException
   {
      IAeCoordinating rVal = null;
      rVal = (IAeCoordinating) getCoordinatingsPidMap().get( getKey(aCoordinationId, aPid) );
      if (rVal == null)
      {
         throw new AeCoordinationNotFoundException(getKey(aCoordinationId, aPid));
      }
      else
      {
         return rVal;
      }      
   }
   
   /**
    * Returns the lookup key based on the Coordinating object's process id and the coordination-id.
    * @param aCoordinating
    */   
   protected String getKey(IAeCoordinating aCoordinating)
   {
      return getKey( aCoordinating.getCoordinationId(), aCoordinating.getProcessId()); 
   }   
   
   /**
    * Returns the lookup key based on the process id and the coordination id.
    * @param aCoordinationId 
    * @param aPid
    */
   protected String getKey(String aCoordinationId, long aPid)
   {
      return Long.toString(aPid) + ":" + aCoordinationId; //$NON-NLS-1$
   }
   
   /**
    * @return Returns the processEventListeners.
    */
   protected Map getCoordinatingsPidMap()
   {
      if (mCoordinatingsPidMap == null)
      {
         mCoordinatingsPidMap = new HashMap();
      }
      return mCoordinatingsPidMap;
   }     

   /**
    * 
    * Overrides method to 
    * @see org.activebpel.rt.bpel.coord.IAeCoordinationManager#getCoordinatorDetail(long)
    */
   public AeCoordinationDetail getCoordinatorDetail(long aChildProcessId) throws AeCoordinationNotFoundException
   {
      String coordinationId = null;
      // find sub process (child) given process id and get the coordination id.
      Iterator it = getCoordinatingIterator(aChildProcessId);  
      while (it.hasNext())
      {
         IAeCoordinating c = (IAeCoordinating) it.next();
         if (c instanceof IAeParticipant)
         {
            coordinationId = c.getCoordinationId();
            break;
         }
      }
      if (coordinationId == null)
      {
         throw new AeCoordinationNotFoundException(String.valueOf(aChildProcessId));  
      }
      // find parent (coordinator) given coordination id
      it = getCoordinatorIterator(coordinationId);
      return new AeCoordinationDetail( (IAeCoordinating) it.next() );  
   }
   
   /**
    * 
    * Overrides method to 
    * @see org.activebpel.rt.bpel.coord.IAeCoordinationManager#getParticipantDetail(long)
    */
   public List getParticipantDetail(long aParentProcessId) throws AeCoordinationNotFoundException
   {
      List retList = new ArrayList();
      // find parent process coordinators given process id and get the coordination id.
      Iterator it = getCoordinatingIterator(aParentProcessId);  
      while (it.hasNext())
      {
         IAeCoordinating c = (IAeCoordinating) it.next();
         if (c instanceof IAeCoordinator)
         {
            // find children (participants) given coordination id
            Iterator childIter = getParticipantIterator(c.getCoordinationId());
            while (childIter.hasNext())
            {
               retList.add( new AeCoordinationDetail( (IAeCoordinating) childIter.next() ) );
            }  
         }
      }
      if (retList.size() == 0)
      {
         throw new AeCoordinationNotFoundException(String.valueOf(aParentProcessId));  
      }
      return retList;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal#journalCoordinationQueueMessageReceived(long, org.activebpel.rt.bpel.coord.IAeProtocolMessage)
    */
   public long journalCoordinationQueueMessageReceived(long aProcessId,
         IAeProtocolMessage aMessage)
   {
      return IAeProcessManager.NULL_JOURNAL_ID;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal#journalNotifyCoordinatorsParticipantClosed(long)
    */
   public long journalNotifyCoordinatorsParticipantClosed(long aProcessId)
   {
      return IAeProcessManager.NULL_JOURNAL_ID;
   }
}
