//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/bpr/AeJarFileBprAccessor.java,v 1.5 2006/07/18 20:05:33 ckeller Exp $
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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentException;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;
import org.activebpel.rt.util.AeJarReaderUtil;
import org.activebpel.rt.util.AeUtil;

/**
 * A <code>IAeBprFileStrategy</code> impl where bpr resources are pulled from
 * directly from the archive file.
 * 
 * Currently this class is only used by <code>AeMain</code> to perform
 * offline bpr validation.
 */
public class AeJarFileBprAccessor extends AeAbstractBprStrategy
{
   
   /**
    * Constructor.
    * @param aDeploymentContext
    */
   public AeJarFileBprAccessor( IAeDeploymentContext aDeploymentContext )
   {
      super( aDeploymentContext );
   }
   
   /**
    * Reads through the BPR archive and sets up the internal state.
    * @throws AeDeploymentException
    */
   public void init() throws AeDeploymentException
   {
      AeJarReaderUtil jru = null;
      try
      {
         jru = new AeJarReaderUtil( getDeploymentContext().getDeploymentLocation());
         setPddResources(  jru.getEntryNames( new AeNameFilter(".pdd") ) ); //$NON-NLS-1$
         setPdefResources( jru.getEntryNames( new AeNameFilter(".pdef") ) ); //$NON-NLS-1$
         
         Collection wsdds = jru.getEntryNames( new AeNameFilter(".wsdd") ); //$NON-NLS-1$
         if( !AeUtil.isNullOrEmpty(wsdds) )
         {
            if( !getPddResources().isEmpty() )
            {
               throw new AeDeploymentException(AeMessages.getString("AeJarFileBprAccessor.ERROR_0")); //$NON-NLS-1$
            }

            setWsddResource( (String)wsdds.iterator().next() );
         }
      }
      catch (IOException e)
      {
         
         throw new AeDeploymentException( AeMessages.format( 
               "AeJarFileBprAccessor.ERROR_7", getDeploymentContext().getDeploymentLocation() ), e ); //$NON-NLS-1$
      }
      finally
      {
         if( jru != null )
         {
            jru.close();
         }
      }
   }

   
   /**
    * Convience class - impl of FilenameFilter for building
    * deployment descriptor object.
    */
   static class AeNameFilter implements FilenameFilter
   {
      String mExt;
      
      /**
       * Constructor
       * @param aExt extension to filter on.
       */
      public AeNameFilter( String aExt)
      {
         mExt = aExt;
      }
      
      /**
       * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
       */
      public boolean accept( File aFile, String aFilename )
      {
         return !AeUtil.isNullOrEmpty(aFilename) && 
                                    aFilename.toLowerCase().endsWith(mExt);            
      }
   }
   
}
