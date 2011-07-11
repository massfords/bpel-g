// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/deploy/AeAxisBase.java,v 1.6 2005/10/17 20:02:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.deploy;

import java.io.StringReader;

import org.activebpel.rt.AeException;
import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentException;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *  Base class for handling Axis web service deployments.
 */
abstract public class AeAxisBase
{
   
   //---------------------------------------------------------------------------
   //          AXIS DEPLOYMENT METHODS
   //---------------------------------------------------------------------------
   
   /**
    * Generic Axis deployment method.
    * @param aWsddDoc The WSDD deployment document.
    * @param aLoader Class loader for deploying web services.
    * @throws AeException
    */
   public void deployToWebServiceContainer(Document aWsddDoc, ClassLoader aLoader)
      throws AeException
   {
      try
      {
         Document wsddDoc = axisHack(aWsddDoc);
         Document axisDeployDoc = createDeploymentDocument( wsddDoc );
         Document adminDeployDoc = createDeploymentDocument( wsddDoc );
         deployToAxis( aLoader, axisDeployDoc, adminDeployDoc );      
      }
      catch (Throwable e)
      {
         AeException.logError( e, AeMessages.getString("AeAxisBase.ERROR_0") + e.getLocalizedMessage() ); //$NON-NLS-1$
         throw new AeException( AeMessages.getString("AeAxisBase.ERROR_0"), e ); //$NON-NLS-1$
      }
   }
   
   /**
    * Create deployment wsdd doc.
    * @param aWsddDoc
    * @throws Exception
    */
   public static Document createDeploymentDocument( Document aWsddDoc )
   throws Exception
   {
      return createAxisDocument( aWsddDoc, "deployment" ); //$NON-NLS-1$
   }
   
   /**
    * Deploy web service to Axis Server.
    * @param aLoader Axis web services class loader.
    * @param aAxisDoc Axis wsdd doc.
    * @param aAdminDoc Axis admin wsdd doc.
    * @throws Exception
    */
   abstract protected void deployToAxis( ClassLoader aLoader, 
                                          Document aAxisDoc, Document aAdminDoc )
   throws Exception;
   
   
   
   //---------------------------------------------------------------------------
   //          AXIS UNDEPLOYMENT METHODS
   //---------------------------------------------------------------------------


   /**
    * Generic removal of Axis web services.
    * @param aWsddDoc The wsdd document.
    * @throws AeException
    */
   public void undeployFromWebServiceContainer(Document aWsddDoc)
      throws AeException
   {
      try
      {
         Document wsddDoc = axisHack(aWsddDoc);
         Document axisDeployDoc = createUndeploymentDocument( wsddDoc );
         Document adminDeployDoc = createUndeploymentDocument( wsddDoc );
            
         undeployFromAxis( axisDeployDoc, adminDeployDoc );      
      }
      catch (Exception e)
      {
         AeException.logError( e, AeMessages.getString("AeAxisBase.ERROR_1") ); //$NON-NLS-1$
         throw new AeException( AeMessages.getString("AeAxisBase.ERROR_1"), e ); //$NON-NLS-1$
      }
   }

   /**
    * Remove web services from Axis server.
    * @param aAxisDoc
    * @param aAdminDoc
    * @throws Exception
    */
   protected abstract void undeployFromAxis( Document aAxisDoc, Document aAdminDoc )
   throws Exception;
   
   /**
    * Create undeployment wsdd doc.
    * @param aWsddDoc
    * @throws Exception
    */
   public static Document createUndeploymentDocument( Document aWsddDoc )
   throws Exception
   {
      return createAxisDocument( aWsddDoc, "undeployment" ); //$NON-NLS-1$
   }

   //---------------------------------------------------------------------------
   //    AXIS UTILITY METHODS
   //---------------------------------------------------------------------------

   /**
    * Utility method for creating Axis deployment/undeployment doms.
    * @param aWsddDoc
    * @param aElementName Either deployment or undeployment.
    * @throws Exception
    */
   protected static Document createAxisDocument( Document aWsddDoc, String aElementName )
   throws Exception
   {
      // basically builds two additional docs from our generated wsdd uses them
      // to register services with Axis server
      Element root = aWsddDoc.getDocumentElement();
   
      // the deployment command document
      Document deployDoc = XMLUtils.newDocument();
   
      // create command
      Element deploy = deployDoc.createElementNS(root.getNamespaceURI(), aElementName );
   
      NamedNodeMap attributes = root.getAttributes();
      for (int count = 0; count < attributes.getLength(); count++)
      {
         Attr attribute = (Attr) attributes.item(count);
         deploy.setAttributeNodeNS((Attr) deployDoc.importNode(attribute, true));
      }
   
      NodeList services = root.getElementsByTagNameNS(root.getNamespaceURI(), "service"); //$NON-NLS-1$
      for (int i = 0; i < services.getLength(); i++)
      {
         Element serviceElement = (Element) services.item(i);
         deploy.appendChild(deployDoc.importNode(serviceElement, true));
      }
   
      // insert command into document
      deployDoc.appendChild(deploy);
   
      return deployDoc;      
   }
   
   /**
    * Axis was choking on our dom object - but serializing/deserializing
    * solved for now.
    * @param aDoc
    * @throws AeDeploymentException
    */
   protected Document axisHack( Document aDoc ) throws AeDeploymentException
   {
      try
      {
         String ser = AeXMLParserBase.documentToString( aDoc );
         InputSource in = new InputSource(new StringReader( ser ));
         return XMLUtils.newDocument(in);
      }
      catch (Exception e)
      {
         throw new AeDeploymentException(AeMessages.getString("AeAxisBase.ERROR_7"), e); //$NON-NLS-1$
      } 
   }
}
