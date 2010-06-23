//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/AeLoggingFilter.java,v 1.1 2007/02/16 14:05:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging; 

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.bpel.IAeProcessEvent;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;

/**
 * Base class with factory method to return a filter given the config setting
 * for logging. 
 */
public class AeLoggingFilter implements IAeLoggingFilter
{
   /** filter that doesn't do any logging */
   public static final String NONE = "urn:ae:none"; //$NON-NLS-1$
   /** filter that logs all execution events (excludes dead-path) */
   public static final String EXECUTION = "urn:ae:execution"; //$NON-NLS-1$
   /** filter that logs all events */
   public static final String FULL = "urn:ae:full"; //$NON-NLS-1$
   
   /** filter that doesn't accept any events */
   private static final IAeLoggingFilter FILTER_NONE = new AeLoggingFilter(NONE, false); 
   /** filter that accepts all events except READY_TO_EXECUTE */
   private static final IAeLoggingFilter FILTER_ALL_BUT_READY = new AeAllowAllFilter(FULL); 
   /** refinement of ALL_BUT_READY that also excludes dead-path events */
   private static final IAeLoggingFilter FILTER_NO_DEADPATH = new AeNoDeadPathFilter(EXECUTION); 

   /**
    * Accepts all but READY_TO_EXECUTE 
    */
   private static class AeAllowAllFilter extends AeLoggingFilter
   {
      /**
       * Ctor 
       */
      public AeAllowAllFilter(String aName)
      {
         super(aName, true);
      }
      
      /**
       * @see org.activebpel.rt.bpel.server.logging.AeLoggingFilter#accept(org.activebpel.rt.bpel.IAeProcessEvent)
       */
      public boolean accept(IAeProcessEvent aEvent)
      {
         return aEvent.getEventID() != IAeProcessEvent.READY_TO_EXECUTE;
      }

      /**
       * @see org.activebpel.rt.bpel.server.logging.AeLoggingFilter#accept(org.activebpel.rt.bpel.IAeProcessInfoEvent)
       */
      public boolean accept(IAeProcessInfoEvent aInfoEvent)
      {
         return true;
      }
      
   }
   
   /**
    * Subclass of the ALL filter that excludes DEAD_PATH events
    */
   private static class AeNoDeadPathFilter extends AeAllowAllFilter
   {
      /**
       * Ctor
       * @param aName
       */
      public AeNoDeadPathFilter(String aName)
      {
         super(aName);
      }
      
      /**
       * @see org.activebpel.rt.bpel.server.logging.AeLoggingFilter.AeAllowAllFilter#accept(org.activebpel.rt.bpel.IAeProcessEvent)
       */
      public boolean accept(IAeProcessEvent aEvent)
      {
         return super.accept(aEvent) && aEvent.getEventID() != IAeProcessEvent.DEAD_PATH_STATUS;
      }
      
   }
   
   /** static map of config values to their filters */
   private static final Map sFilters = new HashMap();
   
   static
   {
      // true and false are legacy values
      sFilters.put("false", FILTER_NONE); //$NON-NLS-1$
      sFilters.put("true", FILTER_ALL_BUT_READY); //$NON-NLS-1$
      
      sFilters.put(FULL, FILTER_ALL_BUT_READY);
      sFilters.put(EXECUTION, FILTER_NO_DEADPATH);
      sFilters.put(NONE, FILTER_NONE);
   }
   
   /** true if the filter accepts at least one type of process or process info event */
   private boolean mEnabled;
   /** the name of the filter */
   private String mName;
   
   /**
    * Ctor
    * @param aEnabled
    */
   private AeLoggingFilter(String aName, boolean aEnabled)
   {
      setEnabled(aEnabled);
      setName(aName);
   }
   
   /**
    * Factory method to create a filter given a config value
    * @param aConfigValue
    */
   public static IAeLoggingFilter getLoggingFilter(String aConfigValue)
   {
      IAeLoggingFilter filter = (IAeLoggingFilter) sFilters.get(aConfigValue);
      if (filter == null)
         filter = FILTER_NONE;
      return filter;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.logging.IAeLoggingFilter#accept(org.activebpel.rt.bpel.IAeProcessEvent)
    */
   public boolean accept(IAeProcessEvent aEvent)
   {
      return false;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.logging.IAeLoggingFilter#accept(org.activebpel.rt.bpel.IAeProcessInfoEvent)
    */
   public boolean accept(IAeProcessInfoEvent aInfoEvent)
   {
      return false;
   }
   
   /**
    * @return the enabled
    */
   public boolean isEnabled()
   {
      return mEnabled;
   }

   /**
    * @param aEnabled the enabled to set
    */
   protected void setEnabled(boolean aEnabled)
   {
      mEnabled = aEnabled;
   }

   /**
    * @see org.activebpel.rt.bpel.server.logging.IAeLoggingFilter#getName()
    */
   public String getName()
   {
      return mName;
   }

   /**
    * @param aName the name to set
    */
   protected void setName(String aName)
   {
      mName = aName;
   }
}
 