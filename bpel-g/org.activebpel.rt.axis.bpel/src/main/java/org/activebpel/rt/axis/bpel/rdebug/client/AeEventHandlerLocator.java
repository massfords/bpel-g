// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/rdebug/client/AeEventHandlerLocator.java,v 1.11 2007/09/14 20:09:23 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.rdebug.client;

import java.net.URL;
import java.rmi.Remote;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.activebpel.rt.axis.AeAxisEngineConfiguration;
import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.axis.bpel.admin.client.AeActiveBpelEventHandlerStub;
import org.activebpel.rt.axis.bpel.admin.client.AeActiveBpelEventHandlerStubAdapter;
import org.activebpel.rt.axis.bpel.eventhandler.IAeActiveBpelEventHandler;
import org.activebpel.rt.bpel.server.admin.rdebug.client.IAeEventHandler;
import org.activebpel.rt.bpel.server.admin.rdebug.client.IAeEventHandlerConstants;
import org.activebpel.rt.bpel.server.admin.rdebug.client.IAeEventHandlerService;
import org.apache.axis.AxisFault;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Service;
import org.apache.axis.client.Stub;

/**
 * Class used to locate the event handler service routine for BPEL engine events. 
 */
public class AeEventHandlerLocator extends Service implements IAeEventHandlerService
{
   /** Cache the axis engine configuration, which is lazily instantiated. */
   private EngineConfiguration mAxisEngineConfig;

   /** The URL of the server which provides us with administrative functions */
   private String mServerURL;
   
   /** The name of the event handler service */
   private QName mServiceName;

   /** The ports in use by the locator */
   private HashSet mPorts;

   /**
    * Constructor for the remote debug locator which takes as input the 
    * server connect information of server URL, username and password.
    * @param aServerURL the URL of the server to connect to
    */
   public AeEventHandlerLocator(String aServerURL)
   {
      mServerURL = aServerURL;

      // Setup the service name we will be using. Right now we support RPC and Doc/Literal but this will change in next release
      if (aServerURL.endsWith(IAeEventHandlerConstants.RPC_EVENT_HANDLER_SERVICE))
         mServiceName = new QName(IAeEventHandlerConstants.RPC_EVENT_HANDLER_NS, IAeEventHandlerConstants.RPC_EVENT_HANDLER_SERVICE);
      else
         mServiceName = new QName(IAeEventHandlerConstants.EVENT_HANDLER_NS, IAeEventHandlerConstants.EVENT_HANDLER_SERVICE);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.client.IAeEventHandlerService#getRemoteDebugAddress()
    */
   public String getRemoteDebugAddress()
   {
      return mServerURL;
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.client.IAeEventHandlerService#getRemoteDebugService()
    */
   public IAeEventHandler getRemoteDebugService() throws ServiceException
   {
      try
      {
         IAeEventHandler eventHandler;
         if (IAeEventHandlerConstants.EVENT_HANDLER_SERVICE.equals(getServiceName().getLocalPart()))
         {
            Stub stub = new AeActiveBpelEventHandlerStub(new URL(getRemoteDebugAddress()), this);
            stub.setPortName(getServiceName().getLocalPart());
            
            eventHandler = new AeActiveBpelEventHandlerStubAdapter((IAeActiveBpelEventHandler)stub);
         }
         else
         {
            Stub stub = new AeEventHandlerStub(new URL(getRemoteDebugAddress()), this);
            stub.setPortName(getServiceName().getLocalPart());
            
            eventHandler = (IAeEventHandler)stub;
         }
         
         return eventHandler;
      }
      catch (AxisFault e)
      {
         return null;
      }
      catch (Exception e)
      {
         throw new ServiceException(e);
      }
   }

   /**
    * @see javax.xml.rpc.Service#getPort(java.lang.Class)
    */
   public Remote getPort(Class aServiceEndpointInterface) throws ServiceException
   {
      try
      {
         if (IAeEventHandler.class.isAssignableFrom(aServiceEndpointInterface))
         {
            AeEventHandlerStub stub = new AeEventHandlerStub(new URL(getRemoteDebugAddress()), this);
            stub.setPortName(getServiceName().getLocalPart());
            return stub;
         }
      }
      catch (Throwable t)
      {
         throw new ServiceException(t);
      }
      throw new ServiceException(AeMessages.getString("AeEventHandlerLocator.ERROR_1") //$NON-NLS-1$
            + (aServiceEndpointInterface == null ? "null" : aServiceEndpointInterface.getName())); //$NON-NLS-1$
   }

   /**
    * @see javax.xml.rpc.Service#getPort(javax.xml.namespace.QName, java.lang.Class)
    */
   public Remote getPort(QName aPortName, Class aServiceEndpointInterface) throws ServiceException
   {
      if (aPortName == null)
         return getPort(aServiceEndpointInterface);

      String inputPortName = aPortName.getLocalPart();
      if (getServiceName().getLocalPart().equals(inputPortName))
      {
         return getRemoteDebugService();
      }
      else
      {
         Remote stub = getPort(aServiceEndpointInterface);
         ((Stub) stub).setPortName(aPortName);
         return stub;
      }
   }

   /**
    * @see javax.xml.rpc.Service#getServiceName()
    */
   public QName getServiceName()
   {
      return mServiceName;
   }

   /**
    * @see javax.xml.rpc.Service#getPorts()
    */
   public Iterator getPorts()
   {
      if (mPorts == null)
      {
         mPorts = new HashSet();
         mPorts.add(new QName(getServiceName().getLocalPart()));
      }
      return mPorts.iterator();
   }
   
   /**
    * Overrides method to return the ActiveBPEL client engine configuration. 
    * @see org.apache.axis.client.Service#getEngineConfiguration()
    */
   protected EngineConfiguration getEngineConfiguration()
   {
      if(mAxisEngineConfig == null)
         mAxisEngineConfig = new AeAxisEngineConfiguration();
      return mAxisEngineConfig;
   }
}