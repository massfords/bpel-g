// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/IAePolicyConstants.java,v 1.19 2008/03/28 17:47:15 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt;

import javax.xml.namespace.QName;

/**
 * Standard constants used in Active Endpoints policies.  
 */
public interface IAePolicyConstants
{
   /**
    *  Generic Handler constants
    */
   public static final String TAG_VALUE_ATTR = "value"; //$NON-NLS-1$
   public static final String TAG_NAME_ATTR = "name"; //$NON-NLS-1$
   public static final String TAG_TYPE_ATTR = "type"; //$NON-NLS-1$
   
   /**
    *  Policy-Driven provider constants
    */
   public static final String PARAM_HANDLER_CLASS = "handlerClass"; //$NON-NLS-1$
   public static final String PARAM_DELEGATE_CLASS = "handlerDelegate"; //$NON-NLS-1$
   public static final String PARAM_TRANSPORT = "Transport"; //$NON-NLS-1$
   public static final String PARAM_RECEIVE_HANDLER = "receiveHandler"; //$NON-NLS-1$
   public static final String PARAM_STYLE = "Style"; //$NON-NLS-1$
   public static final String PARAM_USE = "Use"; //$NON-NLS-1$
   
   /**
    *  Engine managed correlation constants
    */
   public static final String TAG_ASSERT_MANAGED_CORRELATION = "engineManagedCorrelationPolicy"; //$NON-NLS-1$
   public static final QName CONVERSATION_ID_HEADER = new QName(IAeConstants.ABX_NAMESPACE_URI, "conversationId"); //$NON-NLS-1$

   /**
    *  MapProcessInitiatorAsHeader constants
    */
   public static final String TAG_ASSERT_MAP_PROCESS_INTIATOR = "MapProcessInitiatorAsHeader"; //$NON-NLS-1$
   public static final QName PRINCIPAL_HEADER = new QName(IAeConstants.ABX_NAMESPACE_URI, "principal"); //$NON-NLS-1$
   public static final String ANONYMOUS_PRINCIPAL = "anonymous"; //$NON-NLS-1$
   
   /**
    *  XPath receiver constants
    */
   public static final String TAG_ASSERT_XPATH_RECEIVE = "ReceiverXPathMap"; //$NON-NLS-1$ 
   public static final String TAG_ASSERT_XPATH_SEND = "SenderXPathMap"; //$NON-NLS-1$
   public static final String XPATH_QUERY_SOURCE = "XPathQuerySource"; //$NON-NLS-1$
   public static final String XPATH_QUERY_SOURCE_CONTEXT = "MessageContext"; //$NON-NLS-1$
   public static final String XPATH_QUERY_SOURCE_OPTIONS = "Options"; //$NON-NLS-1$
   public static final String XPATH_QUERY_PARAMS = "XPathQueryParams"; //$NON-NLS-1$   
   public static final String XMLNS_PREFIX = "xmlns:"; //$NON-NLS-1$
   public static final String XPATH_PREFIX = "xpath:"; //$NON-NLS-1$
   public static final String XPATH_MAP = "XPATH_MAP"; //$NON-NLS-1$
   public static final String AE_CONTEXT_MAPPED_PROPERTIES = "AE_MAPPED_PROPERTIES"; //$NON-NLS-1$

   /**
    *  Reliable Messaging constants
    */
   public static final String TAG_ASSERT_RM = "RMAssertion"; //$NON-NLS-1$
   public static final String TAG_ASSERT_RM_TIMEOUT = "InactivityTimeout"; //$NON-NLS-1$
   public static final String TAG_ASSERT_RM_RETRY_INTERVAL = "BaseRetransmissionInterval"; //$NON-NLS-1$
   public static final String TAG_ASSERT_EXP_BACKOFF = "ExponentialBackoff"; //$NON-NLS-1$   
   public static final String TAG_ASSERT_ACK_INTERVAL = "AcknowledgementInterval"; //$NON-NLS-1$   
   public static final String TAG_ASSERT_MILLIS = "Milliseconds"; //$NON-NLS-1$   
   public static final String TAG_ASSERT_RM_BINDING = "binding"; //$NON-NLS-1$
   public static final String TAG_ASSERT_RM_ACKTO = "AcksTo"; //$NON-NLS-1$
   public static final String TAG_ASSERT_RM_PROXY = "ProxyUrl"; //$NON-NLS-1$
   public static final String RM_TRANSPORT = "RMTransport"; //$NON-NLS-1$
   public static final String RM_TRANS_ID = "AeTransmissionId"; //$NON-NLS-1$
   
   /**
    *  WS-Security constants
    */
   public static final String ISSUER_SERIAL = "IssuerSerial"; //$NON-NLS-1$
   public static final String TAG_ALIAS_ATTR = "alias"; //$NON-NLS-1$
   public static final String TAG_DIRECTION_ATTR = "direction"; //$NON-NLS-1$
   public static final String DIRECTION_IN = "in"; //$NON-NLS-1$
   public static final String DIRECTION_OUT = "out"; //$NON-NLS-1$
   public static final String DIRECTION_BOTH = "both"; //$NON-NLS-1$   
   public static final String TAG_ASSERT_SECURITY_AUTH = "Authentication"; //$NON-NLS-1$
   public static final String TAG_ASSERT_AUTH_USERNAMETOKEN = "UsernameToken"; //$NON-NLS-1$
   public static final String TAG_ASSERT_AUTH_PWD_TEXT = "PasswordText"; //$NON-NLS-1$
   public static final String TAG_ASSERT_AUTH_PWD_DIGEST = "PasswordDigest"; //$NON-NLS-1$
   public static final String TAG_ASSERT_AUTH_NONCE = "Nonce"; //$NON-NLS-1$
   public static final String TAG_ASSERT_AUTH_USER = "User"; //$NON-NLS-1$
   public static final String TAG_ASSERT_AUTH_PWD_CLEARTEXT = "CleartextPassword"; //$NON-NLS-1$
   public static final String TAG_ASSERT_AUTH_PREEMPTIVE = "HTTPPreemptive"; //$NON-NLS-1$   
   public static final String TAG_ASSERT_AUTH_PASSWORD = "Password"; //$NON-NLS-1$
   public static final String TAG_ASSERT_TIMESTAMP = "Timestamp"; //$NON-NLS-1$   
   public static final String TAG_ASSERT_SECURITY_ENCRYPT = "EncryptionParts"; //$NON-NLS-1$
   public static final String TAG_ASSERT_SECURITY_SIGN = "SignatureParts"; //$NON-NLS-1$
   public static final String TAG_NAMESPACE_ATTR = "namespace"; //$NON-NLS-1$
   public static final String DIRECTREFERENCE = "DirectReference"; //$NON-NLS-1$
   public static final String CRYPTO_PROPERTIES = "crypto.properties"; //$NON-NLS-1$
   public static final String ADDUTELEMENT = "addUTElement"; //$NON-NLS-1$
   public static final String DEFAULT_USER = "anonymous"; //$NON-NLS-1$
   public static final String X509KEYIDENTIFIER = "X509KeyIdentifier"; //$NON-NLS-1$
   public static final String SECURITY_ACTION = "SECURITY_ACTION"; //$NON-NLS-1$
   public static final String ENCRYPTION_ACTION = "ENCRYPTION_ACTION"; //$NON-NLS-1$
   public static final String SIGNATURE_USER = "SIGNATURE_USER"; //$NON-NLS-1$
   public static final String SIGNATURE_ACTION = "SIGNATURE_ACTION"; //$NON-NLS-1$
   public static final String RECEIVER_ACTION = "RECEIVER_ACTION"; //$NON-NLS-1$
   
   // REST Service Policy
   public static final String TAG_REST_ENABLED = "RESTenabled"; //$NON-NLS-1$
   public static final String TAG_DESCRIPTION_ATTR = "description"; //$NON-NLS-1$
   public static final String TAG_REST_USAGE = "usage"; //$NON-NLS-1$
   
   // HTTP Policy options
   public static final String TAG_HTTP_TRANSPORT_OPTIONS = "HTTPTransportOptions"; //$NON-NLS-1$
   public static final String ATTR_HTTP_SOCKET_TIMEOUT = "httpSocketTimeout"; //$NON-NLS-1$
   public static final String ATTR_HTTP_TCP_NODELAY = "httpTcpNoDelay"; //$NON-NLS-1$
   public static final String ATTR_HTTP_REDIRECT_WITH_GET = "redirectWithGET"; //$NON-NLS-1$
   public static final String ATTR_HTTP_CLIENT_CONNECTION_TIMEOUT = "httpClientConnectionTimeout"; //$NON-NLS-1$
   public static final String ATTR_HTTP_CONNECTION_MANAGER_TIMEOUT = "httpConnectionManagerTimeout"; //$NON-NLS-1$
   public static final String TAG_HTTP_XML_TYPES = "XmlTypes"; //$NON-NLS-1$
   public static final String TAG_HTTP_MIME_TYPE = "MimeType"; //$NON-NLS-1$
   
   // SAML Policy
   public static final String TAG_ASSERT_AUTH_SAML = "SAMLToken"; //$NON-NLS-1$
   public static final String SAML_SUBJECT_NAME = "subjectName"; //$NON-NLS-1$
   public static final String SAML_CONFIRM_METHOD = "confirmationMethod"; //$NON-NLS-1$
   public static final String SAML_CONFIRM_SENDER_VOUCHES = "urn:oasis:names:tc:SAML:1.0:cm:sender-vouches"; //$NON-NLS-1$
   public static final String SAML_CONFIRM_HOLDER_OF_KEY = "urn:oasis:names:tc:SAML:1.0:cm:holder-of-key"; //$NON-NLS-1$
   public static final String SAML_AUTH_METHOD = "authenticationMethod"; //$NON-NLS-1$
   
   // retry policies
   /** Retry policy tag name. */
   public static final String RETRY_POLICY_TAG = "retry"; //$NON-NLS-1$
   /** Retry attempts attr name. */
   public static final String RETRY_ATTEMPTS_ATTR = "attempts"; //$NON-NLS-1$
   /** Retry interval attr name. */
   public static final String RETRY_INTERVAL_ATTR = "interval"; //$NON-NLS-1$
   /** Retry service name attr name. */
   public static final String PROCESS_SERVICE_NAME_ATTR = "service"; //$NON-NLS-1$
   /** Retry yes or no element returned from service. */
   public static final String RETRY_TAG = "retry"; //$NON-NLS-1$
   /** Retry interval element returned from service. */
   public static final String INTERVAL_TAG = "interval"; //$NON-NLS-1$
   /** Retry fault list attr name. */
   public static final String FAULT_LIST_ATTR = "faultList"; //$NON-NLS-1$
   /** Retry fault exclusion list attr name. */
   public static final String FAULT_EXCLUSION_LIST_ATTR = "faultExclusionList"; //$NON-NLS-1$
   /** Retry behavior after attempts are exhausted. */
   public static final String RETRY_ON_FAILURE_ATTR = "onFailure"; //$NON-NLS-1$
   /** Activity faults after all attempts fail */
   public static final String RETRY_FAULT_ON_FAILURE = "fault"; //$NON-NLS-1$
   /** Process is suspended after all attempts fail */
   public static final String RETRY_SUSPEND_ON_FAILURE = "suspend"; //$NON-NLS-1$

   /** Wild card for qname tests. */
   public static final String QNAME_WILDCARD = "*"; //$NON-NLS-1$

   /** ActiveBPEL retry check document namespace. */
   public static final String ABPEL_RETRY_CHECK_NS = "http://www.activebpel.org/services/retry"; //$NON-NLS-1$
   /** Retry check input element tag name. */
   public static final String RETRY_CHECK_INPUT_TAG = "retryCheckInput"; //$NON-NLS-1$
   /** Retry check input processId element tag name. */
   public static final String RETRY_CHECK_PROCESS_ID_TAG = "retryCheckInput"; //$NON-NLS-1$
   /** Retry check output part name. */
   public static final String RETRY_CHECK_OUTPUT_PART = "output"; //$NON-NLS-1$

   /**
    * Suspend process on invoke recovery constants.
    */
   public static final String TAG_INVOKE_RECOVERY = "InvokeRecovery"; //$NON-NLS-1$
   public static final String ATTR_SUSPEND_PROCESS = "suspendProcess"; //$NON-NLS-1$
   
   /**
    * JMS Transport constants.
    */
   public static final String TAG_JMS_DELIVERY = "JMSDeliveryOptions"; //$NON-NLS-1$
   public static final String ATTR_JMS_MESSAGE_TYPE = "jmsMessageType"; //$NON-NLS-1$
   public static final String JMS_TEXT_MESSAGE = "text"; //$NON-NLS-1$
   public static final String JMS_BYTES_MESSAGE = "bytes"; //$NON-NLS-1$
   public static final String ATTR_JMS_MESSAGE_FORMAT = "jmsMessageFormat"; //$NON-NLS-1$
   public static final String JMS_SOAP_FORMAT = "soap"; //$NON-NLS-1$
   public static final String JMS_XML_FORMAT = "xml"; //$NON-NLS-1$
   public static final String ATTR_JMS_EXPIRATION = "jmsExpiration"; //$NON-NLS-1$
   public static final String ATTR_JMS_PRIORITY = "jmsPriority"; //$NON-NLS-1$
   public static final String ATTR_JMS_CORRELATION_ID = "jmsCorrelationID"; //$NON-NLS-1$
   public static final String ATTR_JMS_MANAGER_ID = "jmsManagerID"; //$NON-NLS-1$
   
}
