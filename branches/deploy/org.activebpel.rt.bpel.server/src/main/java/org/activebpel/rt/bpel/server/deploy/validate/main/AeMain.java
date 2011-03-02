// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/main/AeMain.java,v 1.5 2005/06/13 17:54:08 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate.main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;
import org.activebpel.rt.bpel.server.deploy.bpr.AeBpr;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.activebpel.rt.bpel.server.deploy.validate.IAePredeploymentValidator;

/**
 * Main wrapper for running the pre-deployment validators in standalone
 * mode.
 */
public class AeMain
{
   /** Hold the arg key value pairs */
   private Map mArgs;
   
   /**
    * Main method.
    * @param aArgs BPR file name plus any options.
    * @throws Exception
    */
   public static void main( String[] aArgs ) throws Exception
   {
      if( aArgs == null || aArgs.length == 0 )
      {
         printUsage();
         return;
      }
      
      try
      {
         AeMain main = new AeMain( aArgs );
         File file = new File(main.getArg("-f")); //$NON-NLS-1$
         if( !file.isFile() )
         {
            printBadFile( main.getArg("-f") ); //$NON-NLS-1$
            printUsage();               
            return;
         }
         
         IAeDeploymentContext context = new AeStandaloneDeploymentContext(file);
         
         String reporterClass = main.getArg("-e"); //$NON-NLS-1$
         IAeBaseErrorReporter reporter = (IAeBaseErrorReporter)Class.forName(reporterClass).newInstance();
         
         String validatorClass = main.getArg("-v"); //$NON-NLS-1$
         IAePredeploymentValidator validator = (IAePredeploymentValidator)Class.forName(validatorClass).newInstance();
         
         IAeBpr bprFile = AeBpr.createValidationBpr(context);

         validator.validate( bprFile, reporter );
         
         if( !reporter.hasErrors() )
         {
            display( file + AeMessages.getString("AeMain.4") ); //$NON-NLS-1$
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw e;
      }
   }
   
   /**
    * Print the expected usage to the console.
    */
   protected static void printUsage()
   {
      display( "usage: java " + AeMain.class.getName() + " <bprfile> {options}" ); //$NON-NLS-1$ //$NON-NLS-2$
      display( "options: " ); //$NON-NLS-1$
      display( "     -e <IAeBaseErrorHandler>" ); //$NON-NLS-1$
      display( "     -v <IAePredeploymentValidator>" ); //$NON-NLS-1$
   }
   
   /**
    * Print the invalid file error message to the console.
    * @param aBadFile The offending file name.
    */
   protected static void printBadFile( String aBadFile )
   {
      display( aBadFile + AeMessages.getString("AeMain.10") ); //$NON-NLS-1$
   }
   
   /**
    * Writes the message to the console.
    * @param aMessage Displayed in the console.
    */
   protected static void display( String aMessage )
   {
      System.out.println( aMessage );
   }

   /**
    * Constructor.
    * @param aArgs Command line arguments.
    */
   public AeMain( String[] aArgs )
   {
      mArgs = new HashMap();
      mArgs.put( "-f", aArgs[0] ); //$NON-NLS-1$
      mArgs.put( "-v", "org.activebpel.rt.bpel.server.deploy.validate.AePredeploymentValidator" ); //$NON-NLS-1$ //$NON-NLS-2$
      mArgs.put( "-e", "org.activebpel.rt.bpel.server.deploy.validate.main.AeErrorReporterConsole" ); //$NON-NLS-1$ //$NON-NLS-2$

      for( int i = 1; i < aArgs.length; i++ )
      {
         String flag = aArgs[i];
         ++i;
         if( i >= aArgs.length )
         {
            break;
         }
         else
         {
            String value = aArgs[i];
            mArgs.put( flag, value );         
         }
      }
   }
   
   /**
    * Retrieve a command line arg via its flag or null if none is found.
    * @param aFlag The flag from the command line.
    */
   public String getArg( String aFlag )
   {
      return (String) mArgs.get( aFlag );
   }
}
