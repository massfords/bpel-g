// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/deploy/AeWsddBuilder.java,v 1.2 2007/12/27 17:58:05 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.deploy;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.server.deploy.AeDeploymentException;
import org.activebpel.rt.bpel.server.deploy.IAePolicyMapper;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.activebpel.rt.bpel.server.deploy.IAeWsddConstants;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Builder class for creating a wsdd deployment document.
 */
public class AeWsddBuilder implements IAeWsddConstants
{

   /** Wsdd document. */
   protected Document mWsddDocument;

   /**
    * Create an undeployment document for web service removal.
    * @param aServiceNames
    */
   public static Document createUndeployDocument( Collection aServiceNames )
   throws AeDeploymentException
   {
      AeWsddBuilder builder = new AeWsddBuilder();
      
      // for undeployment build a list of service names to be removed
      for( Iterator iter = aServiceNames.iterator(); iter.hasNext(); )
      {
         String serviceName = (String)iter.next();
         builder.addServiceElement( serviceName, null );
      }
      
      return builder.getWsddDocument();
   }

   /**
    * Constructor. Calls initDocument().
    */
   public AeWsddBuilder()
   {
      initDocument();
   }

   /**
    * Creates a wsdd document with an empty (no children) deployment child node.
    */
   protected void initDocument()
   {
      mWsddDocument = AeXmlUtil.newDocument();
      Element element = createElement(TAG_DEPLOYMENT);
      mWsddDocument.appendChild( element );

      element.setAttribute( "xmlns",      WSDD_NAMESPACE_URI ); //$NON-NLS-1$
      element.setAttribute( "xmlns:xsi",  W3C_XML_SCHEMA_INSTANCE ); //$NON-NLS-1$
      element.setAttribute( "xmlns:proc", PROVIDER_NAMESPACE_URI ); //$NON-NLS-1$

   }

   /**
    * Creates wsdd service deployments from the information
    * 
    * @param aServices
    * @throws AeDeploymentException
    */
   public void addServices(IAeServiceDeploymentInfo[] aServices) throws AeDeploymentException
   {
      for (int i = 0; i < aServices.length; i++)
      {
         IAeServiceDeploymentInfo serviceData = aServices[i];
         if( serviceData.isRPCEncoded() )
         {
            addRpcService( serviceData );
         }
         else if (serviceData.isRPCLiteral())
         {
            addRpcLiteralService( serviceData );
         }
         else if (serviceData.isMessageService())
         {
            addMsgService( serviceData );
         }
         else if (serviceData.isPolicyService())
         {
            addPolicyService( serviceData );
         }
      }
   }
   
   /**
    * Add a bpel rpc service element.
    * @param aServiceData
    */
   public void addRpcService(IAeServiceDeploymentInfo aServiceData) throws AeDeploymentException
   {
      addService( aServiceData, TAG_RPC_BINDING);
   }

   /**
    * Add a bpel rpc literal service element
    * @param aServiceData
    */
   public void addRpcLiteralService(IAeServiceDeploymentInfo aServiceData) throws AeDeploymentException
   {
      addService( aServiceData, TAG_RPC_LIT_BINDING );
   }

   /**
    * Add a bpel msg service element.
    * @param aServiceData
    */
   public void addMsgService(IAeServiceDeploymentInfo aServiceData) throws AeDeploymentException
   {
      addService( aServiceData, TAG_MSG_BINDING );
   }

   /**
    * Add a bpel service element where the provider is defined as a parameter in the WSDD.
    * @param aServiceData
    */
   public void addPolicyService( IAeServiceDeploymentInfo aServiceData )
   throws AeDeploymentException
   {
      addService( aServiceData, TAG_POLICY_BINDING );
   }
   
   
   /**
    * Utility method for adding a service element configured with the 
    * appropriate parameter children.
    * @param aServiceData
    * @param aBinding
    */
   protected void addService(IAeServiceDeploymentInfo aServiceData, String aBinding) throws AeDeploymentException
   {
      Element serviceElement = addServiceElement( aServiceData.getServiceName(), aBinding );

      // add process info to wsdd doc
      serviceElement.appendChild( createParamElement( TAG_PROCESS_NS, aServiceData.getProcessQName().getNamespaceURI() ) );
      serviceElement.appendChild( createParamElement( TAG_PROCESS_NAME, aServiceData.getProcessQName().getLocalPart() ) );
      
      // add partner link to wsdd doc
      serviceElement.appendChild( createParamElement( TAG_PARTNER_LINK, aServiceData.getPartnerLinkDefKey().getPartnerLinkName() ) );
      serviceElement.appendChild( createParamElement( TAG_PARTNER_LINK_ID, String.valueOf(aServiceData.getPartnerLinkDefKey().getPartnerLinkId()) ) );

      if( !AeUtil.isNullOrEmpty(aServiceData.getAllowedRoles()) )
      {
         serviceElement.appendChild( createParamElement( TAG_ALLOWED_ROLES, aServiceData.getAllowedRolesAsString() ) );
      }

      try 
      {
         // get the main policy mapper
         IAePolicyMapper mapper = (IAePolicyMapper) AeEngineFactory.getPolicyMapper();
         if (mapper != null)
         {
            // get Service Parameters
            List handlers = mapper.getServiceParameters(aServiceData.getPolicies());
            if (!AeUtil.isNullOrEmpty(handlers)) 
            {
               for (Iterator it = handlers.iterator(); it.hasNext();) 
               {
                  serviceElement.appendChild(getWsddDocument().importNode((Element) it.next(), true));           
               }
            }
            // get Server Request handlers
            handlers = mapper.getServerRequestHandlers(aServiceData.getPolicies());
            if (!AeUtil.isNullOrEmpty(handlers)) 
            {
               Element requestFlow = (Element) serviceElement.appendChild( createElement( TAG_REQUEST_FLOW) );
               for (Iterator it = handlers.iterator(); it.hasNext();) 
               {
                    requestFlow.appendChild(getWsddDocument().importNode((Element) it.next(), true));           
               }
            }
            // get Server Response handlers
            handlers = mapper.getServerResponseHandlers(aServiceData.getPolicies());
            if (!AeUtil.isNullOrEmpty(handlers)) 
            {
               Element responseFlow = (Element) serviceElement.appendChild( createElement( TAG_RESPONSE_FLOW) );
               for (Iterator it = handlers.iterator(); it.hasNext();) 
               {
                  responseFlow.appendChild(getWsddDocument().importNode((Element) it.next(), true));           
               }
            }
         }
      } 
      catch (Throwable t) 
      {
         throw new AeDeploymentException("Unable to create handler chain for policies server",t); //$NON-NLS-1$
      }
   }

   /**
    * Create a client side handler deployment document for policy assertions.
    * @param aPolicyList
    */
   public static Document getClientDeployment( List aPolicyList )
   throws AeDeploymentException
   {
      AeWsddBuilder builder = new AeWsddBuilder();

      builder.createClientDocument(aPolicyList);

      return builder.getWsddDocument();
   }


   /**
    * Create a client side handler deployment document for policy assertions.
    * @param aPolicyList
    */
   protected void createClientDocument( List aPolicyList )
   throws AeDeploymentException
   {
      try {

         // Create global config element
         Element globalConfig = (Element) getWsddDocument().getDocumentElement().appendChild( createElement( TAG_GLOBAL_CONFIG) );

         // Map policy assertions to service-specific handler definitions
         // add handlers to service deployment
         if (!AeUtil.isNullOrEmpty(aPolicyList)) 
         {
            // get the main policy mapper
            IAePolicyMapper mapper = (IAePolicyMapper) AeEngineFactory.getPolicyMapper();
            // get Client Request handlers
            List handlers = mapper.getClientRequestHandlers(aPolicyList);
            if (!AeUtil.isNullOrEmpty(handlers)) 
            {
               Element requestFlow = (Element) globalConfig.appendChild( createElement( TAG_REQUEST_FLOW) );
               for (Iterator it = handlers.iterator(); it.hasNext();) 
               {
                    Element handler = (Element) it.next();
                    requestFlow.appendChild(getWsddDocument().importNode(handler, true));
               }
            }
            // get Client Response handlers
            handlers = mapper.getClientResponseHandlers(aPolicyList);
            if (!AeUtil.isNullOrEmpty(handlers)) 
            {
               Element responseFlow = (Element) globalConfig.appendChild( createElement( TAG_RESPONSE_FLOW) );
               for (Iterator it = handlers.iterator(); it.hasNext();) 
               {
                  Element handler = (Element) it.next();
                  responseFlow.appendChild(getWsddDocument().importNode(handler, true));
               }
            }

         }
      } 
      catch (Throwable t) 
      {
         throw new AeDeploymentException("Unable to create handler chain for policies",t); //$NON-NLS-1$
      }
   }



   /**
    * Convenience method for generating the service element.
    * @param aName the value of the name attribute
    * @param aProvider the value of the provider attribute, can be null for undeployment
    * @return the service element
    */
   protected Element addServiceElement(String aName, String aProvider )
   {
      Element serviceElement = createElement(TAG_SERVICE);
      serviceElement.setAttributeNS(null, TAG_NAME, aName );
      if(aProvider != null)
         serviceElement.setAttributeNS(null, TAG_PROVIDER, aProvider );
      getWsddDocument().getDocumentElement().appendChild(serviceElement);
      return serviceElement;
   }

   /**
    * Creates a parameter element for building wsdd.
    * @param aName value of the name attribute for the element
    * @param aValue value of the value attribute for the element
    * @return wsdd parameter element
    */
   protected Element createParamElement( String aName, String aValue )
   {
      Element paramElement = createElement(TAG_PARAMETER);
      setAttribute( paramElement, TAG_NAME, aName );
      setAttribute( paramElement, TAG_VALUE, aValue );
      return paramElement;
   }

   /**
    * Utility method for creating a NS aware element.
    * @param aName element tag name
    * @return ns aware dom element
    */
   protected Element createElement( String aName )
   {
      return getWsddDocument().createElementNS( WSDD_NAMESPACE_URI, aName );
   }

   /**
    * Utility method for adding attribute to element.
    * @param aElement target element
    * @param aName attribute ncname
    * @param aValue attribute value
    */
   protected void setAttribute( Element aElement, String aName, String aValue )
   {
      aElement.setAttributeNS( null, aName, aValue );
   }

   /**
    * Accessor for wsdd xml.
    * @return wsdd dom
    */
   public Document getWsddDocument()
   {
      return mWsddDocument;
   }
}
