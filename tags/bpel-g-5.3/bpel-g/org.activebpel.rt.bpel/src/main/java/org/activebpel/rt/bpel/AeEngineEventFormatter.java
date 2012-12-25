// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/AeEngineEventFormatter.java,v 1.3.26.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AER;
import org.activebpel.rt.util.AeMessageFormatter;
import org.activebpel.rt.util.AeUtil;


/**
 * <p>Basic engine event formatting.</p>
 */
public abstract class AeEngineEventFormatter extends AeMessageFormatter
{
   /** Properties object used to access format specifiers. */
   private Properties mFmtProps;
   
   /**
    * @see org.activebpel.rt.util.AeMessageFormatter#populateMap(java.lang.String)
    */
   public void populateMap( String aPropRoot )
   {
      // Populate map with local formats
      //
      super.populateMap( "MessageFormatting.Engine.Event" ); //$NON-NLS-1$
      
      // Populate map with additional subclass specializations, if any.
      //
      if ( !AeUtil.isNullOrEmpty( aPropRoot ))
         super.populateMap( aPropRoot );      
   }

   /**
    * @see org.activebpel.rt.util.AeMessageFormatter#getResourceString(java.lang.String)
    */   
   public String getResourceString(String aKey)
   {
      return (String)getFmtProps().get( aKey );
   }

   /**
    * @see org.activebpel.rt.util.AeMessageFormatter#getFormatString(java.lang.String)
    */
   public String getFormatString(String aKey)
   {
      return getResourceString( aKey );
   }

   /**
    * Returns the base set of event arguments.  If subclasses add args to the
    * list, they should override this method to return the true # of args.
    *
    * @see org.activebpel.rt.util.AeMessageFormatter#getMaxArgs()
    */
   public int getMaxArgs()
   {
      return AER.ARG_COUNT ;
   }

   /**
    * Set the event-based argument values.
    * 
    * @param aEventID The event ID.
    * @param aPath The activity's node path.
    * @param aPID The process engine instance PID.
    * @param aFault The name of the fault involved, if any.
    * @param aInfo Any ancillary info object sent with the event.
    */
   public void setEventArguments( int aEventID, String aPath, long aPID, String aFault, String aInfo  )
   {
      setArgument( AER.ARG_NODE_OR_LINK_XPATH, aPath );
      setArgument( AER.ARG_PID, aPID + ""); //$NON-NLS-1$
      setArgument( AER.ARG_EVENT_ID, aEventID );
      setArgument( AER.ARG_FAULT_NAME, aFault );
      setArgument( AER.ARG_ANCILLARY_INFO, aInfo );
      setArgument( AER.ARG_TIMESTAMP, AER.getFormattedTimestamp( getResourceString( AER.sTSFormatKey )));
   }
   
   /**
    * Returns the (populated) format properties object.
    * 
    * @return Properties
    */
   public Properties getFmtProps()
   {
      if ( mFmtProps == null )
      {
         mFmtProps = new Properties();
         try {
            InputStream in = getClass().getResourceAsStream("/org/activebpel/rt/bpel/eventFormat.properties"); //$NON-NLS-1$
            mFmtProps.load( in );
         }
         catch ( FileNotFoundException fnfe )
         {
            AeException.logError( fnfe, AeMessages.getString("AeEngineEventFormatter.ERROR_3") ); //$NON-NLS-1$
         }
         catch ( IOException ioe )
         {
            AeException.logError( ioe, AeMessages.getString("AeEngineEventFormatter.ERROR_4") ); //$NON-NLS-1$
         }
      }

      return mFmtProps;
   }
}
