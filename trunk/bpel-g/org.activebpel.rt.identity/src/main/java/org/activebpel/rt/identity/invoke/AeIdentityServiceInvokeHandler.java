//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/invoke/AeIdentityServiceInvokeHandler.java,v 1.12 2008/02/17 21:54:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.invoke;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.AeFaultFactory;
import org.activebpel.rt.bpel.impl.activity.support.AeFault;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.wsio.invoke.AeWSIInvokeHandlerBase;
import org.activebpel.rt.identity.AeIdentityException;
import org.activebpel.rt.identity.IAeIdentity;
import org.activebpel.rt.identity.IAeIdentityRole;
import org.activebpel.rt.identity.IAeIdentityServiceConstants;
import org.activebpel.rt.identity.IAeIdentityServiceManager;
import org.activebpel.rt.identity.invoke.io.AeAssertionQueryResponseSerializer;
import org.activebpel.rt.identity.invoke.io.AeIdentityListSerializer;
import org.activebpel.rt.identity.invoke.io.AeIdentityResultSetSerializer;
import org.activebpel.rt.identity.invoke.io.AeIdentityServiceFaultSerializer;
import org.activebpel.rt.identity.invoke.io.AeIdentityServiceMessageDeserializer;
import org.activebpel.rt.identity.invoke.io.AeIdentityServiceMessageSerializerBase;
import org.activebpel.rt.identity.invoke.io.AeIdentityServiceRoleListSerializer;
import org.activebpel.rt.identity.search.AeAssertionQueryResponse;
import org.activebpel.rt.identity.search.AeIdentityQuery;
import org.activebpel.rt.identity.search.AeIdentityResultSet;
import org.activebpel.rt.identity.search.IAeIdentitySearch;
import org.activebpel.rt.message.AeMessageData;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.wsio.AeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceMessageData;
import org.activebpel.wsio.invoke.AeInvokeResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AeIdentityServiceInvokeHandler extends AeWSIInvokeHandlerBase
{
   /** The findRolesByPrincipal message QName. */
   private static final QName FIND_ROLES_BY_PRINCIPAL_MESSAGE = new QName(IAeIdentityServiceConstants.IDENTITY_SERVICE_NS, "principalNameInput"); //$NON-NLS-1$
   /** The findIdentitiesByRole message QName. */
   private static final QName FIND_IDENTITIES_BY_ROLE_MESSAGE = new QName(IAeIdentityServiceConstants.IDENTITY_SERVICE_NS, "roleNameInput"); //$NON-NLS-1$
   /** The findIdentities message QName. */
   private static final QName FIND_IDENTITIES_MESSAGE = new QName(IAeIdentityServiceConstants.IDENTITY_SERVICE_NS, "identityQueryInput"); //$NON-NLS-1$
   /** The assertPrincipalInQueryResult message QName. */
   private static final QName ASSERT_PRINCIPAL_MESSAGE = new QName(IAeIdentityServiceConstants.IDENTITY_SERVICE_NS, "assertionInput"); //$NON-NLS-1$
   /** The empty message QName. */
   private static final QName EMPTY_MESSAGE = new QName(IAeIdentityServiceConstants.IDENTITY_SERVICE_NS, "emptyMessage"); //$NON-NLS-1$
   /** The fault message QName. */
   private static final QName FAULT_MESSAGE = new QName(IAeIdentityServiceConstants.IDENTITY_SERVICE_NS, "searchFault"); //$NON-NLS-1$
   /** The identity list output message QName. */
   private static final QName IDENTITY_LIST_OUTPUT_MESSAGE = new QName(IAeIdentityServiceConstants.IDENTITY_SERVICE_NS, "identityListOutput"); //$NON-NLS-1$
   /** The role list output message QName. */
   private static final QName ROLE_LIST_OUTPUT_MESSAGE = new QName(IAeIdentityServiceConstants.IDENTITY_SERVICE_NS, "roleListOutput"); //$NON-NLS-1$
   /** The role list output message QName. */
   private static final QName IDENTITY_QUERY_OUTPUT_MESSAGE = new QName(IAeIdentityServiceConstants.IDENTITY_SERVICE_NS, "identityQueryOutput"); //$NON-NLS-1$
   /** The assertPrincipalInQueryResult message QName. */
   private static final QName ASSERTION_INPUT_WITH_RESPONSE = new QName(IAeIdentityServiceConstants.IDENTITY_SERVICE_NS, "assertionInputWithResponse"); //$NON-NLS-1$
   /** The assertionQueryResponse message QName. */
   private static final QName IDENTITY_QUERY_ASSERTION_RESPONSE_MESSAGE = new QName(IAeIdentityServiceConstants.IDENTITY_SERVICE_NS, "assertionQueryResponse"); //$NON-NLS-1$
   /** The findIdentities message QName. */
   private static final QName COUNT_IDENTITIES_MESSAGE = new QName(IAeIdentityServiceConstants.IDENTITY_SERVICE_NS, "countIdentitiesInput"); //$NON-NLS-1$
   /** The identity count output message QName. */
   private static final QName IDENTITY_COUNT_OUTPUT_MESSAGE = new QName(IAeIdentityServiceConstants.IDENTITY_SERVICE_NS, "countIdentitiesOutput"); //$NON-NLS-1$
   

   /**
    * @see org.activebpel.rt.bpel.wsio.invoke.AeWSIInvokeHandlerBase#mapThrowableAsFault(org.activebpel.wsio.invoke.AeInvokeResponse, java.lang.Throwable)
    */
   protected IAeFault mapThrowableAsFault(AeInvokeResponse aResponse, Throwable aThrowable)
   {
      IAeFault fault = null;
      if (aThrowable instanceof AeIdentityException)
      {
         try
         {
            AeIdentityException ie = (AeIdentityException) aThrowable;
            Document doc = (new AeIdentityServiceFaultSerializer(ie)).serialize().getOwnerDocument();
            Map data = new HashMap();
            data.put("fault",doc); //$NON-NLS-1$
            AeMessageData faultData = new AeMessageData(FAULT_MESSAGE, data);
            fault = new AeFault(FAULT_MESSAGE, faultData);
         }
         catch(Exception e)
         {
            // catch errors during serialization of exception to a fault.
            AeException.logError(e);
            fault = AeFaultFactory.getSystemErrorFault(aThrowable);
         }
      }
      else
      {
         fault = super.mapThrowableAsFault(aResponse, aThrowable);
      }

      return fault;
   }

   /**
    * Creates a generic deserializer to access  message part data.
    * @param aMessageData
    * @param aMessagePartName
    * @throws AeIdentityException
    */
   protected AeIdentityServiceMessageDeserializer createDeserializer(IAeWebServiceMessageData aMessageData, String aMessagePartName)
      throws AeIdentityException
   {
      try
      {
         Document doc = extractMessagePartDocument(aMessageData,aMessagePartName);
         AeIdentityServiceMessageDeserializer des = new AeIdentityServiceMessageDeserializer(doc);
         return des;
      }
      catch(Exception e)
      {
         throw new AeIdentityException(e);
      }
   }

   /**
    * Convenience method to serialize and set the output data in the response.
    * @param aResponse wsio response
    * @param aOutputMessageType output message type qname
    * @param aPartName message data part
    * @param aSerializer serializer.
    * @throws Exception Errors due to serialization.
    */
   protected void setResponseData(AeInvokeResponse aResponse, QName aOutputMessageType, String aPartName,
            AeIdentityServiceMessageSerializerBase aSerializer) throws Exception
   {
      setResponseData(aResponse, aOutputMessageType, aPartName, aSerializer.serialize().getOwnerDocument());
   }

   /**
    * Implements the 'findRolesByPrincipal' operation.
    * @param aMessageData
    * @param aResponse
    * @throws Exception
    */
   public void findRolesByPrincipal(IAeWebServiceMessageData aMessageData, AeInvokeResponse aResponse) throws Exception
   {
      compareExpectedMessageType(aMessageData, FIND_ROLES_BY_PRINCIPAL_MESSAGE, "findRolesByPrincipal"); //$NON-NLS-1$
      // get the input data
      AeIdentityServiceMessageDeserializer des = createDeserializer(aMessageData, "principalName"); //$NON-NLS-1$
      String principalName = des.getPrincipalName();
      // run search
      IAeIdentitySearch searcher = getSearcher();
      IAeIdentityRole roles[] = searcher.findRolesByPrincipal(principalName);
      // serialize response data.
      AeIdentityServiceRoleListSerializer ser = new AeIdentityServiceRoleListSerializer(roles);
      setResponseData(aResponse, ROLE_LIST_OUTPUT_MESSAGE, "roles", ser); //$NON-NLS-1$
   }

   /**
    * Implements the 'findRoles' operation.
    * @param aMessageData
    * @param aResponse
    * @throws Exception
    */
   public void findRoles(IAeWebServiceMessageData aMessageData, AeInvokeResponse aResponse) throws Exception
   {
      // run search
      IAeIdentitySearch searcher = getSearcher();
      IAeIdentityRole roles[] = searcher.findRoles();
      // serialize response data.
      AeIdentityServiceRoleListSerializer ser = new AeIdentityServiceRoleListSerializer(roles);
      setResponseData(aResponse, ROLE_LIST_OUTPUT_MESSAGE, "roles", ser); //$NON-NLS-1$
   }

   /**
    * Implements the 'findIdentitiesByRole' operation.
    * @param aMessageData
    * @param aResponse
    * @throws Exception
    */
   public void findIdentitiesByRole(IAeWebServiceMessageData aMessageData, AeInvokeResponse aResponse) throws Exception
   {
      compareExpectedMessageType(aMessageData, FIND_IDENTITIES_BY_ROLE_MESSAGE, "findIdentitiesByRole"); //$NON-NLS-1$
      // get the input data
      AeIdentityServiceMessageDeserializer des = createDeserializer(aMessageData, "roleName"); //$NON-NLS-1$
      String roleName = des.getRoleName();
      IAeIdentitySearch searcher = getSearcher();
      IAeIdentity identities[] = searcher.findIdentitiesByRole(roleName);
      AeIdentityListSerializer ser = new AeIdentityListSerializer(identities);
      setResponseData(aResponse, IDENTITY_LIST_OUTPUT_MESSAGE, "identities", ser); //$NON-NLS-1$
   }

   /**
    * Implements the findIdentities wsdl operation.
    * @param aMessageData
    * @param aResponse
    * @throws Exception
    */
   public void findIdentities(IAeWebServiceMessageData aMessageData, AeInvokeResponse aResponse) throws Exception
   {
      compareExpectedMessageType(aMessageData, FIND_IDENTITIES_MESSAGE, "findIdentities"); //$NON-NLS-1$
      AeIdentityServiceMessageDeserializer des = createDeserializer(aMessageData, "query"); //$NON-NLS-1$
      AeIdentityQuery query = des.getQuery();
      IAeIdentitySearch searcher = getSearcher();
      AeIdentityResultSet resultSet = searcher.findIdentities(query);
      AeIdentityResultSetSerializer ser = new AeIdentityResultSetSerializer(resultSet);
      setResponseData(aResponse, IDENTITY_QUERY_OUTPUT_MESSAGE, "resultSet", ser); //$NON-NLS-1$
   }

   /**
    * Implements the findIdentities wsdl operation.
    * @param aMessageData
    * @param aResponse
    * @throws Exception
    */
   public void countIdentities(IAeWebServiceMessageData aMessageData, AeInvokeResponse aResponse) throws Exception
   {
      compareExpectedMessageType(aMessageData, COUNT_IDENTITIES_MESSAGE, "countIdentities"); //$NON-NLS-1$
      AeIdentityServiceMessageDeserializer des = createDeserializer(aMessageData, "countIdentitiesRequest"); //$NON-NLS-1$
      AeIdentityQuery query = des.getQuery();
      IAeIdentitySearch searcher = getSearcher();
      // fixme (MF-identity) fixup base class to do something better than NoSuchMethodError when something not found.
      // fixme (MF-identity) come back and optimize this with a specialized query to return just the count.
      AeIdentityResultSet resultSet = searcher.findIdentities(query);
      Document doc = AeXmlUtil.newDocument();
      Element count = AeXmlUtil.addElementNS(doc, IAeIdentityServiceConstants.IDENTITY_SERVICE_NS, "aeid:identitiesCount", String.valueOf(resultSet.getTotalMatched())); //$NON-NLS-1$
      count.setAttributeNS(IAeConstants.W3C_XMLNS,"xmlns:aeid", IAeIdentityServiceConstants.IDENTITY_SERVICE_NS ); //$NON-NLS-1$
      setResponseData(aResponse, IDENTITY_COUNT_OUTPUT_MESSAGE, "count", doc); //$NON-NLS-1$
   }

   /**
    * Implements the assertPrincipalInQueryResult wsdl operation.
    * @param aMessageData
    * @param aResponse
    * @throws Exception
    */
   public void assertPrincipalInQueryResult(IAeWebServiceMessageData aMessageData, AeInvokeResponse aResponse) throws Exception
   {
      compareExpectedMessageType(aMessageData, ASSERT_PRINCIPAL_MESSAGE, "assertPrincipalInQueryResult"); //$NON-NLS-1$
      AeIdentityServiceMessageDeserializer des = createDeserializer(aMessageData, "assertion"); //$NON-NLS-1$
      String principalName = des.getPrincipalName();
      List queries = des.getQueries();
      IAeIdentitySearch searcher = getSearcher();
      searcher.assertPrincipalInQueryResult(principalName, queries);
      AeWebServiceMessageData respMsgData = createWebServiceMessageData(EMPTY_MESSAGE);
      aResponse.setMessageData(respMsgData);
   }

   /**
    * Implements the assertPrincipalInQueryResult wsdl operation.
    * @param aMessageData
    * @param aResponse
    * @throws Exception
    */
   public void assertPrincipalInQueryResultWithResponse(IAeWebServiceMessageData aMessageData, AeInvokeResponse aResponse) throws Exception
   {
      compareExpectedMessageType(aMessageData, ASSERTION_INPUT_WITH_RESPONSE, "assertPrincipalInQueryResultWithResponse"); //$NON-NLS-1$
      AeIdentityServiceMessageDeserializer des = createDeserializer(aMessageData, "principalQueryAssertionWithResponse"); //$NON-NLS-1$
      String principalName = des.getPrincipalName();
      List queries = des.getQueries();
      IAeIdentitySearch searcher = getSearcher();
      AeAssertionQueryResponse response = searcher.assertPrincipalInQueryResultWithResponse(principalName, queries);
      AeAssertionQueryResponseSerializer ser = new AeAssertionQueryResponseSerializer(response);
      setResponseData(aResponse, IDENTITY_QUERY_ASSERTION_RESPONSE_MESSAGE, "assertionQueryResponse", ser); //$NON-NLS-1$
   }

   /**
    * Convenience method the return searcher.
    * @throws AeIdentityException
    */
   protected IAeIdentitySearch getSearcher() throws AeIdentityException
   {
      return getManager().getIdentitySearch();
   }

   /**
    * Convenience method the return identity service manager.
    * @return identity service manager.
    */
   protected IAeIdentityServiceManager getManager()
   {
      IAeIdentityServiceManager mgr = (IAeIdentityServiceManager) AeEngineFactory.getEngine().getCustomManager(IAeIdentityServiceConstants.MANAGER_NAME);
      return mgr;
   }
}
