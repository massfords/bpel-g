//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/invoke/AeInvoker.java,v 1.7 2008/03/11 03:09:29 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.invoke;

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.AeDataConverter;
import org.activebpel.rt.bpel.impl.AeFaultFactory;
import org.activebpel.rt.bpel.impl.IAeInvokeInternal;
import org.activebpel.rt.bpel.impl.activity.support.AeFault;
import org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.reply.AeQueuingReplyReceiver;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.invoke.AeInvokePrepareException;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.activebpel.wsio.invoke.IAeInvokeHandler;
import org.activebpel.wsio.invoke.IAeTwoPhaseInvokeHandler;

/**
 * AeInvoker is responsible for invoking by delegating to a invoke handler
 * and handling the response message or fault from the handler by queuing it back into
 * the engine.
 */
public class AeInvoker
{
   /** The invoke data. */
   private IAeInvokeInternal mInvoke;
   /** The invoke handler. */
   private IAeInvokeHandler mInvokeHandler;
   /** Any query data. */
   private String mQueryData;
   /** Reply receiver used to queue result back into the engine. */
   private AeQueuingReplyReceiver mQueuingReplyReceiver;

   /**
    * Constructs an invoker.
    * @param aInvoke
    * @param aHandler
    * @param aQueryData
    */
   public AeInvoker( IAeInvokeInternal aInvoke, IAeInvokeHandler aHandler, String aQueryData )
   {
      mInvoke = aInvoke;
      mInvokeHandler = aHandler;
      mQueryData = aQueryData;
      // create  reply receiver here so that we have an accurate snapshot of the
      // reply information (specifically the invoke execution/transmission id).
      mQueuingReplyReceiver = new AeQueuingReplyReceiver(aInvoke);
   }

   /**
    * @return True if this invoke has already been transmitted.
    * @throws AeBusinessProcessException
    */
   public boolean isTransmitted() throws AeBusinessProcessException
   {
      try
      {
         IAeInvokeInternal invoke = getInvokeQueueObject();
         IAeTransmissionTracker tracker = AeEngineFactory.getEngine().getTransmissionTracker();
         return tracker.isTransmitted(invoke.getTransmissionId());
      }
      catch(Exception e)
      {
         throw new AeBusinessProcessException(e.getMessage(), e);
      }
   }

   /**
    * Accessor for invoke object.
    */
   public IAeInvokeInternal getInvokeQueueObject()
   {
      return mInvoke;
   }

   /**
    * Accessor for the invoke handler.
    */
   protected IAeInvokeHandler getInvokeHandler()
   {
      return mInvokeHandler;
   }
   
   /**
    * Setter for the invoker's invoke handler.
    * 
    * @param aInvokeHandler
    */
   public void setInvokeHandler(IAeInvokeHandler aInvokeHandler)
   {
      mInvokeHandler = aInvokeHandler;
   }

   /**
    * Accessor for the query data.
    */
   protected String getQueryData()
   {
      return mQueryData;
   }

   /**
    * @return Returns the queuingReplyReceiver.
    */
   protected AeQueuingReplyReceiver getQueuingReplyReceiver()
   {
      return mQueuingReplyReceiver;
   }

   /**
    * Delegates to the <code>IAeTwoPhaseInvokeHandler</code> prepare method.
    * @return true if successful.
    */
   public boolean prepareInvoke()
   {
      //
      // Note: This method is normally called via the IAeMessageQueue::addInvoke().
      // The current thread should have a lock on the Invoke object's Process.
      //

      // Prepare phase: Most invoke handlers would do nothing and simply return true.
      // Some handlers i.e. durable invoke handlers such as the process/subprocess invoke handlers
      // will use this method to create the subprocess and record a transmission (ack)
      // id in a single transaction. The execution of the newly created subprocess will be done
      // in IAeInvokeHandler::handleInvoke(...) via Work manager.

      // Note: Subprocess invokes should be synchronous (including one way invokes).
      // A subprocess invoke creates a new process (the subprocess). After the (sub) process is created,
      // it calls back the invoke's enclosing scope (via CoordinationManager.register(...)) so that
      // the scope is aware of the (sub) process joining the coordination (i.e. registered).
      // This (notification of registration to the enclosing scope) must happen before the Invoke
      // activity is allowed to continue. (wrt to one way invokes).
      // This is the reason for using the two phase invoke handler for process/subprocess invokes
      // i.e the subprocess creations and coordination registration can happen in the same thread
      // as the Invoke activity (hence the use of prepare()) and the execution can happen in
      // a worker thread.

      boolean rVal = true;
      if (getInvokeHandler() instanceof IAeTwoPhaseInvokeHandler)
      {
         IAeTwoPhaseInvokeHandler handler2 = (IAeTwoPhaseInvokeHandler) getInvokeHandler();
         try
         {
            // prepare method will return false if the (in the case of process/subprocess)
            // invoke has already been reliably transmitted. Therefore, this invokes  does
            // not need to be re-transmitted.
            //
            // If the invoke has not been already transmitted (e.g. xmt id  < 0), then it will
            // assign a transmission id (in the case of process/subprocess invoke handlers.)

            rVal = handler2.prepare( getInvokeQueueObject(), getQueryData() ) ;

            // Since the prepare method may assign a durable/persistent transmission id,
            // this value needs to be also set in the reply receiver.
            getQueuingReplyReceiver().setTransmissionId( getInvokeQueueObject().getTransmissionId() );

         }
         catch(AeInvokePrepareException e)
         {
            rVal = false;
            //log exception and queue exception as a fault to the invoke activity.
            AeException.logError(e,e.getMessage());
            handleExceptionDuringInvoke( e );
         }

      }
      return rVal;
   }

   /**
    * Delegates a call to the invoke handler to really handle the invoke.
    * The result from the handler is queued back into the engine.
    */
   public void handleInvoke()
   {
      IAeInvoke invoke = getInvokeQueueObject();
      try
      {
         IAeWebServiceResponse response = getInvokeHandler().handleInvoke( invoke, getQueryData() );
         IAeMessageData messageData = null;

         // If the response is not null and is flagged as earlyReply, then the invokeHandler
         // would have created a AeQueuingReplyReceiver to handle onMessage/onFault.
         // (e.g. see source AeProcessInvokeHandler class).
         //
         // Otherwise, we need to handle reply here for two cases.
         // case (a): response is available and it is not flagged as early reply. In this case,
         // get the message/fault data queue it to the engine via the locally constructed
         // AeQueuingReplyReceiver.
         if( response != null && !response.isEarlyReply() )
         {
            if( response.getMessageData() != null )
            {
               messageData = AeDataConverter.convert(response.getMessageData());
            }

            if( response.isFaultResponse() )
            {
               AeFault fault = new AeFault( response.getErrorCode(), messageData );
               fault.setInfo(response.getErrorString());
               fault.setDetailedInfo(response.getErrorDetail());
               handleResponseFault( fault, response.getBusinessProcessProperties());
            }
            else
            {
               handleResponseMessage( messageData, response.getBusinessProcessProperties() );
            }
         }
         else if (response == null)
         {
            // case (b):
            // If the response is null (i.e. there is no flag to indicate this is a early reply type),
            // call onMessage on the locally constructed queuingReplyRec.
            handleResponseMessage( null, null );
         }
      }
      catch (Exception e)
      {
         AeException.logError(e,e.getMessage());
         handleExceptionDuringInvoke( e );
      }
   }

   /**
    * Handle invoke response data by queuing it back into engine for delivary.
    * @param aMessage
    * @param aProcessProperties
    */
   public void handleResponseMessage(IAeMessageData aMessage, Map aProcessProperties)
   {
      try
      {
         getQueuingReplyReceiver().onMessage(aMessage, aProcessProperties );
      }
      catch (AeBusinessProcessException e)
      {
         AeException.logError(e, AeMessages.getString("AeInvoker.REPORT_ERROR_INVOKING")); //$NON-NLS-1$
      }
   }

   /**
    * Handle invoke fault by queuing it back into the engine.
    * @param aFault
    * @param aProcessProperties
    */
   public void handleResponseFault(IAeFault aFault, Map aProcessProperties )
   {
      try
      {
         getQueuingReplyReceiver().onFault( aFault, aProcessProperties);
      }
      catch (AeBusinessProcessException e)
      {
         AeException.logError(e, AeMessages.getString("AeInvoker.REPORT_ERROR_INVOKING")); //$NON-NLS-1$
      }
   }

   /**
    * Report any errors during invoke by queuing a System Error fault.
    * @param aEx
    */
   public void handleExceptionDuringInvoke(Throwable aEx )
   {
      // any assigned value needs to be also set in the reply receiver.
      getQueuingReplyReceiver().setTransmissionId( getInvokeQueueObject().getTransmissionId() );

      IAeFault fault = AeFaultFactory.getSystemErrorFault(aEx);
      handleResponseFault(fault, null);
   }

}
