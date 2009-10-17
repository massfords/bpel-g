// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/AeBuildNumber.java,v 1.4 2005/10/12 19:33:27 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war;

import java.io.IOException;
import java.util.Properties;

/**
 * Build number class is a helper for accessing version info.
 */
public class AeBuildNumber
{
   private static String mBuildNumber = ""; //$NON-NLS-1$
   private static String mBuildDate   = ""; //$NON-NLS-1$
   private static String mVersionNumber = ""; //$NON-NLS-1$
   private static String mMasterBuildNumber = ""; //$NON-NLS-1$

   static
   {
      try
      {
         Properties props = new Properties();
         props.load(new AeBuildNumber().getClass().getResourceAsStream("version.properties")); //$NON-NLS-1$
      
         mBuildNumber   = props.getProperty("build.number"); //$NON-NLS-1$
         mBuildDate     = props.getProperty("build.date"); //$NON-NLS-1$
         mVersionNumber = props.getProperty("build.version"); //$NON-NLS-1$
         mMasterBuildNumber = props.getProperty("build.master"); //$NON-NLS-1$
      }
      catch (IOException ioe)
      {
      }
   }

   /**
    * default constructor for a build resource file
    */
   private AeBuildNumber()
   {
   }

   /**
    * Obtains the build number for this component.
    * 
    * @return a String value representing the build number
    */
   public final static String getBuildNumber() 
   {
      return mBuildNumber;
   }
   
   /**
    * Obtains the build number for this component.
    * 
    * @return a String value representing the build number
    */
   public final static String getVersionNumber() 
   {
      return mVersionNumber;
   }
   
   /**
    * Obtains the build date for this component.
    * 
    * @return a String value representing the build date
    */
   public final static String getBuildDate() 
   {
      return mBuildDate;
   }

   /**
    * Obtains the master build number for this component.
    */
   public final static String getMasterBuildNumber()
   {
      return mMasterBuildNumber;
   }
}
