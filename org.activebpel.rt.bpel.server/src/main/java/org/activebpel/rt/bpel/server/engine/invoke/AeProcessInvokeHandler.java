// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/invoke/AeProcessInvokeHandler.java,v 1.25 2008/03/11 03:09:29 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.invoke;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.IAePolicyConstants;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeDataConverter;
import org.activebpel.rt.bpel.impl.AeEndpointReference;
import org.activebpel.rt.bpel.impl.AeFaultFactory;
import org.activebpel.rt.bpel.impl.IAeInvokeInternal;
import org.activebpel.rt.bpel.impl.addressing.AeAddressingHeaders;
import org.activebpel.rt.bpel.impl.addressing.IAeAddressingHeaders;
import org.activebpel.rt.bpel.impl.queue.AeInvoke;
import org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver;
import org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.deploy.AeProcessPersistenceType;
import org.activebpel.rt.bpel.server.deploy.AeRoutingInfo;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.receive.AeExtendedMessageContext;
import org.activebpel.rt.bpel.server.engine.reply.AeDurableQueuingReplyReceiver;
import org.activebpel.rt.bpel.server.engine.reply.AeQueuingReplyReceiver;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.wsio.AeMessageAcknowledgeException;
import org.activebpel.wsio.AeWsAddressingException;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;
import org.activebpel.wsio.IAeWebServiceAttachment;
import org.activebpel.wsio.IAeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.invoke.AeInvokePrepareException;
import org.activebpel.wsio.invoke.AeInvokeResponse;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.activebpel.wsio.invoke.IAeTwoPhaseInvokeHandler;
import org.activebpel.wsio.receive.IAeMessageContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handler created by the AeProcessHandlerFactory. This handler is responsible for invokes based on the
 * 'process' custom invoke protocol.
 */
public class AeProcessInvokeHandler implements IAeTwoPhaseInvokeHandler, IAeMessageAcknowledgeCallback
{
   /** Process id of the child process. */
   private long mChildProcessId;

   /** Invoke web service response. */
   private AeInvokeResponse mResponse = new AeInvokeResponse();

   /** Invoke object. */
   private IAeInvoke mInvoke;

   /** Indicates that the message was delivered to the engine */
   private boolean mDelivered;

   /** Indicates if the target process is persistent. */
   private boolean mPersistent;

   /** Endpoint reference */
   private IAeEndpointReference mEndpointReference;

   /** Service name. */
   private String mServiceName;

   /**
    * Default constructor.
    */
   public AeProcessInvokeHandler()
   {
   }

   /**
    * Initializes by setting the endpoint reference, persistent type and the service name.
    * @param aInvoke
    * @throws AeBusinessProcessException
    */
   protected void initialize(IAeInvoke aInvoke) throws AeException
   {
      setInvoke(aInvoke);
      // set the endpoint reference
      setEndpointReference(getPartnerEndpointReference(aInvoke));
      // for process and subprocess invokes, the address is the endpoint service name.
      setServiceName(extractServiceName(getEndpointReference()));
      setPersistentType(aInvoke, getServiceName());
      // assign transmission id if invoke (deployment plan) persistent.
      if ( isPersistent() )
      {
         getTransmissionTracker().assignTransmissionId(aInvoke, aInvoke.getProcessId(), aInvoke.getLocationId());
      }
   }

   /**
    * Overrides method to assign transmission id if not already assigned..
    * @see org.activebpel.wsio.invoke.IAeTwoPhaseInvokeHandler#prepare(org.activebpel.wsio.invoke.IAeInvoke,
    *      java.lang.String)
    */
   public boolean prepare(IAeInvoke aInvoke, String aQueryData) throws AeInvokePrepareException
   {
      // check to see if the invoke has been reliably delivered.
      try
      {
         initialize(aInvoke);
         return true;
      }
      catch (Throwable t)
      {
         throw new AeInvokePrepareException(t);
      }
   }

   /**
    * Invokes the given process. The process to invoke for this handler is specified as a QName in the
    * wsa::Address field of the endpoint reference. If the QName is not specified or if the deployment plan
    * for the given QName is not found, this handler will generate a system fault.
    * @param aInvoke process to invoke
    * @param aQueryData custom invoke handler data.
    * @see org.activebpel.wsio.invoke.IAeInvokeHandler#handleInvoke(org.activebpel.wsio.invoke.IAeInvoke,
    *      java.lang.String)
    */
   public IAeWebServiceResponse handleInvoke(IAeInvoke aInvoke, String aQueryData ) 
   {
      // invoke process only if has not been already transmitted.
      try
      {
         // create message context
         IAeMessageContext context = createMessageContext(aInvoke, getServiceName());

         // Create process and execute it.
         // Note: the Engine's subprocess create code will callback this class via
         // IAeMessageAcknowledgeCallback#onAcknowledge
         // once the message (invoke) has been delivered to the engine. The callback method - onAcknowlege
         // will record the transmission id to indicate that message was actually delivered.

         // reset delivered flag
         setDelivered(false);

         // queueReceive data into the engine and if a process was created, then queue it for execution.
         queueReceiveData((IAeInvokeInternal)aInvoke, context, this, true);
      }
      catch (Throwable t)
      {
         mapThrowableAsFault(t);
         setDelivered(true);
         AeException.logError(t, t.getMessage());
      }

      // wait until delivered (engine will call back once the message has been delivered or fault if message never consumed).
      if ( !isDelivered() )
      {
         synchronized (this)
         {
            if ( !isDelivered() )
            {
               try
               {
                  wait();
               }
               catch (InterruptedException t)
               {
                  mapThrowableAsFault(t);
               }
            }
         }
      }
      return getResponse();
   }

   /**
    * Overrides method to insert the transmission id to the persistent store.
    * @see org.activebpel.wsio.IAeMessageAcknowledgeCallback#onAcknowledge(java.lang.String)
    */
   public void onAcknowledge(String aMessageId) throws AeMessageAcknowledgeException
   {
      // journal the acknowledgment. The aMessageId is normally null for subprocess invoke, but
      // not necessarily null for other durable invokes such as WS-RM.
      try
      {
         recordInvokeAsDelivered(aMessageId);
      }
      catch (Throwable t)
      {
         throw new AeMessageAcknowledgeException(t);
      }
   }

   /**
    * @see org.activebpel.wsio.IAeMessageAcknowledgeCallback#onNotAcknowledge(java.lang.Throwable)
    */
   public void onNotAcknowledge(Throwable aReason)
   {
      mapThrowableAsFault(aReason);
      try
      {
         recordInvokeAsDelivered(null);
      }
      catch (Throwable t)
      {
         AeException.logError(t);
      }
   }

   /**
    * Records the invoke as delivered by writing the transmission id to the database and setting the delivered
    * flag to true.
    * @param aMessageId
    * @throws Throwable
    */
   protected void recordInvokeAsDelivered(String aMessageId) throws Throwable
   {
      if ( isPersistent() )
      {
         IAeTransmissionTracker txManager = AeEngineFactory.getEngine().getTransmissionTracker();
         txManager.add(getInvoke().getTransmissionId(), aMessageId, IAeTransmissionTracker.TRANSMITTED_STATE);
      }
      setDelivered(true);
      // notify waiting threads.
      synchronized (this)
      {
         notify();
      }
   }

   /**
    * Creates a context for the given invoke and queues it to the engine for execution.
    * @param aInvoke
    * @param aContext
    * @param aAckCallback
    * @param aQueueForExecution
    *
    * @throws AeException
    */
   protected long queueReceiveData(IAeInvokeInternal aInvoke, IAeMessageContext aContext,
         IAeMessageAcknowledgeCallback aAckCallback, boolean aQueueForExecution) throws AeException
   {
      // create message data
      IAeWebServiceMessageData wsData = aInvoke.getInputMessageData();

      try
      {
         IAeMessageData data = AeDataConverter.convert(wsData);

         // create reply receiver.
         IAeReplyReceiver replyRec = createReplyReceiver(aInvoke, aContext);

         // Queue the invoke into the Engine. Once a response is ready (i.e. a message or a fault),
         // the queuedRespRec's onMessage or onFault method is called by the engine, which
         // in turn queues the response to be dispatched to the recipient.
         long subprocessId = AeEngineFactory.getEngine().queueReceiveData(data, replyRec, aContext, aAckCallback, aQueueForExecution);
         setChildProcessId(subprocessId);
         return subprocessId;
      }
      finally
      {
         // Close wsData attachmentstreams to eliminate redundant temporary files.
         List attachments = wsData.getAttachments();
         if (attachments != null)
         {
            for (Iterator wsItr = attachments.iterator(); wsItr.hasNext();)
            {
               AeCloser.close(((IAeWebServiceAttachment) wsItr.next()).getContent());
            }
         }
      }
   }

   /**
    * Creates and returns the reply receiver. This implementation returns a AeDurableQueuingReplyReceiver if
    * the invoke is a two way invoke. The current <code>AeInvokeResponse</code> is flagged as early reply
    * (async). Returns null for one-way invokes.
    * @param aInvoke
    * @param aWsioMsgContext
    * @throws AeException
    */
   protected IAeReplyReceiver createReplyReceiver(IAeInvokeInternal aInvoke, IAeMessageContext aWsioMsgContext)
         throws AeException
   {
      IAeReplyReceiver queuedRespRec = null;
      if ( !aInvoke.isOneWay() )
      {
         // always returns marker unless it's oneWay.
         getResponse().setEarlyReply(true);

         // Since this an early reply, the response receiver's onMessage or onFault
         // methods simply calls getEngine().queueInvokeData() or queueInvokeFault() respectively
         // instead of a typical notifyAll (since there should not be any threads waiting on this object.)

         if ( isPersistent() )
         {
            queuedRespRec = new AeDurableQueuingReplyReceiver(aInvoke);
         }
         else
         {
            queuedRespRec = new AeQueuingReplyReceiver(aInvoke);
         }
      }
      else
      {
         getResponse().setEarlyReply(false);
      }
      return queuedRespRec;
   }

   /**
    * Creates the message context required to queue.
    * @param aInvoke
    * @param aServiceName
    * @throws AeException
    */
   protected IAeMessageContext createMessageContext(IAeInvoke aInvoke, String aServiceName)
         throws AeException
   {
      AeExtendedMessageContext context = new AeExtendedMessageContext();

      context.setOperation(aInvoke.getOperation());
      context.setServiceName(aServiceName);

      IAeEndpointReference myEndpointRef = getMyEndpointReference(aInvoke);
      myEndpointRef.addProperty(new QName("urn:activebpel", "invokehandler"), aInvoke.getInvokeHandler()); //$NON-NLS-1$ //$NON-NLS-2$


      IAeEndpointReference partnerEndpointRef = getPartnerEndpointReference(aInvoke);
      // add any mapped headers to the target endpoint
      Element mappedHeaders = getMappedHeaders(aInvoke, partnerEndpointRef);
      if (mappedHeaders != null)
      {
         partnerEndpointRef.addExtensibilityElement(mappedHeaders);
      }
      // make sure all the addressing headers are delivered      
      IAeAddressingHeaders wsa = new AeAddressingHeaders(partnerEndpointRef.getSourceNamespace());
      try
      {
         wsa.setReferenceProperties(partnerEndpointRef.getReferenceProperties());
         partnerEndpointRef.getReferenceProperties().clear();
         partnerEndpointRef.clearPolicies();
      }
      catch (AeWsAddressingException ex)
      {
         throw new AeBusinessProcessException(ex.getLocalizedMessage(), ex);
      }
      
      wsa.setRecipient(partnerEndpointRef);
      wsa.setFrom(myEndpointRef);
      IAeEndpointReference replyTo = AeEngineFactory.getPartnerAddressing().mergeReplyEndpoint(myEndpointRef,
            wsa.getReplyTo());
      wsa.setReplyTo(replyTo);

      context.setWsAddressingHeaders(wsa);
      return (IAeMessageContext)context;
   }

   /**
    * Maps the error as BPEL or system fault.
    * @param aThrowable
    */
   protected void mapThrowableAsFault(Throwable aThrowable)
   {
      IAeFault fault = null;
      if ( aThrowable instanceof AeBpelException )
      {
         fault = ((AeBpelException)aThrowable).getFault();
      }
      else
      {
         fault = AeFaultFactory.getSystemErrorFault(aThrowable);
      }

      getResponse().setFaultData(fault.getFaultName(), AeDataConverter.convertFaultDataNoException(fault));

      // make sure we turn off the early reply flag for faults!
      // if not, the AeInMemoryQueueManager::AeInvokeWork::run() will attempt to call back the engine
      // on a onFault or onMessage invocation.
      getResponse().setEarlyReply(false);
   }

   /**
    * Returns the my endpoint reference.
    * @param aInvoke
    * @return my endpoint reference.
    */
   protected IAeEndpointReference getMyEndpointReference(IAeInvoke aInvoke) throws AeException
   {
      IAeEndpointReference endpoint = null;
      if ( aInvoke instanceof AeInvoke )
      {
         endpoint = ((AeInvoke)aInvoke).getMyReference();
      }
      else
      {
         endpoint = createEndpointReference(aInvoke.getMyEndpointReferenceString());
      }

      // get the correlation header
      String correlationHeaderValue = (String)endpoint.getProperties().get(
            IAePolicyConstants.CONVERSATION_ID_HEADER);
      if ( correlationHeaderValue != null )
      {
         QName headerName = IAePolicyConstants.CONVERSATION_ID_HEADER;
         // create a header element for the conversation id
         Document doc = AeXmlUtil.newDocument();
         Element convHeader = doc.createElementNS(headerName.getNamespaceURI(), headerName.getLocalPart());
         convHeader.appendChild(doc.createTextNode(correlationHeaderValue));
         endpoint.addReferenceProperty(convHeader);
      }
      IAeEndpointReference myEndpoint = new AeEndpointReference();
      myEndpoint.setReferenceData(endpoint);
      // we don't want to transmit our policy info in the replyTo
      myEndpoint.clearPolicies();

      return myEndpoint;
   }

   /**
    * Returns the partner endpoint reference for the given invoke.
    * @param aInvoke
    * @return endpoint reference.
    * @throws AeBusinessProcessException if aInvoke is not of type AeInvoke.
    */
   protected IAeEndpointReference getPartnerEndpointReference(IAeInvoke aInvoke)
         throws AeBusinessProcessException
   {
      IAeEndpointReference endpoint = null;
      if ( aInvoke instanceof AeInvoke )
      {
         endpoint = ((AeInvoke)aInvoke).getPartnerReference();
      }
      else
      {
         endpoint = createEndpointReference(aInvoke.getPartnerEndpointReferenceString());
      }
      
      return endpoint;
   }

   /**
    * Creates an abx:Headers element for the invoke by examining specific policies
    * 
    * If the process initiator header policy is in effect, we will add a mapped header as a 
    * reference property to the endpoint.
    * 
    * @param aInvoke
    * @param aEndpoint
    * @throws AeBusinessProcessException
    */
   protected Element getMappedHeaders(IAeInvoke aInvoke, IAeEndpointReference aEndpoint) throws AeBusinessProcessException
   {
      try
      {
         Element headers = null;
         
         // resolve policy references
         IAeContextWSDLProvider wsdlProvider = AeEngineFactory.getDeploymentProvider().findDeploymentPlan(aInvoke.getProcessId(), aInvoke.getProcessName());
         List resolvedPolicies = aEndpoint.getEffectivePolicies(wsdlProvider, aInvoke.getPortType(), aInvoke.getOperation());
         
         Map callProperties = AeEngineFactory.getPolicyMapper().getCallProperties(resolvedPolicies);
         // check for principal header policy 
         QName principalName = (QName) callProperties.get(IAePolicyConstants.TAG_ASSERT_MAP_PROCESS_INTIATOR);
         if (!AeUtil.isNullOrEmpty(principalName))
         {
            // create an abx:Headers element for the getMyRoleProperty function
            headers = AeXmlUtil.createElement(IAeConstants.ABX_NAMESPACE_URI, "abx", "Headers"); //$NON-NLS-1$ //$NON-NLS-2$
            String principal = aInvoke.getProcessInitiator();
            if (AeUtil.isNullOrEmpty(principal))
            {
               principal = IAePolicyConstants.ANONYMOUS_PRINCIPAL;
            }
            AeXmlUtil.addElementNS(headers, principalName.getNamespaceURI(), "abx:" + principalName.getLocalPart(), principal); //$NON-NLS-1$
            headers.setAttribute("operation", aInvoke.getOperation()); //$NON-NLS-1$
         }
         return headers;
      }
      catch (AeException ae)
      {
         throw new AeBusinessProcessException(ae.getLocalizedMessage(), ae);
      }
   }
   
   /**
    * Creates an endpoint reference object given the endpoint reference as a xml string.
    * @param aXml
    * @throws AeBusinessProcessException
    */
   protected IAeEndpointReference createEndpointReference(String aXml) throws AeBusinessProcessException
   {
      try
      {
         AeXMLParserBase parser = new AeXMLParserBase();
         parser.setNamespaceAware(true);
         parser.setValidating(false);
         Document dom = parser.loadDocumentFromString(aXml, null);
         IAeEndpointReference endpoint = new AeEndpointReference();
         endpoint.setReferenceData(AeXmlUtil.getFirstSubElement(dom));
         return endpoint;
      }
      catch (AeException e)
      {
         AeBusinessProcessException bpe = new AeBusinessProcessException(AeMessages.format(
               "AeProcessInvokeHandler.ENDPOINT_PARSE_ERROR", e.getMessage())); //$NON-NLS-1$
         bpe.setRootCause(e);
         throw bpe;
      }
   }

   /**
    * Returns the service name given the endpoint reference.
    * @param aEndpointReference
    * @return service name
    * @throws AeBusinessProcessException if the service name is not found.
    */
   protected String extractServiceName(IAeEndpointReference aEndpointReference)
         throws AeBusinessProcessException
   {
      // for process and subprocess invokes, the address is the endpoint service name.
      String serviceName = aEndpointReference.getAddress();
      if ( AeUtil.isNullOrEmpty(serviceName) )
      {
         throw new AeBusinessProcessException(AeMessages
               .getString("AeProcessInvokeHandler.ERROR_SERVICENAME_MISSING")); //$NON-NLS-1$
      }
      return serviceName;
   }

   /**
    * @return Returns the persistent.
    */
   protected boolean isPersistent()
   {
      return mPersistent;
   }

   /**
    * @param aPersistent The persistent to set.
    */
   protected void setPersistent(boolean aPersistent)
   {
      mPersistent = aPersistent;
   }

   /**
    * @return Returns the delivered.
    */
   protected boolean isDelivered()
   {
      return mDelivered;
   }

   /**
    * @param aDelivered The delivered to set.
    */
   protected void setDelivered(boolean aDelivered)
   {
      mDelivered = aDelivered;
   }

   /**
    * @return Returns the childProcessId.
    */
   protected long getChildProcessId()
   {
      return mChildProcessId;
   }

   /**
    * @param aChildProcessId The childProcessId to set.
    */
   protected void setChildProcessId(long aChildProcessId)
   {
      mChildProcessId = aChildProcessId;
   }

   /**
    * @return Returns the invoke.
    */
   protected IAeInvoke getInvoke()
   {
      return mInvoke;
   }

   /**
    * @param aInvoke The invoke to set.
    */
   protected void setInvoke(IAeInvoke aInvoke)
   {
      mInvoke = aInvoke;
   }

   /**
    * @return Returns the transmission id manager.
    */
   protected IAeTransmissionTracker getTransmissionTracker()
   {
      return AeEngineFactory.getEngine().getTransmissionTracker();
   }

   /**
    * @return Returns the response.
    */
   protected AeInvokeResponse getResponse()
   {
      return mResponse;
   }

   /**
    * @return Returns the endpointReference.
    */
   protected IAeEndpointReference getEndpointReference()
   {
      return mEndpointReference;
   }

   /**
    * @param aEndpointReference The endpointReference to set.
    */
   protected void setEndpointReference(IAeEndpointReference aEndpointReference)
   {
      mEndpointReference = aEndpointReference;
   }

   /**
    * @return Returns the serviceName.
    */
   protected String getServiceName()
   {
      return mServiceName;
   }

   /**
    * @param aServiceName The serviceName to set.
    */
   protected void setServiceName(String aServiceName)
   {
      mServiceName = aServiceName;
   }

   /**
    * Sets the processes deployment persistent type.
    * @param aServiceName process or subprocess servicename
    * @throws AeBusinessProcessException
    */
   protected void setPersistentType(IAeInvoke aInvoke, String aServiceName) throws AeBusinessProcessException
   {
      // check to see if the target process is persistent.
      AeRoutingInfo routingInfo = AeEngineFactory.getDeploymentProvider().getRoutingInfoByServiceName(
            aServiceName);
      boolean targetProcPersistent = routingInfo.getDeployment().getPersistenceType() != AeProcessPersistenceType.NONE;

      // check to see if the current (caller) process is persistent.
      IAeProcessDeployment deployedPlan = AeEngineFactory.getDeploymentProvider().findDeploymentPlan(
            aInvoke.getProcessId(), aInvoke.getProcessName());
      boolean callerProcPersistent = deployedPlan.getPersistenceType() != AeProcessPersistenceType.NONE;

      // for process and subprocess invokes, "persistent" invoke means that both, calling and the target
      // processes must be persistent.
      setPersistent(callerProcPersistent && targetProcPersistent);
   }

}
