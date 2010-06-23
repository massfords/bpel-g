// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AER.java,v 1.3 2006/02/14 22:34:05 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Constants and utility methods for the application runtime.
 */
public class AER
{
   //////////////////////////////////////
   // Message Formatting argument indices
   //////////////////////////////////////
   
   /** Index for Duration or Deadline argument. */
   public static final int ARG_DURATION_OR_DEADLINE = 0 ;
   /** Index for Expression argument. */
   public static final int ARG_EXPRESSION = 1 ;
   /** Index for Evaluated Expression argument. */
   public static final int ARG_EVALUATED_EXPRESSION = 2 ;
   /** Index for Activity Name argument. */
   public static final int ARG_ACTIVITY_NAME = 3 ;
   /** Index for Node/Link XPath Expression argument. */
   public static final int ARG_NODE_OR_LINK_XPATH = 4 ;
   /** Reserved. */
   public static final int ARG_RESERVED = 5 ;
   /** Index for Activity Type argument. */
   public static final int ARG_ACTIVITY_TYPE = 6 ;
   /** Index for Fault Name argument. */
   public static final int ARG_FAULT_NAME = 7 ;
   /** Index for Link Name argument. */
   public static final int ARG_LINK_NAME = 8 ;
   /** Index for Link Transition Condition argument. */
   public static final int ARG_LINK_XTN_CONDITION = 9 ;

   /** Index for PID argument. */
   public static final int ARG_PID = 10 ;
   /** Index for Link Transition Condition argument. */
   public static final int ARG_ANCILLARY_INFO = 11 ;
   /** Index for Event ID argument. */
   public static final int ARG_EVENT_ID = 12 ;
   
   /** Wait duration in seconds for Wait Activity. */
   public static final int ARG_WAIT_SECS = 13 ;
   /** Join condition potentially maintained by every activity. */
   public static final int ARG_JOIN_CONDITION = 14 ;
   
   /** Timestamp for the event being reported. */
   public static final int ARG_TIMESTAMP = 15 ;
   
   /** Delineate the end of arguments - should always be 1+ the last arg index. */
   public static final int ARG_COUNT = 16 ;
   
   /** Properties key for timestamp format. */
   public static String sTSFormatKey = "MessageFormatting.Engine.Event.TimestampFormat"; //$NON-NLS-1$
   
   /**
    * Format the current time/instant using the format string provided.
    * 
    * @param aFormatString The SimpleDataFormat pattern to use.
    * 
    * @return String 
    */
   public static String getFormattedTimestamp( String aFormatString )
   {
      String ts = ""; //$NON-NLS-1$
      if(AeUtil.notNullOrEmpty(aFormatString))
      {
         SimpleDateFormat df = new SimpleDateFormat( aFormatString );
         ts = df.format( new Date());
      }
      return ts ;
   }
}
