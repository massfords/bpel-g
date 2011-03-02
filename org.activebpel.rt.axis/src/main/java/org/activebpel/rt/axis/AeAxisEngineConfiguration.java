// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/AeAxisEngineConfiguration.java,v 1.1 2007/08/02 19:51:36 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUTF8Util;
import org.activebpel.rt.util.AeUtil;
import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.Handler;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.xml.sax.SAXException;

/**
 * This is a reusable implementation of the WSDDEngineConfiguration object.  This object
 * can be instantiated and then used by multiple AeService objects without reloading the
 * deployment from disk.
 */
public class AeAxisEngineConfiguration implements WSDDEngineConfiguration
{
   /** A byte array containing the contents of the client-config.wsdd file (performance and loading tweak). */
   private static byte [] sConfig = null;

   /* the default axis confiuration file name */
   private static final String DEFAULT_AXIS_CLIENT_CONFIG = "ae-client-config.wsdd"; //$NON-NLS-1$
   
   /** The cached WSDD deployment. */
   private WSDDDeployment mDeployment = null;

   /**
    * Creates an axis engine configuration object.
    */
   public AeAxisEngineConfiguration()
   {
      init();
   }

   /**
    * Initializes the configuration object.
    */
   public void init()
   {
      InputStream is = null;
      try
      {
         is = new ByteArrayInputStream(getConfig());
         WSDDDocument doc = new WSDDDocument(XMLUtils.newDocument(is));
         mDeployment = doc.getDeployment();
      }
      catch (WSDDException e)
      {
         mDeployment = null;
      }
      catch (ParserConfigurationException pce)
      {
         mDeployment = null;
      }
      catch (SAXException se)
      {
         mDeployment = null;
      }
      catch (IOException se)
      {
         mDeployment = null;
      }
      finally
      {
         AeCloser.close(is);
      }

   }

   /**
    * @see org.apache.axis.WSDDEngineConfiguration#getDeployment()
    */
   public WSDDDeployment getDeployment()
   {
      return mDeployment;
   }

   /**
    * User to override the default client handler chain  
    * 
    * @param aWsdd WSDD document to load 
    */
   public void setGlobalConfig(String aWsdd) throws ConfigurationException
   {
      InputStream is = null;
      try {
         is = AeUTF8Util.getInputStream(aWsdd);
         WSDDDocument doc = new WSDDDocument(XMLUtils.newDocument(is));
         mDeployment.setGlobalConfiguration(doc.getDeployment().getGlobalConfiguration());
         
      } catch (Exception e) {
         throw new ConfigurationException(e);
      }
      finally {
         AeCloser.close(is);
      }
   }
   
   
   /**
    * @see org.apache.axis.EngineConfiguration#configureEngine(org.apache.axis.AxisEngine)
    */
   public void configureEngine(AxisEngine engine) throws ConfigurationException
   {
      try
      {
         mDeployment.configureEngine(engine);
         engine.refreshGlobalOptions();
      }
      catch (Exception e)
      {
         throw new ConfigurationException(e);
      }
   }

   /**
    * @see org.apache.axis.EngineConfiguration#writeEngineConfig(org.apache.axis.AxisEngine)
    */
   public void writeEngineConfig(AxisEngine engine) throws ConfigurationException
   {
      // Don't allow this.
   }

   /**
    * @see org.apache.axis.EngineConfiguration#getHandler(javax.xml.namespace.QName)
    */
   public Handler getHandler(QName qname) throws ConfigurationException
   {
      return mDeployment.getHandler(qname);
   }

   /**
    * @see org.apache.axis.EngineConfiguration#getService(javax.xml.namespace.QName)
    */
   public SOAPService getService(QName qname) throws ConfigurationException
   {
      SOAPService service = mDeployment.getService(qname);
      if (service == null)
      {
         throw new ConfigurationException(Messages.getMessage("noService10", qname.toString())); //$NON-NLS-1$
      }
      return service;
   }

   /**
    * @see org.apache.axis.EngineConfiguration#getServiceByNamespaceURI(java.lang.String)
    */
   public SOAPService getServiceByNamespaceURI(String namespace) throws ConfigurationException
   {
      return mDeployment.getServiceByNamespaceURI(namespace);
   }

   /**
    * @see org.apache.axis.EngineConfiguration#getTransport(javax.xml.namespace.QName)
    */
   public Handler getTransport(QName qname) throws ConfigurationException
   {
      return mDeployment.getTransport(qname);
   }

   /**
    * @see org.apache.axis.EngineConfiguration#getTypeMappingRegistry()
    */
   public TypeMappingRegistry getTypeMappingRegistry() throws ConfigurationException
   {
      return mDeployment.getTypeMappingRegistry();
   }

   /**
    * @see org.apache.axis.EngineConfiguration#getGlobalRequest()
    */
   public Handler getGlobalRequest() throws ConfigurationException
   {
      return mDeployment.getGlobalRequest();
   }

   /**
    * @see org.apache.axis.EngineConfiguration#getGlobalResponse()
    */
   public Handler getGlobalResponse() throws ConfigurationException
   {
      return mDeployment.getGlobalResponse();
   }

   /**
    * @see org.apache.axis.EngineConfiguration#getGlobalOptions()
    */
   public Hashtable getGlobalOptions() throws ConfigurationException
   {
      WSDDGlobalConfiguration globalConfig = mDeployment.getGlobalConfiguration();

      if (globalConfig != null)
         return globalConfig.getParametersTable();

      return null;
   }

   /**
    * @see org.apache.axis.EngineConfiguration#getDeployedServices()
    */
   public Iterator getDeployedServices() throws ConfigurationException
   {
      return mDeployment.getDeployedServices();
   }

   /**
    * Implements method by returning the associated deployments roles.
    * @see org.apache.axis.EngineConfiguration#getRoles()
    */
   public List getRoles()
   {
      return mDeployment.getRoles();
   }
   
   /**
    * @return Returns the engineConfig.
    */
   protected static byte[] getConfig()
   {
      // Just in case the loadCOnfig wasn't called.  Should never happen.
      if(sConfig == null)
         loadConfig(DEFAULT_AXIS_CLIENT_CONFIG);
      return sConfig;
   }
   
   /**
    * User to force the loading of the engine config while we have the 
    * right thread context.
    * @param aConfigLoc The path of the configguration file to load. 
    */
   public static void loadConfig(String aConfigLoc)
   {
      InputStream is = null;
      try
      {
         URL url = AeUtil.findOnClasspath(aConfigLoc, AeAxisEngineConfiguration.class);
         is = url.openStream();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte [] buff = new byte[4096];
         int count = 0;
         while ( (count = is.read(buff)) != -1 )
         {
            baos.write(buff, 0, count);
         }
         sConfig = baos.toByteArray();
      }
      catch (Exception e)
      {
         AeException.logError(e, AeMessages.getString("AeAxisEngineConfiguration.ERROR_2")); //$NON-NLS-1$
      }
      finally
      {
         AeCloser.close(is);
      }
   }
   
}
