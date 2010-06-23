// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/IAeConstants.java,v 1.17 2007/12/07 15:57:24 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt;

/**
 * Standard constants used in Active Endpoints projects.  
 */
public interface IAeConstants
{
   /** XML namespace declaration */
   public static final String W3C_XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace"; //$NON-NLS-1$
   /** XML Schema namespace declaration */
   public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema" ; //$NON-NLS-1$
   /** XML Schema instance namespace declaration */
   public static final String W3C_XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance" ; //$NON-NLS-1$
   /** XSL namespace declaration */
   public static final String XSL_NAMESPACE = "http://www.w3.org/1999/XSL/Transform"; //$NON-NLS-1$
   /** XML Namespace declaration for W3C XML Namespace declaration */
   public static final String W3C_XMLNS = "http://www.w3.org/2000/xmlns/"; //$NON-NLS-1$
   /** Web Services Addressing namespace declaration. */
   public static final String WSA_NAMESPACE_URI = "http://schemas.xmlsoap.org/ws/2003/03/addressing";    //$NON-NLS-1$
   /** Web Services Addressing namespace declaration. */
   public static final String WSA_NAMESPACE_URI_2005_08 = "http://www.w3.org/2005/08/addressing";    //$NON-NLS-1$
   /** Web Services Addressing namespace declaration. */
   public static final String WSA_NAMESPACE_URI_2004_08 = "http://schemas.xmlsoap.org/ws/2004/08/addressing";    //$NON-NLS-1$
   /** Web Services Addressing namespace declaration. */
   public static final String WSA_NAMESPACE_URI_2004_03 = "http://schemas.xmlsoap.org/ws/2004/03/addressing";    //$NON-NLS-1$
   /** Web Services Policy namespace declaration. */
   public static final String WSP_NAMESPACE_URI = "http://schemas.xmlsoap.org/ws/2004/09/policy";    //$NON-NLS-1$
   /** Web Services Security Policy namespace declaration. */
   public static final String SP_NAMESPACE_URI = "http://schemas.xmlsoap.org/ws/2005/07/securitypolicy";    //$NON-NLS-1$
   /** ActiveBPEL Policy namespace declaration. */
   public static final String ABP_NAMESPACE_URI = "http://schemas.active-endpoints.com/ws/2005/12/policy";    //$NON-NLS-1$
   /** WS-Security 1.0 Policy namespace declaration. */
   public static final String WSS_NAMESPACE_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";    //$NON-NLS-1$
   /** WS-Security Util 1.0 Policy namespace declaration. */
   public static final String WSU_NAMESPACE_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";    //$NON-NLS-1$
   /** SOAP env namespace declaration. */
   public static final String SOAP_NAMESPACE_URI = "http://schemas.xmlsoap.org/soap/envelope/";    //$NON-NLS-1$
   /** W3C digital signature namespace. */
   public static final String DSIG_NAMESPACE_URI = "http://www.w3.org/2000/09/xmldsig#";    //$NON-NLS-1$
   /** W3C xml encryption namespace. */
   public static final String XENC_NAMESPACE_URI = "http://www.w3.org/2001/04/xmlenc#";    //$NON-NLS-1$
   /** ActiveBPEL extensions namespace */
   public static final String ABX_NAMESPACE_URI = "http://www.activebpel.org/bpel/extension";  //$NON-NLS-1$
   /** WS-ReliableMessaging namespace */
   public static final String WSRM_NAMESPACE_URI = "http://schemas.xmlsoap.org/ws/2005/02/rm"; //$NON-NLS-1$
   /** WS-ReliableMessaging policynamespace */
   public static final String WSRM_POLICY_NAMESPACE_URI = "http://schemas.xmlsoap.org/ws/2005/02/rm/policy"; //$NON-NLS-1$
   /** Standard WSDL namespace */
   public static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/"; //$NON-NLS-1$

   /** ActiveBPEL policy namespace. 
    * Note - this is deprecated since new versions use ABP_NAMESPACE_URI, but should still check for this to provide backward compatibility.
    * */
   public static final String ABPEL_POLICY_NS = "http://www.activebpel.org/policies"; //$NON-NLS-1$

   /** System property key for setting the transformer factory impl. */
   public static final String SYSTEM_PROPERTY_TRANSFORMER_FACTORY_IMPL = "urn:active-endpoints:java:system-property:transformer-factory-impl"; //$NON-NLS-1$
   
   /** Extension name for bpel files. Could be used at other places too. */
   public static final String BPEL = "bpel"; //$NON-NLS-1$
}
