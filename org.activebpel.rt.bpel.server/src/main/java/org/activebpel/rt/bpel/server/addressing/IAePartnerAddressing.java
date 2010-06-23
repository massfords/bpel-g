// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/addressing/IAePartnerAddressing.java,v 1.9 2008/02/17 21:38:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.addressing;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.soap.SOAPMessageContext;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.IAePartnerLink;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.impl.addressing.IAeAddressingHeaders;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.wsio.IAeWebServiceEndpointReference;
import org.activebpel.wsio.IAeWsAddressingHeaders;
import org.w3c.dom.Element;

/**
 * Partner Addressing refers to the process of finding the addressing information
 * for a given business partner. The endpoint addressing information is obtained from
 * one of the following sources:
 *    STATIC - The address exists within the business process deployment descriptor file.
 *             This is the easiest place to store the address but it's the least flexible
 *             since it requires redeploying the address in order to make a change and it
 *             also makes management difficult since the information is distributed across
 *             all of the deployed processes and makes aggregation and reporting cumbersome

 *   DYNAMIC - The address is not known until the execution of the process at which time
 *             it is dynamically assigned during the execution of an assign activity.
 *             Since there is nothing known about the endpoint until the process executes,
 *             this offers little in the way of management abilities.
 * 
 * PRINCIPAL - The authenticated principal that invoked the process is used as a key
 *             into a separate system which houses endpoint addresses. This allows all
 *             of the endpoints to be stored outside of the bpel process and allows
 *             a much greater degree of flexibility since they can be changed without
 *             having to redeploy the process. The only side effect is that this forces
 *             the web service layer to authenticate the caller - which is probably 
 *             already done in most production bpel processes.
 * 
 *   INVOKER - The address is extracted from the set of WS-Addressing Headers that came into the
 *             web service framework on an invoke. It is expected that the headers within the message
 *             will contain some kind of authentication information (i.e. WS-Security).
 */
public interface IAePartnerAddressing
{
   /**
    * Reads a static endpoint reference encoded within the element.
    * 
    * This is broken out from the deployment reader in the event that we ever move
    * away from WS-Addressing, which is a possibility until WS-Addressing becomes
    * an open source standard. We may need to have pluggable support for the xml
    * formatted endpoint reference objects.
    * @param aElement
    * @return IAeEndpointReference or null if the source type wasn't static
    */
   public IAeEndpointReference readFromDeployment(Element aElement) throws AeBusinessProcessException;
   
   /**
    * If the partner link's endpoint source is set to "invoker" then the code will
    * attempt to extract an IAeEndpointReference object from the metadata in the
    * SOAPMessageContext. Currently, this is limited to extracting WS-Addressing 
    * compliant SOAP headers from a SOAPMessageContext. This gets called from the
    * web service handler since that's the layer that has access to the context.
    * 
    * Called from the deployment descriptor if the source type for the partner link
    * was set to "invoker".
    * @param aContext
    */
   public IAeEndpointReference readFromContext(SOAPMessageContext aContext) throws AeBusinessProcessException;
   
   /**
    * If the endpoint source for the partner link was set to principal then the
    * addressing layer obtains the endpoint reference data from the principal's
    * provisioning data.
    * 
    * Called indirectly from the engine (through the deployment descriptor) during 
    * the queueing of any receive data. 
    * @param aLink
    * @param aPrincipal
    */
   public void updateFromPrincipal(IAePartnerLink aLink, String aPrincipal) throws AeBusinessProcessException;
   

   /**
    * Creates the required WS-Addressing headers for a response message, 
    *
    * Determines the proper reply destination endpoint as follows:
    * If the wsa Fault action is specified, the FaultTo endpoint is used, if available.
    * If present, the ReplyTo header is used. 
    * If ReplyTo is unavailable, use the From header
    * If neither ReplyTo nor From are available, default to the wsa anonymous role
    *  
    * If the request contains a MessageId, the required RelatesTo header is included 
    * with a RelationshipType of wsa:Reply  
    *
    * The From endpoint is the endpoint that was invoked 
    *   
    * @param aSource addressing headers from a request
    * @param aAction response action to use
    * @return The WS-Addressing headers for the response
    * @throws AeBusinessProcessException
    */
   public IAeAddressingHeaders getReplyAddressing(IAeWsAddressingHeaders aSource, String aAction ) throws AeBusinessProcessException;
 
   /**
    * Updates an endpoint reference with a set of addressing headers.  The result is an endpoint 
    * containing the complete set of required WS-Addressing elements set as headers within the 
    * reference properties collection 
    * 
    * @param aWsaHeaders
    * @param aEndpoint
    * @throws AeBusinessProcessException
    */
   public IAeEndpointReference updateEndpointHeaders(IAeAddressingHeaders aWsaHeaders, IAeWebServiceEndpointReference aEndpoint) throws AeBusinessProcessException;

   /**
    * Determines the implicit ReplyTo endpoint for a partner link
    * The wsdl for the myRole service endpoint MUST have a matching service definition
    * so we can determine the port and binding.
    * @param aProcessDeployment
    * @param aPartnerLink
    * @param aProcessPlan
    * @param aConversationId
    * 
    * @return ReplyTo endpoint for invoke
    * @throws AeBusinessProcessException
    */
   public IAeEndpointReference getMyRoleEndpoint(IAeProcessDeployment aProcessDeployment, AePartnerLinkDef aPartnerLink, QName aProcessPlan, String aConversationId) throws AeBusinessProcessException;
   
   /**
    * Merges 2 endpoints to update the target with information from the source 
    * @param aSource endpoint from wsa headers
    * @param aTarget existing partner endpoint
    * @return merged endpoint
    */
   public IAeEndpointReference mergeReplyEndpoint(IAeWebServiceEndpointReference aSource, IAeWebServiceEndpointReference aTarget) throws AeBusinessProcessException;
   
}
