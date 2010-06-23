// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/AeAxisInvokeHandler.java,v 1.4 2008/02/27 17:55:58 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Fault;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.axis.bpel.invokers.AeAxisInvokeContext;
import org.activebpel.rt.axis.bpel.invokers.AeInvokerFactory;
import org.activebpel.rt.axis.bpel.invokers.IAeInvoker;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeWSDLPolicyHelper;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.AeFaultFactory;
import org.activebpel.rt.bpel.impl.AeTimeoutPolicy;
import org.activebpel.rt.bpel.impl.queue.AeInvoke;
import org.activebpel.rt.bpel.server.AeCryptoUtil;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.invoke.AeInvokeContext;
import org.activebpel.rt.bpel.server.engine.invoke.AeWSIOInvokeHandler;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.AeFaultMatcher;
import org.activebpel.wsio.AeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceEndpointReference;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.invoke.AeInvokeResponse;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Transport;
import org.apache.axis.constants.Style;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.SOAPHeaderElement;
import org.w3c.dom.Element;

/**
 * Handles invoking of endpoint references on behalf of the business process engine.
 */
public class AeAxisInvokeHandler extends AeWSIOInvokeHandler
{
   private static final String MUST_UNDERSTAND_ATTRIBUTE = "mustUnderstand"; //$NON-NLS-1$
   private static final String ACTOR_ATTRIBUTE = "actor"; //$NON-NLS-1$
   /** namespace we're using for identifying the credentials embedded in the endpoint properties */
   private static final String CREDENTIALS_NAMESPACE = "http://active-endpoints/endpoint-credentials"; //$NON-NLS-1$

   /**
    * Invokes the partner operation. This is done by utlizing the endpoint
    * reference info to create an axis service and the operation
    * information in the queue object to create the call.  The input message
    * is then moved to the call input and vica versa on output.
    * @see org.activebpel.wsio.invoke.IAeInvokeHandler#handleInvoke(org.activebpel.wsio.invoke.IAeInvoke, java.lang.String)
    */
   public IAeWebServiceResponse handleInvoke(IAeInvoke aInvokeQueueObject, String aQueryData)
   {
      Operation operation = null;
      AeInvokeResponse response = new AeInvokeResponse();
      try
      {
         AeAxisInvokeContext invokeCtx = createInvokeContext();
         setupInvokeContext(invokeCtx, aInvokeQueueObject, aQueryData);
         invokeCtx.setResponse(response);
         operation = invokeCtx.getOperation();
         
         Call call = createCall(invokeCtx);
         // Set the transmission id property on call
         call.setProperty(RM_TRANS_ID, new Long(aInvokeQueueObject.getTransmissionId()));
         if ( aInvokeQueueObject.isOneWay() )
            call.getOperation().setMep(OperationType.ONE_WAY);
         invokeCtx.setCall(call);

         IAeInvoker invoker = AeInvokerFactory.getInvoker(invokeCtx);
         invoker.invoke(invokeCtx);
         extractMappedProperties(aInvokeQueueObject, call.getMessageContext());
         response = invokeCtx.getResponse();
      }
      catch (AxisFault e)
      {
         if (e.getCause() instanceof SocketTimeoutException)
         {
            IAeFault fault = AeFaultFactory.getTimeoutFault(AeMessages.getString("AeAxisInvokeHandler.InvokeTimeout")); //$NON-NLS-1$
            response.setFaultData(fault.getFaultName(), null);
            response.setErrorString(fault.getInfo());
            response.setErrorDetail(AeUtil.getStacktrace(e.getCause()));
         }
         else if (AeFaultFactory.isTimeoutFault(e.getFaultCode()))
         {
            response.setFaultData(e.getFaultCode(), null);
            response.setErrorString(e.getFaultString());
         }
         else
         {
            setFaultOnResponse(aInvokeQueueObject.getPortType(), operation, e, response);
         }
      }
      catch (Throwable t)
      {
         AeException.logError(t, t.getMessage());
         IAeFault fault = AeFaultFactory.getSystemErrorFault(t);
         response.setFaultData(fault.getFaultName(), null);
         response.setErrorString(t.getMessage());
         response.setErrorDetail(AeUtil.getStacktrace(t));
      }

      return response;
   }
  
   /**
    * Extracts mapped header properties from Axis MessageContext and adds to partnerLink on 
    * <code>IAeInvoke</code> object.
    * @param aInvoke
    */
   protected void extractMappedProperties(IAeInvoke aInvoke, MessageContext aMsgContext)
   {
      IAeEndpointReference myRef = ((AeInvoke)aInvoke).getMyReference();
      // Get any mapped message headers attached by our handler
      Element headers = (Element) aMsgContext.getProperty(AE_CONTEXT_MAPPED_PROPERTIES);
      if (headers != null) {
         headers.setAttribute("operation", aInvoke.getOperation()); //$NON-NLS-1$
         myRef.addExtensibilityElement(headers);
      }
   }

   /**
    * Create and configure the client call object.
    * @param aContext
    * @throws Exception
    */
   protected Call createCall(AeInvokeContext aContext) throws Exception
   {
      Service wsdlService = aContext.getService();
      IAeEndpointReference endpoint = aContext.getEndpoint();
      Operation operation = aContext.getOperation();
      String url = aContext.getEndpoint().getAddress(); 
      Style requestStyle = getAxisRequestStyle(wsdlService, operation.getName(), endpoint);

      // See if a timeout policy has been configured
      int timeout = AeEngineFactory.getEngineConfig().getWebServiceInvokeTimeout();
      Element timeoutPolicy = AeWSDLPolicyHelper.getPolicyElement(aContext.getPolicyList(), AeTimeoutPolicy.TIMEOUT_ID);
      if (timeoutPolicy != null)
         timeout = AeTimeoutPolicy.getTimeoutValue(timeoutPolicy);
      
      /**
       * TODO: KP add a special .pdd flag to indicate that we need a whole new one-off client for particular cases
      **/ 
      AeService service = new AeService();
      Call call = (Call)service.createCall();

      // set an empty soap service so axis doesn't bother trying to find specific one for invoke, since there won't be one
      call.setSOAPService( new SOAPService() );
      setTargetAddress(call, url);
      call.setTimeout(new Integer(timeout * 1000));
      call.setSOAPActionURI(aContext.getSoapAction());
      call.setOperationName(operation.getName());
      call.setProperty(Call.OPERATION_STYLE_PROPERTY, requestStyle.getName());
      
      // for policy assertions associated with our endpoint & wsdl subjects
      if ( !AeUtil.isNullOrEmpty(aContext.getPolicyList()) )
      {
         setupCallForPolicies(aContext.getPolicyList(), call);
      }
      else
      {
         setCredentialsOnCall(endpoint, call);
      }
            
      // Add all wsa:ReferenceProperties to the call header, per WS-Addressing spec
      Iterator refProps = endpoint.getReferenceProperties().iterator();
      SOAPHeaderElement header = null;
      while (refProps.hasNext())
      {
         header = new SOAPHeaderElement((Element)refProps.next());
         // special handling for credentials stored as reference properties
         // in the header
         if ( header.getNamespaceURI().equals(CREDENTIALS_NAMESPACE) )
            continue;
         
         // Axis will add duplicate actor and mustUnderstand attributes that will 
         // cause issues later if we do not remove them explicitly
         String actor = header.getAttributeNS(IAeConstants.SOAP_NAMESPACE_URI, ACTOR_ATTRIBUTE);
         if (actor != null)
         {
            header.removeAttribute(ACTOR_ATTRIBUTE);
            header.setActor(actor);
         }
         String mustUnderstand = header.getAttributeNS(IAeConstants.SOAP_NAMESPACE_URI, MUST_UNDERSTAND_ATTRIBUTE);
         if (mustUnderstand != null)
         {
            header.removeAttribute(MUST_UNDERSTAND_ATTRIBUTE);
            header.setMustUnderstand(AeUtil.toBoolean(mustUnderstand));
         }
                  
         call.addHeader(header);
      }
      
      return call;
   }

   /**
    * Determine the call style (RPC or Document).
    *
    * @param aWsdlService
    * @param aOperationName
    * @param aEndpointRef
    * @throws AeBusinessProcessException
    */
   protected Style getAxisRequestStyle( Service aWsdlService, String aOperationName, IAeWebServiceEndpointReference aEndpointRef ) throws AeBusinessProcessException
   {
      
      return Style.getStyle(getRequestStyle(aWsdlService, aOperationName, aEndpointRef));
   }

   /**
    * Create the invoke context.
    */
   protected AeAxisInvokeContext createInvokeContext()
   {
      AeAxisInvokeContext ctx = new AeAxisInvokeContext();
      return ctx;
   }

   /**
    * Sets the fault data on the response.
    *
    * @param aPortType namespace of the port type we invoked
    * @param aOper the operation we invoked
    * @param aAxisFault fault generated from invoke
    * @param aResponse response object we're populating
    */
   protected void setFaultOnResponse(QName aPortType, Operation aOper, AxisFault aAxisFault, AeInvokeResponse aResponse)
   {
      QName faultCode = aAxisFault.getFaultCode();
      Element[] details = aAxisFault.getFaultDetails();
      Element firstDetailElement = details != null && details.length > 0 ? details[0] : null;

      AeFaultMatcher faultMatcher = new AeFaultMatcher(aPortType, aOper, faultCode, firstDetailElement);
      Fault wsdlFault = faultMatcher.getWsdlFault();
      QName faultName = faultMatcher.getFaultName();
      if (faultName == null)
      {
         faultName = new QName(Constants.NS_URI_AXIS, Constants.FAULT_SERVER_GENERAL);
      }

      AeWebServiceMessageData data = null;
      if ( wsdlFault != null )
      {
         // if we have a wsdl fault, then the faultName is the QName of the wsdl fault
         // and the data is extracted from the firstDetailElement
         data = extractMessageData(wsdlFault, firstDetailElement);
      }

      aResponse.setFaultData(faultName, data);
      aResponse.setErrorString(aAxisFault.getFaultString());

      // we weren't able to extract a WSDL fault, convert the error details
      // into a human readable string for debugging purposes.
      if ( data == null )
      {
         aResponse.setErrorDetail(getErrorDetail(aAxisFault));
      }
   }

   /**
    * Converts the Element[] in Axis's faultDetail to a String for as the error
    * detail string in our response.
    *
    * @param aFault
    */
   protected String getErrorDetail(AxisFault aFault)
   {
      String errorDetail = null;
      Element[] details = aFault.getFaultDetails();
      if ( details != null )
      {
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);

         for (int i = 0; i < details.length; i++)
         {
            // todo should we include the element names?
            pw.println(AeXmlUtil.getText(details[i]));
         }

         errorDetail = sw.toString();
      }
      return errorDetail;
   }

   /**
    * Extracts the credentials (if any) from the endpoint reference and sets them
    * on the call object.
    * @param aEndpointReference
    * @param aCall
    */
   protected void setCredentialsOnCall(IAeWebServiceEndpointReference aEndpointReference, 
                                      Call aCall)
   {
      String username = aEndpointReference.getUsername();
      if ( !AeUtil.isNullOrEmpty(username) )
      {
         aCall.setUsername(username);
      }
      String password = aEndpointReference.getPassword();
      if ( !AeUtil.isNullOrEmpty(password) )
      {
         aCall.setPassword(password);
      }
   }
   
   /**
    * Subclasses may override this method if they handle URIs for non-standard protocols
    * 
    * @param aCall
    * @param aUrl
    * @throws MalformedURLException
    */
   protected void setTargetAddress(Call aCall, String aUrl) throws MalformedURLException
   {
      aCall.setTargetEndpointAddress(new URL(aUrl));
   }
   
   /**
    * Call setup for policy assertions.
    * @param aPolicyList
    * @param aCall
    */
   protected void setupCallForPolicies( List aPolicyList, Call aCall ) throws AeException
   {
      try {
         // Map policy assertions to call properties
         if (!AeUtil.isNullOrEmpty(aPolicyList)) 
         {
            Map props = getPolicyDrivenProperties(aPolicyList);
            for (Iterator it = props.keySet().iterator(); it.hasNext();)
            {
                String name = (String) it.next();
                if (name.equals(TAG_ASSERT_AUTH_USER))
                   aCall.setUsername((String) props.get(name));
                else if (name.equals(TAG_ASSERT_AUTH_PASSWORD))
                {
                   String password = (String) props.get(name);
                   aCall.setPassword(AeCryptoUtil.decryptString(password));
                }
                else if (name.equals(PARAM_TRANSPORT))
                {
                   Transport transport = (Transport) props.get(name);
                   transport.setUrl(aCall.getTargetEndpointAddress());
                   aCall.setTransport(transport);
                }
                else   
                   aCall.setProperty(name, props.get(name));
            }
         }
      } 
      catch (Throwable t) 
      {
         throw new AeException(t.getLocalizedMessage(),t);  
      }
   }
}
