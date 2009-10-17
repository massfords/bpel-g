//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeDeploymentUtil.java,v 1.4 2008/02/02 19:23:26 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy; 

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.scanner.AeDeploymentFileInfo;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.logging.AeStructuredDeploymentLog;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeFileUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;

/**
 * Utility for deploying bpr's from an InputStream 
 */
public class AeDeploymentUtil
{
   /**
    * Load the engine admin web services from our classpath.
    */
   public static void deployWebService(String aWsrName, URL aWsrURL)
   {
      try
      {
         // This is a workaround for weblogic 8.1 where the getResource call on the classloader (that pointed to wsrURL) 
         // was coming back null. The fix was to make a local copy (in the staging dir) and work from it.
         URL copyUrl = copyWsrToStagingDir(aWsrURL, aWsrName);
         
         // load the engine admin wsr directly (no need to unpack it)
         ClassLoader current = Thread.currentThread().getContextClassLoader();
         ClassLoader deploymentCL = new URLClassLoader( new URL[] {copyUrl}, current );
         URL serviceWsddUrl = deploymentCL.getResource( "META-INF/service.wsdd" ); //$NON-NLS-1$

         AeXMLParserBase parser = new AeXMLParserBase();
         parser.setValidating( false );
         Document wsddDom = parser.loadDocument( serviceWsddUrl.openStream(), null );
         
         IAeWebServicesDeployer deployer = AeEngineFactory.getDeploymentHandlerFactory().getWebServicesDeployer();
         AeDeploymentContainer container = new AeDeploymentContainer(null, null, null);
         container.setWsddData(wsddDom);
         deployer.deployToWebServiceContainer( container, deploymentCL );
      }
      catch( Exception e )
      {
         AeException.logError(e, AeMessages.format("AeDeploymentUtil.ERROR_DEPLOYING", aWsrName)); //$NON-NLS-1$
      }
   }
   
   /**
    * Copies the given WSR to the staging dir.
    * @param aWsr The URL of the WSR top be copied
    * @param aDestName The destination directory for the WSR
    * @throws IOException
    */
   protected static URL copyWsrToStagingDir( URL aWsr, String aDestName ) throws IOException
   {
      File stagingDir = new File( AeDeploymentFileInfo.getStagingDirectory() );
      File copyDest = new File( stagingDir, aDestName );
      InputStream in = null;
      OutputStream out = null;
      try
      {
         in = aWsr.openStream();
         out = new FileOutputStream( copyDest );
         AeFileUtil.copy(in, out);
         return copyDest.toURI().toURL();
      }
      finally
      {
         AeCloser.close( in );
         AeCloser.close( out );
      }
   }
   
   /**
    * Deploys the bpr and checks that all of the pdd's were deployed without an
    * error.
    * @param aResourceClass class to which to load the resouces from.
    * @param aBprName
    * @throws IOException
    * @throws AeException
    */   
   public static void deployBprWithErrorCheck(Class aResourceClass, String aBprName) throws IOException, AeException
   {
      InputStream bprStream = aResourceClass.getResourceAsStream("/" + aBprName); //$NON-NLS-1$
      if (bprStream != null)
      {
         AeDeploymentUtil.deployBprWithErrorCheck(bprStream, aBprName);
      }
      
   }   
   
   /**
    * Deploys the bpr and checks that all of the pdd's were deployed without an
    * error.
    * @param aBprStream
    * @param aBprName
    * @throws IOException
    * @throws AeException
    */
   public static void deployBprWithErrorCheck(InputStream aBprStream, String aBprName) throws IOException, AeException
   {
      IAeDeploymentSummary summary = deployBpr(aBprStream, aBprName);

      if (summary.getTotalErrors() != 0)
      {
         handleFailedDeployment(summary);
      }
   }

   /**
    * Deploys the bpr and returns a summary of the deployment information
    * @param aBprStream
    * @param aBprName
    * @return IAeDeploymentSummary - contains info on each process that was 
    *                                deployed
    * @throws IOException - thrown if there are problems creating the tmp file
    * @throws AeException - thrown if there is a fatal error deploying
    */
   public static IAeDeploymentSummary deployBpr(InputStream aBprStream, String aBprName) throws IOException, AeException
   {
      AeStructuredDeploymentLog log = new AeStructuredDeploymentLog();
      File tmpFile = File.createTempFile("ae-tmpBpr", "bpr"); //$NON-NLS-1$ //$NON-NLS-2$
      FileOutputStream fout = null;
      try
      {
         fout = new FileOutputStream(tmpFile);
         AeFileUtil.copy(aBprStream, fout);
         fout.close();
         aBprStream.close();
         AeEngineFactory.getEngineAdministration().deployNewBpr(tmpFile, aBprName, log);
      }
      finally
      {
         AeCloser.close(fout);
         AeCloser.close(aBprStream);
         tmpFile.delete();
      }
      IAeDeploymentSummary summary = log.getDeploymentSummary();
      return summary;
   }

   /**
    * Gives the manager a chance to handle the failed deployment. This method
    * will simply throw an exception with the details of which pdd's failed. 
    * @param aSummary
    * @throws AeException
    */
   private static void handleFailedDeployment(IAeDeploymentSummary aSummary) throws AeException
   {
      String errorNames = getPddNamesWithErrors(aSummary);
      String errorMessage = AeMessages.format("AeDeploymentUtil.DeploymentError", errorNames.toString()); //$NON-NLS-1$
      throw new AeException(errorMessage);
   }

   /**
    * Gets the pdd names from the summary that contain errors
    * @param aSummary
    */
   private static String getPddNamesWithErrors(IAeDeploymentSummary aSummary)
   {
      StringBuffer buffer = new StringBuffer();
      String delim = ""; //$NON-NLS-1$
      for(Iterator it=aSummary.getDeploymentInfoList().iterator(); it.hasNext();)
      {
         IAeDeploymentInfo info = (IAeDeploymentInfo) it.next();
         if (info.getNumErrors() > 0)
         {
            buffer.append(delim);
            buffer.append(info.getPddName());
            delim = ","; //$NON-NLS-1$
         }
      }
      return buffer.toString();
   }
} 