//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/invoke/AeSubprocessInvokeHandler.java,v 1.11 2008/02/17 21:38:47 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.invoke;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinationContext;
import org.activebpel.rt.bpel.coord.IAeCoordinationManager;
import org.activebpel.rt.bpel.coord.IAeCreateContextRequest;
import org.activebpel.rt.bpel.coord.IAeCreateContextResponse;
import org.activebpel.rt.bpel.impl.IAeInvokeInternal;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.coord.AeCreateContextRequest;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.invoke.AeInvokePrepareException;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.activebpel.wsio.receive.IAeMessageContext;

/**
 * Handler created by the AeProcessHandlerFactory. This handler is responsible for
 * invokes based on the 'subprocess' custom invoke protocol. The subprocess
 * invoke handler protocol implements a durable invoke, hence the invoke is done in
 * two phases. <br/>
 * <br/>
 * In phase 1 (prepare method), the subprocess is created by the engine and the inbound receive 
 * data is journaled, how ever the process is not yet executed. In the same transaction where
 * the subprocess is created, the engine will callback with an acknowlegement to indicate that
 * the process has been created and is ready for execution. The handler for this callback
 * will record (journal) this invoke as transmitted (delivered) so that it will not be re-executed
 * during fail-overs.
 * <br/>
 * <br/>
 * In phase 2, the process created in phase 1 is executed, typically on a work manager thread.
 */ 
public class AeSubprocessInvokeHandler extends AeProcessInvokeHandler 
{
   /**
    * Default constructor.
    */
   public AeSubprocessInvokeHandler()
   {
   }
   
   /** 
    * Overrides method to durably create a process, but not queue it for execution. The execution
    * of the subprocess is done in the <code>handleInvoke</code> method.
    * @see org.activebpel.rt.bpel.server.engine.invoke.AeProcessInvokeHandler#prepare(org.activebpel.wsio.invoke.IAeInvoke, java.lang.String)
    */
   public boolean prepare(IAeInvoke aInvoke, String aQueryData ) throws AeInvokePrepareException
   {                  
      // check to see if the invoke has been reliably delivered.
      try
      {
         initialize(aInvoke);   
         // subprocess invoke is only supported for persistent type deployments.
         if (!isPersistent())
         {
            throw new AeBusinessProcessException(AeMessages.getString("AeSubprocessInvokeHandler.ERROR_SUBPROCESS_NOT_SUPPORTED_FOR_NONPERSISTENCE")); //$NON-NLS-1$
         }
         
         //create message context
         IAeMessageContext context = createMessageContext( aInvoke, getServiceName() );
         
         // queueReceive data into the engine to create process, but do not execute it. The execution will be done in the handleInvoke method.
         // Note: the Engine's subprocess create code will callback this class via IAeMessageAcknowledgeCallback#onAcknowledge.         
         long processId = queueReceiveData( (IAeInvokeInternal) aInvoke, context, this, false );
         return processId != IAeBusinessProcess.NULL_PROCESS_ID;
      }
      catch(Throwable t)
      {
         throw new AeInvokePrepareException(t);
      }      
   }  
   
   /**
    * Queues the process created in <code>prepare</code> method for execution. Typically, this method
    * is invoked asynchronously via a worker thread. 
    * @param aInvoke process to invoke
    * @param aQueryData custom invoke handler data.
    * @see org.activebpel.wsio.invoke.IAeInvokeHandler#handleInvoke(org.activebpel.wsio.invoke.IAeInvoke, java.lang.String)
    */
   public IAeWebServiceResponse handleInvoke(IAeInvoke aInvoke, String aQueryData)
   {     
      try
      {
         AeEngineFactory.getEngine().executeProcess( getChildProcessId() );
      }
      catch (Throwable t)
      {
         mapThrowableAsFault(t);
         AeException.logError(t,t.getMessage());
      }
      return getResponse();
   }
   
   
   /**
    * Creates the message context required to queue. This method also creates and adds the coordination context
    * to the return AeMessageContext object.
    * @param aInvoke
    * @param aServiceName name of target process's service name.
    * @throws AeException
    */
   protected IAeMessageContext createMessageContext(IAeInvoke aInvoke, String aServiceName) throws AeException
   {
      // call base class to create basic wsio message context.
      IAeMessageContext wsioMsgContext = createMessageContextInternal(aInvoke, aServiceName);
      
      // construct create coordination context request.
      IAeCreateContextRequest req = new AeCreateContextRequest();
      req.setCoordinationType(IAeCoordinating.AE_SUBPROCESS_COORD_TYPE);
      req.setProperty(IAeCoordinating.AE_COORD_PID, String.valueOf(aInvoke.getProcessId()) );
      req.setProperty(IAeCoordinating.AE_COORD_LOCATION_PATH, aInvoke.getLocationPath());
      
      // get the coordination manager.
      IAeCoordinationManager manager = AeEngineFactory.getCoordinationManager();
      // create a context (this operation may throw coordination related exceptions).
      IAeCreateContextResponse resp = manager.createCoordinationContext(req);
      IAeCoordinationContext coordContext = resp.getContext();
      
      // Ideally, we want to do; IAeMessageContext.setCoordinationContext(IAeCoordinationContext)      
      // For now, set the coordination information (i.e. coord context obj) in the business properties.
      // How does this impact java.io.Serializable (cluster related) - should we simple pass name/value string with the coord id.
      
      // Business properties expect only strings.
      wsioMsgContext.getBusinessProcessProperties().put(IAeCoordinating.WSCOORD_ID,
            coordContext.getIdentifier());
      return wsioMsgContext;
   }

   /**
    * Creates the message context.  Useful for subclasses to override just that funtionality,
    * while keeping the coordination logic intact in the <code>createMessageContext</code> method.
    * 
    * @param aInvoke
    * @param aServiceName
    * @throws AeException
    */
   protected IAeMessageContext createMessageContextInternal(IAeInvoke aInvoke, String aServiceName) throws AeException
   {
      return super.createMessageContext(aInvoke, aServiceName);
   }   
}
