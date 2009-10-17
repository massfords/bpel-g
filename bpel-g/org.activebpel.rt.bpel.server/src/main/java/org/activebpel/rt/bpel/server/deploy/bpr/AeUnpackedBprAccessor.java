//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/bpr/AeUnpackedBprAccessor.java,v 1.2 2005/06/17 21:51:13 PCollins Exp $
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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentException;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;
import org.activebpel.rt.util.AeUtil;

/**
 * A <code>IAeBprFileStrategy</code> impl where bpr resources are pulled from
 * an unpacked copy of the original bpr file.
 */
public class AeUnpackedBprAccessor extends AeJarFileBprAccessor
{
   /**
    * Constructor.
    * @param aDeploymentContext
    */
   public AeUnpackedBprAccessor(IAeDeploymentContext aDeploymentContext)
   {
      super(aDeploymentContext);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBprAccessor#init()
    */
   public void init() throws AeDeploymentException
   {
      File rootDir = new File( getDeploymentContext().getTempDeploymentLocation().getFile() );
      
      setPddResources(new ArrayList()); 
      listFiles( getPddResources(), rootDir, "", new AeSuffixFilter(".pdd") ); //$NON-NLS-1$ //$NON-NLS-2$

      setPdefResources( new ArrayList() );
      listFiles( getPdefResources(), rootDir, "", new AeSuffixFilter(".pdef") ); //$NON-NLS-1$ //$NON-NLS-2$

      List wsddList = new ArrayList();
      listFiles( wsddList, rootDir, "", new AeSuffixFilter(".wsdd") ); //$NON-NLS-1$ //$NON-NLS-2$
      
      if( !AeUtil.isNullOrEmpty(wsddList) )
      {
         if( !getPddResources().isEmpty() )
         {
            throw new AeDeploymentException(AeMessages.getString("AeUnpackedBprFile.ERROR_0"));  //$NON-NLS-1$
         }

         setWsddResource( (String)wsddList.iterator().next() );
      }
   }

   /**
    * Extract the appropriate files from the directory.
    * @param aMatches Container collection for holding matched resources.
    * @param aDir The directory in which to look for matches (recurses).
    * @param aPath Path prefix to add to file names.
    * @param aFilter FileFilter instance.
    */
   protected void listFiles( Collection aMatches, File aDir, String aPath, FileFilter aFilter )
   {
      File[] files = aDir.listFiles(aFilter);
      if( files!= null )
      {
         for( int i = 0; i < files.length; i++ )
         {
            if( files[i].isFile() )
            {
               aMatches.add( aPath + files[i].getName() );
            }
            else
            {
               String name = files[i].getName();
               if( !name.endsWith(File.separator) )
               {
                  name+=File.separatorChar;
               }
               listFiles( aMatches, files[i], aPath+name, aFilter );
            }
         }
      }
   }
   
   /**
    * Simple suffix file filter.
    */
   public class AeSuffixFilter implements FileFilter
   {
      /** suffix string */
      private String mSuffix;
      
      /**
       * Constructor.
       * @param aSuffix
       */
      public AeSuffixFilter(String aSuffix)
      {
         mSuffix = aSuffix;   
      }
      
      /**
       * @see java.io.FileFilter#accept(java.io.File)
       */
      public boolean accept(File aFile)
      {
         return aFile.isDirectory() || aFile.getName().endsWith(mSuffix);
      }
   }
}
