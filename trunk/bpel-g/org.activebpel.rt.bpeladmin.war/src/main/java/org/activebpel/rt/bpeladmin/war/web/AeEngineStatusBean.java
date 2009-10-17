// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeEngineStatusBean.java,v 1.10 2007/08/09 19:17:12 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import java.text.MessageFormat;
import java.util.Date;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.IAeBusinessProcessEngine;
import org.activebpel.rt.bpel.server.admin.IAeEngineAdministration;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpeladmin.war.AeBuildNumber;
import org.activebpel.rt.bpeladmin.war.AeMessages;

/**
 *  Bean for driving display of home page.
 */
public class AeEngineStatusBean extends AeAbstractAdminBean
{
   /** The format of the engine version string. */
   private static final String VERSION_PATTERN = "{0} ({1})"; //$NON-NLS-1$

   /**
    * Default constructor.
    */
   public AeEngineStatusBean()
   {
   }
   
   /**
    * Indicates that engine should start
    * @param aValue Flag to signal ok to start engine.
    */
   public void setStart( boolean aValue )
   {
      if( aValue )
      {
         try
         {
            getAdmin().start();
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
         }
      }
   }
   
   /**
    * Indicates that engine should start
    * @param aValue Flag to signal ok to start engine.
    */
   public void setStop( boolean aValue )
   {
      if( aValue )
      {
         try
         {
            getAdmin().stop();
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
         }
      }
   }
   
   /**
    * Returns the number of deployed processes.
    */
   public int getDeployedProcessesSize()
   {
      try
      {
         if( getAdmin().getDeployedProcesses() == null )
         {
            return 0;
         }
         return getAdmin().getDeployedProcesses().length;
      }
      catch(Exception ex)
      {
         AeException.logError(ex);
         return 0;
      }
   }
   
   /**
    * Returns the engine start date.
    */
   public Date getStartDate()
   {
      Date startDate = null;
      if(getAdmin().getEngineState() == IAeEngineAdministration.RUNNING)
      {
         startDate = getAdmin().getStartDate();
      }
      return startDate;
   }
   
   /**
    * Returns the engine configuration description.
    */
   public String getEngineDescription()
   {
      IAeEngineAdministration admin = getAdmin();
      return admin.getEngineConfig().getDescription();
   }
   
   /**
    * Returns the engine version.
    */
   public String getEngineVersion()
   {
      return MessageFormat.format(VERSION_PATTERN, new Object[] { AeBuildNumber.getVersionNumber(), AeBuildNumber.getMasterBuildNumber() });
   }
   
   /**
    * Returns boolean true if engine is running.
    */
   public boolean isEngineRunning()
   {
      return getAdmin().isRunning();
   }
   
   /**
    * Returns the current monitor status text which represents monitor state of engine.
    */
   public String getMonitorStatus()
   {
      // TODO (RN) These strings should be localized to the browser context not the engine
      int status = getAdmin().getMonitorStatus();
      if (status == IAeBusinessProcessEngine.MONITOR_WARNING)
         return AeMessages.getString("AeEngineStatusBean.MonitorWarning"); //$NON-NLS-1$
      else if (status == IAeBusinessProcessEngine.MONITOR_ERROR)
         return AeMessages.getString("AeEngineStatusBean.MonitorError"); //$NON-NLS-1$
      else
         return AeMessages.getString("AeEngineStatusBean.MonitorNormal"); //$NON-NLS-1$
   }
   
   /**
    * Returns the engine status started, stopped, not configured.
    */
   public String getEngineStatus()
   {
      // TODO (RN) These strings should be localized to the browser context not the engine
      String status = AeMessages.getString("AeEngineStatusBean.0"); //$NON-NLS-1$
      // if the factory says engine storage is not ready then display that message
      if(! AeEngineFactory.isEngineStorageReady())
      {
         status = AeMessages.getString("AeEngineStatusBean.1"); //$NON-NLS-1$
      }
      else
      { 
         // check the state of the engine and get information to display to the user
         switch(getAdmin().getEngineState())
         {
            case IAeEngineAdministration.CREATED:
               status = AeMessages.getString("AeEngineStatusBean.2"); //$NON-NLS-1$
            break;
         
            case IAeEngineAdministration.STARTING:
               status = AeMessages.getString("AeEngineStatusBean.3"); //$NON-NLS-1$
            break;
         
            case IAeEngineAdministration.RUNNING:
               status = AeMessages.getString("AeEngineStatusBean.4"); //$NON-NLS-1$
            break;

            case IAeEngineAdministration.STOPPING:
               status = AeMessages.getString("AeEngineStatusBean.5"); //$NON-NLS-1$
            break;
         
            case IAeEngineAdministration.STOPPED:
               status = AeMessages.getString("AeEngineStatusBean.6"); //$NON-NLS-1$
            break;
         
            case IAeEngineAdministration.SHUTTINGDOWN:
               status = AeMessages.getString("AeEngineStatusBean.7"); //$NON-NLS-1$
            break;
         
            case IAeEngineAdministration.SHUTDOWN:
               status = AeMessages.getString("AeEngineStatusBean.8"); //$NON-NLS-1$
            break;
         
            case IAeEngineAdministration.ERROR:
               status = AeMessages.getString("AeEngineStatusBean.9") + getAdmin().getEngineErrorInfo(); //$NON-NLS-1$
            break;
         }
      }
      return status;
   }
}
