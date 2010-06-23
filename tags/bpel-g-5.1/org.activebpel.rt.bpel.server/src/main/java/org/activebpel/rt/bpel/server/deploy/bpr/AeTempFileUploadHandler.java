//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/bpr/AeTempFileUploadHandler.java,v 1.1 2005/06/13 17:54:07 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.bpr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeFileUtil;
import org.activebpel.rt.util.AeUtil;

/**
 * Default implementation of <code>IAeFileUploadHandler</code>.  Uses the given
 * <code>InputStream</code> to create a temp file for bpr deployment.  
 * Delegates to <code>IAeEngineAdministration</code> to handle the actual 
 * deployment details.  Them removes the temp file (after success or failure).
 */
public class AeTempFileUploadHandler
{

   // error message constants
   private static final String NO_FILE_UPLOADED = "AeTempFileUploadHandler.ERROR_1"; //$NON-NLS-1$
   private static final String DELETE_FAILED    = "AeTempFileUploadHandler.ERROR_2"; //$NON-NLS-1$
   
   /**
    * Creates a temp file from the given stream.  Call deployNewBpr on the
    * <code>IAeEngineAdministration</code> impl.
    * @param aBprFileName
    * @param aBprStream
    * @param aLogger
    * @throws AeException
    */
   public static void handleUpload( String aBprFileName, InputStream aBprStream, IAeDeploymentLogger aLogger ) throws AeException
   {
      File tmpFile = null;
      try
      {
         tmpFile = createTempFile( aBprStream );
         AeEngineFactory.getEngineAdministration().deployNewBpr(tmpFile, aBprFileName, aLogger);
      }
      catch( IOException io )
      {
         throw new AeException(io);
      }
      finally
      {
         if( tmpFile != null )
         {
            if( !tmpFile.delete() )
            {
               String message = AeMessages.format( DELETE_FAILED, new Object[]{tmpFile.getAbsolutePath()} );
               AeException.logWarning( message  );
            }
         }
      }
   }
   
   /**
    * Create the temp file.
    * @param aIn
    * @throws IOException
    * @throws AeException
    */
   protected static File createTempFile( InputStream aIn ) throws IOException, AeException
   {
      File tmpFile = AeUtil.getTempFile("bpr", ".bpr"); //$NON-NLS-1$ //$NON-NLS-2$
      FileOutputStream fos = null;
      try
      {
         fos = new FileOutputStream(tmpFile);
         long totalSize = AeFileUtil.copy( aIn, fos );
         
         if (totalSize == 0)
         {
            throw new AeException(AeMessages.getString(NO_FILE_UPLOADED));
         }
         
         return tmpFile;
      }
      finally
      {
         AeCloser.close( fos );
      }
   }
}
