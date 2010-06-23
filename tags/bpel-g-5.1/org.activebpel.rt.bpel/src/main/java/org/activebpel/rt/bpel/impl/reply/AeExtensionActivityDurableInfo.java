//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/reply/AeExtensionActivityDurableInfo.java,v 1.3 2008/03/11 03:05:23 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.reply;

import java.util.HashMap;
import java.util.Map;

/**
 * Extension activity implementation of a durable reply info.
 */
public class AeExtensionActivityDurableInfo implements IAeDurableReplyInfo
{
   /** Durable reply prototype that is used by the factory. */
   public static String TYPE = "ExtensionActivityDurableInfo"; //$NON-NLS-1$
   /** Property key for invoke objects process id. */
   public static String PROCESS_ID = "ProcessId"; //$NON-NLS-1$
   /** Property key for invoke object's location path. */
   public static String LOCATION_PATH = "LocationPath"; //$NON-NLS-1$
   /** Property key for transmission id */
   public static String TRANSMISSION_ID = "TransmissionId";  //$NON-NLS-1$

   /** Process Id of invoke */
   private long mProcessId;
   /** LocationPath of invoke */
   private String mLocationPath;
   /** Transmission id for pa */
   private long mTransmissionId; 

   /**
    * C'tor
    * @param aProperties
    */
   public AeExtensionActivityDurableInfo(Map aProperties)
   {
      setProcessId(Long.parseLong((String) aProperties.get(PROCESS_ID)));
      setLocationPath((String) aProperties.get(LOCATION_PATH));
      String transmissionId = (String) aProperties.get(TRANSMISSION_ID);
      if (transmissionId != null)
         setTransmissionId(Long.parseLong(transmissionId));
   }

   /**
    * C'tor
    * @param aProcessId
    * @param aLocationPath
    */
   public AeExtensionActivityDurableInfo(long aProcessId, String aLocationPath, long aTransmissionId)
   {
      setProcessId(aProcessId);
      setLocationPath(aLocationPath);
      setTransmissionId(aTransmissionId);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeDurableReplyInfo#getProperties()
    */
   public Map getProperties()
   {
      Map props = new HashMap();
      props.put( PROCESS_ID, String.valueOf( getProcessId() ) );
      props.put( LOCATION_PATH, String.valueOf( getLocationPath() ) );
      props.put( TRANSMISSION_ID, String.valueOf( getTransmissionId() ) );
      return props;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeDurableReplyInfo#getType()
    */
   public String getType()
   {
      return TYPE; 
   }

   /**
    * @return the locationPath
    */
   public String getLocationPath()
   {
      return mLocationPath;
   }

   /**
    * @param aLocationPath the locationPath to set
    */
   protected void setLocationPath(String aLocationPath)
   {
      mLocationPath = aLocationPath;
   }

   /**
    * @return the pid
    */
   public long getProcessId()
   {
      return mProcessId;
   }

   /**
    * @param aProcessId the pid to set
    */
   protected void setProcessId(long aProcessId)
   {
      mProcessId = aProcessId;
   }

   /**
    * @return the transmissionId
    */
   public long getTransmissionId()
   {
      return mTransmissionId;
   }

   /**
    * @param aTransmissionId the transmissionId to set
    */
   public void setTransmissionId(long aTransmissionId)
   {
      mTransmissionId = aTransmissionId;
   }
}
