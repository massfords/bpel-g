// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/AeBPELProcessEventFormatter.java,v 1.7 2005/03/18 23:25:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

import java.util.Arrays;
import java.util.HashMap;

import org.activebpel.rt.bpel.AeEngineEventFormatter;
import org.activebpel.rt.bpel.IAeBaseProcessEvent;
import org.activebpel.rt.bpel.IAeProcessEvent;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.util.AER;
import org.activebpel.rt.util.AeUtil;


/**
 * Basic BPEL Engine Event formatting.
 */
public class AeBPELProcessEventFormatter extends AeEngineEventFormatter
{
   /** Singleton formatter instance. */
   private static AeBPELProcessEventFormatter sFormatter ;

   /** Map of format IDs to format strings. */
   private HashMap mEventFormatMap = new HashMap();

   /**
    * Private ctor.  Get the singleton instance with getInstance().
    */
   private AeBPELProcessEventFormatter()
   {
      // Nothing special to add.
      populateMap( null );
   }

   /**
    * Get the singleton instance of this formatter.
    * 
    * @return AeBPELProcessEventFormatter
    */
   public static AeBPELProcessEventFormatter getInstance()
   {
      if ( sFormatter == null )
         sFormatter = new AeBPELProcessEventFormatter();
         
      return sFormatter ; 
   }
   
   /**
    * Format a process event.
    * 
    * @param aEvent The event object to format.
    * 
    * @return String containing the formatted event.
    */
   public String formatEvent( IAeProcessEvent aEvent )
   {
      return format( aEvent.getEventID(), convertToArray(aEvent));
   }

   /**
    * Format a process info event.
    * 
    * @param aEvent The event object to format.
    * 
    * @return String containing the formatted event.
    */
   public String formatEvent( IAeProcessInfoEvent aEvent )
   {
      return format( aEvent.getEventID(), convertToArray(aEvent));
   }

   /**
    * @see org.activebpel.rt.util.AeMessageFormatter#format(int, java.lang.Object[])
    */
   public String format( int aEventType, Object[] aArguments )
   {
      // Format the event message, then check to see if we should add an
      //  error/fault indicator at the end.
      //
      String text = super.format( aEventType, aArguments );
      
      // check aEventType to see if we're logging an error or a 
      //  fault, and append the appropriate suffix, if so.
      //
      switch ( aEventType )
      {
         case IAeProcessEvent.DEAD_PATH_STATUS:
            text += " [d]" ; //$NON-NLS-1$
            break ;
            
         case IAeProcessEvent.EXECUTE_FAULT:
            text += " [f]" ; //$NON-NLS-1$
            break ;
            
         default:
            // don' do nuttin'
            break ;
      }
      
      return text ;
   }

   /**
    * @see org.activebpel.rt.util.AeMessageFormatter#getFormatMap()
    */   
   public HashMap getFormatMap()
   {
      return mEventFormatMap ;
   }

   /**
    * @see org.activebpel.rt.util.AeMessageFormatter#getFormatString(java.lang.String)
    */
   public String getFormatString(String aKey)
   {
      String fmt = super.getFormatString( aKey );

      // BPEL Server Engine Event messages include a timestamp/pid prefix.
      //
      if ( fmt == null || fmt.equals( aKey ))
         return aKey ;
      else
         return getResourceString( "MessageFormatting.Engine.Event.PidAndTimestamp" ) + fmt ; //$NON-NLS-1$
   }

   /**
    * Converts the event into an array for use in the formatting routine.
    * 
    * @param aEvent The info event to be formatted for output.
    */
   protected Object[] convertToArray(IAeBaseProcessEvent aEvent)
   {
      Object[] args = new Object[getMaxArgs()];
      Arrays.fill(args, ""); //$NON-NLS-1$
      
      String ancillaryInfo = aEvent.getAncillaryInfo();
      if (AeUtil.isNullOrEmpty(ancillaryInfo))
      {
         ancillaryInfo = getResourceString("MessageFormatting.Engine.Event.AncillaryInfo.NULL"); //$NON-NLS-1$
      }

      args[AER.ARG_NODE_OR_LINK_XPATH] = aEvent.getNodePath();
      args[AER.ARG_PID] = String.valueOf(aEvent.getPID());
      args[AER.ARG_EVENT_ID] = String.valueOf(aEvent.getEventID());
      args[AER.ARG_FAULT_NAME] = aEvent.getFaultName();
      args[AER.ARG_ANCILLARY_INFO] = ancillaryInfo;
      String pattern = getResourceString( AER.sTSFormatKey );
      if (AeUtil.notNullOrEmpty(pattern))
         args[AER.ARG_TIMESTAMP] = AER.getFormattedTimestamp( pattern);
      
      // Engine puts evaluation info in ancillary attribute.
      //
      args[AER.ARG_LINK_XTN_CONDITION] = ancillaryInfo;
      args[AER.ARG_EVALUATED_EXPRESSION] = ancillaryInfo;
      args[AER.ARG_JOIN_CONDITION] = ancillaryInfo;
      
      return args;
   }
}
