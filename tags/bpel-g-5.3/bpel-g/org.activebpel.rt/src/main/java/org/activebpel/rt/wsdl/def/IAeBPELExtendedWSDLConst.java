// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/IAeBPELExtendedWSDLConst.java,v 1.19 2007/07/27 18:08:54 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def;

import javax.xml.namespace.QName;

import org.activebpel.rt.IAeConstants;

/**
 * This interface defines several String constants used commonly throughout this
 * package.
 */
public interface IAeBPELExtendedWSDLConst extends IAeConstants
{
   /** Standard SOAP namespace */
   public static final String SOAP_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap/"; //$NON-NLS-1$

   /** Standard SOAP namespace prefix */
   public static final String SOAP_PREFIX_DEFAULT = "soap"; //$NON-NLS-1$

   /** Standard SOAP encoding URI */
   public static final String SOAP_ENCODING = "http://schemas.xmlsoap.org/soap/encoding/"; //$NON-NLS-1$
   
   /** QName for SOAP encoded array */
   public static final QName SOAP_ARRAY = new QName(SOAP_ENCODING, "Array"); //$NON-NLS-1$

   /** Standard HTTP namespace (no trailing slash per spec) */
   public static final String SOAP_HTTP_TRANSPORT = "http://schemas.xmlsoap.org/soap/http"; //$NON-NLS-1$

   // TODO: bpws, wsbpel and wsbpel-abstractprocess namespaces and prefixes are also declared in IAeBPELConstants. Refactor such that these are defined only in a single file.
   
   /** BPEL4WS 1.1 namespace declaration */
   public static final String BPWS_NAMESPACE_URI = "http://schemas.xmlsoap.org/ws/2003/03/business-process/"; //$NON-NLS-1$

   /** Preferred BPEL4WS 1.1 Prefix. */   
   public static final String BPWS_PREFIX = "bpws"; //$NON-NLS-1$

   /** WSBPEL 2.0 namespace declaration */
   public static final String WSBPEL_2_0_NAMESPACE_URI = "http://docs.oasis-open.org/wsbpel/2.0/process/executable"; //$NON-NLS-1$
   
   /** Preferred WSBPEL 2.0 Prefix. */   
   public static final String WSBPEL_2_0_PREFIX = "bpel"; //$NON-NLS-1$
   
   /** WSBPEL 2.0 abstract process namespace declaration */
   public static final String WSBPEL_2_0_ABSTRACT_NAMESPACE_URI = "http://docs.oasis-open.org/wsbpel/2.0/process/abstract"; //$NON-NLS-1$

   /** Preferred prefix for wsbpel abstract process namespace. */   
   public static final String ABSTRACT_PROC_PREFIX = "absbpel"; //$NON-NLS-1$   
   
   /** Standard Partner Link namespace */
   public static final String PARTNER_LINK_NAMESPACE = 
      "http://schemas.xmlsoap.org/ws/2003/05/partner-link/"; //$NON-NLS-1$

   /** Preferred BPEL4WS 1.1 partner link prefix. */
   public static final String PARTNER_LINK_PREFIX = "plnk"; //$NON-NLS-1$
   
   /** Partner Link namespace for WSBPEL 2.0 */
   public static final String WSBPEL_PARTNER_LINK_NAMESPACE = 
      "http://docs.oasis-open.org/wsbpel/2.0/plnktype"; //$NON-NLS-1$

   /** Preferred WSBPEL 2.0 partner link prefix. */
   public static final String WSBPEL_PARTNER_LINK_PREFIX = "plnk2";  //$NON-NLS-1$
   
   /** Standard Property and Property Alias v1.1 namespace */
   public static final String PROPERTY_1_1_NAMESPACE =
      "http://schemas.xmlsoap.org/ws/2003/03/business-process/"; //$NON-NLS-1$

   /** Standard Property and Property Alias v2.0 namespace */
   public static final String PROPERTY_2_0_NAMESPACE =
      "http://docs.oasis-open.org/wsbpel/2.0/varprop"; //$NON-NLS-1$

   /** Preferred Property and Property Alias prefix for v2.0 namespace. */   
   public static final String PROPERTY_2_0_PREFIX = "vprop"; //$NON-NLS-1$
   
   /** Preferred Policy prefix. */   
   public static final String POLICY_PREFIX = "wsp"; //$NON-NLS-1$

   /** Preferred ws-secutil prefix. */   
   public static final String WSUTIL_PREFIX = "wsu"; //$NON-NLS-1$
   
   public static final String PARTNER_LINK_TYPE_TAG = "partnerLinkType"; //$NON-NLS-1$

   public static final String PORT_TYPE_TAG = "portType"; //$NON-NLS-1$

   public static final String ROLE_TAG = "role"; //$NON-NLS-1$

   public static final String NAME_ATTRIB = "name"; //$NON-NLS-1$

   public static final String PROPERTY_TAG = "property"; //$NON-NLS-1$

   public static final String PROPERTY_ALIAS_TAG = "propertyAlias"; //$NON-NLS-1$

   public static final String TYPE_ATTRIB = "type"; //$NON-NLS-1$

   public static final String PROPERTY_NAME_ATTRIB = "propertyName"; //$NON-NLS-1$

   public static final String MESSAGE_TYPE_ATTRIB = "messageType"; //$NON-NLS-1$

   public static final String ELEMENT_TYPE_ATTRIB = "element"; //$NON-NLS-1$

   public static final String COMPLEX_TYPE_ATTRIB = "type"; //$NON-NLS-1$

   public static final String PART_ATTRIB = "part"; //$NON-NLS-1$

   public static final String QUERY_ATTRIB = "query"; //$NON-NLS-1$
   
   public static final String QUERY_LANGUAGE = "queryLanguage"; //$NON-NLS-1$

   public static final String VARIABLE_TAG = "variable"; //$NON-NLS-1$

   public static final String PARTNER_LINK_TAG = "partnerLink"; //$NON-NLS-1$

}
