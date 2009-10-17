// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeDeploymentLogsBean.java,v 1.2 2004/10/21 19:19:52 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 * Wraps the deployment logs listing and allows access to
 * a single log file via its name.
 */
public class AeDeploymentLogsBean
{
   /** Types wrapper around deployment log file names. */
   protected AeJavaTypesWrapper[] mLogFiles;
   /** A specific log file name. */
   protected String mLogFile;

   /**
    * Constructor.
    */
   public AeDeploymentLogsBean()
   {
      mLogFiles = AeJavaTypesWrapper.wrap(
         AeEngineFactory.getEngineAdministration().getDeploymentLogListing() );
         
      mLogFile = AeEngineFactory.getEngineAdministration().getDeploymentLog();         
   }
     
   /**
    * Returns the contents of the named log file as a string.
    */
   public String getLogFile()
   {
      return mLogFile;
   }
   
   /**
    * Accessor for the log file listing size.
    */
   public int getLogListingSize()
   {
      if( mLogFiles != null )
      {
         return mLogFiles.length;
      }
      else
      {
         return 0;
      }
   }
   
   /**
    * Indexed accessor for the log file listing.
    * @param aIndex
    */
   public AeJavaTypesWrapper getLogListing( int aIndex )
   {
      return mLogFiles[aIndex];
   }
}
