// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeServiceDeploymentUtil.java,v 1.1 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDefKey;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Static utilities to determine service info from deployment documents
 */
public class AeServiceDeploymentUtil implements IAeConstants
{
   
   /** xpath to service name */
   private static XPath mServiceNameXPath;
   /** xpath to the provider name */
   private static XPath mProviderXPath;
   /** xpath to the partnerlink */
   private static XPath mMyPartnerLinkPath;
   /** xpath to the partnerlink location */
   private static XPath mMyPartnerLinkLocPath;
   /** xpath to the allowedRoles path */
   private static XPath mAllowedRolesPath;  
   /** xpath to the process name */
   private static XPath mProcessNamePath;
  
   /**
    * Initialize the xpaths used to generate wsdd
    * from ae deployment xml.
    * @throws AeDeploymentException
    */
   static
   {
      try
      {
         mServiceNameXPath     = new DOMXPath("@service"); //$NON-NLS-1$
         mProviderXPath        = new DOMXPath("@binding"); //$NON-NLS-1$
         mMyPartnerLinkPath    = new DOMXPath("parent::node()/@name"); //$NON-NLS-1$
         mMyPartnerLinkLocPath = new DOMXPath("parent::node()/@location"); //$NON-NLS-1$
         mAllowedRolesPath     = new DOMXPath("@allowedRoles"); //$NON-NLS-1$
         mProcessNamePath      = new DOMXPath("@name"); //$NON-NLS-1$
      }
      catch (JaxenException e)
      {
         IllegalStateException ise = new IllegalStateException(AeMessages.getString("AeWsddService.ERROR_5")); //$NON-NLS-1$
         ise.initCause(e);
         throw ise;
      }
   }
   
   /**
    * Gets the service info from the pdd element
    * 
    * @param aProcessDef
    * @param aProcessElement
    * @throws AeDeploymentException
    */
   public static IAeServiceDeploymentInfo[] getServices( AeProcessDef aProcessDef, Element aProcessElement) throws AeDeploymentException
   {
      try
      {
         // data for creating the processNamespace and
         // processName parameter elements
         // - this data is static for all partnerLink elements 
         // with the myRole child for a given process
         String processNSUri = getProcessNameNsUri( aProcessElement );
         String processLocalName = getProcessNameLocalPart( aProcessElement );      
         QName processQname = new QName( processNSUri, processLocalName );
         
         List services = new ArrayList();
         
         // locate all of the myRole elements and build the 
         // appropriate service element for each one
         List myRoleElements = getMyRoleElementNodes( aProcessElement );
         for (Iterator iter = myRoleElements.iterator(); iter.hasNext();)
         {
            Element myRoleElement = (Element) iter.next();
            String serviceName = getServiceName( myRoleElement );
            String binding = getServiceProvider( myRoleElement );
            
            // Get the partner link name and (optional) location.
            String partnerLinkName = getMyPartnerLinkName(myRoleElement);
            String partnerLinkLocation = getMyPartnerLinkLocation(myRoleElement);
            String partnerLink = partnerLinkName;
            if (AeUtil.notNullOrEmpty(partnerLinkLocation))
               partnerLink = partnerLinkLocation;

            // Look up the partner link def in the process.
            AePartnerLinkDef plDef = aProcessDef.findPartnerLink(partnerLink);
            AePartnerLinkDefKey plDefKey = new AePartnerLinkDefKey(plDef);

            String allowedRoles = getAllowedRoles( myRoleElement );
            List policies = getPolicyNodes(myRoleElement);
            
            AeServiceDeploymentInfo serviceData = new AeServiceDeploymentInfo(serviceName, plDefKey, binding, allowedRoles, policies);
            serviceData.setProcessQName(processQname);
            services.add(serviceData);
         }
                           
         IAeServiceDeploymentInfo[] servicesAdded = new IAeServiceDeploymentInfo[services.size()];
         services.toArray(servicesAdded);
         return servicesAdded;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new AeDeploymentException(AeMessages.getString("AeWsddService.ERROR_7"),e); //$NON-NLS-1$
      }
   }
   
   /**
    * XPath selection of myRole elements from process element.
    * @param aProcessElement 
    * @return list of matching element nodes.
    * @throws Exception
    */
   protected static List getMyRoleElementNodes( Element aProcessElement )
   throws Exception
   {
      NodeList myRoles = aProcessElement.getElementsByTagNameNS(aProcessElement.getNamespaceURI(),"myRole"); //$NON-NLS-1$
      List elements = new ArrayList(myRoles.getLength());
      for (int i = 0; i < myRoles.getLength(); i++)
      {
         elements.add(myRoles.item(i));
      }
      return elements;
   }
   
   /**
    * Gets policy nodes from a myRole or partnerRole element.
    * @param aElement 
    * @return list of matching element nodes.
    * @throws Exception
    */
   protected static List getPolicyNodes( Element aElement )
   throws Exception
   {
      NodeList policies = aElement.getElementsByTagNameNS(IAeConstants.WSP_NAMESPACE_URI,"*"); //$NON-NLS-1$
      List elements = new ArrayList(policies.getLength());
      for (int i = 0; i < policies.getLength(); i++)
      {
         elements.add(policies.item(i));
      }
      return elements;
   }   
   
   
   /**
    * XPath selection of service attribute from process element.
    * @param aProcessElement
    * @return value of service attribute
    * @throws Exception
    */
   protected static String getServiceName( Element aProcessElement ) throws Exception
   {
      return (String)mServiceNameXPath.stringValueOf(aProcessElement);
   }
   
   /**
    * XPath selection of binding attribute.
    * <br />
    * @param aProcessElement
    * @return the provider type
    * @throws Exception
    */
   protected static String getServiceProvider( Element aProcessElement ) throws Exception
   {
      return (String)mProviderXPath.stringValueOf(aProcessElement);
   }
   
   /**
    * XPath to the name attribute of proces element and return
    * the namespace uri from the name value prefix.
    * @param aProcessElement
    * @return namespace uri
    * @throws Exception
    */
   protected static String getProcessNameNsUri( Element aProcessElement ) 
   throws Exception
   {
      String name = mProcessNamePath.stringValueOf(aProcessElement);
      String prefix = AeXmlUtil.extractPrefix(name);
      if( prefix == null )
      {
         return ""; //$NON-NLS-1$
      }
      return AeXmlUtil.getNamespaceForPrefix( aProcessElement, prefix );
   }
   
   /**
    * XPath to the name attribute of the process element and return
    * the local part from the name value.
    * @param aProcessElement
    * @return the local part of the name value
    * @throws Exception
    */
   protected static String getProcessNameLocalPart( Element aProcessElement )
   throws Exception
   {
      return AeXmlUtil.extractLocalPart( mProcessNamePath.stringValueOf(aProcessElement) );      
   }
   
   /**
    * XPath to the value of the parent::node()/@name
    * @param aRoleElement
    * @return name attribute value for the partnerLink child element
    * @throws Exception
    */
   protected static String getMyPartnerLinkName( Element aRoleElement ) throws Exception
   {
      return mMyPartnerLinkPath.stringValueOf( aRoleElement );
   }
   
   /**
    * XPath to the value of the parent::node()/@location.
    * 
    * @param aRoleElement
    * @throws Exception
    */
   protected static String getMyPartnerLinkLocation( Element aRoleElement ) throws Exception
   {
      return mMyPartnerLinkLocPath.stringValueOf( aRoleElement );
   }
   
   /**
    * XPath to the value of the allowedRoles attribute.
    * 
    * @param aRoleElement
    * @return value of allowedRoles attribute
    * @throws Exception
    */
   protected static String getAllowedRoles( Element aRoleElement ) throws Exception
   {
      return mAllowedRolesPath.stringValueOf(aRoleElement);
   }
}
