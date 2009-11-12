// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/AeQueuedReceiveDetail.java,v 1.8 2005/02/01 19:56:36 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin;

import java.beans.ConstructorProperties;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Light weight JavaBean impl of the queued receive object. Omits the receive
 * data and message receiver in favor of the receiver's xpath location.  
 */
public class AeQueuedReceiveDetail
{
   /** xpath location for the message receiver */
   private String mLocation;
   /** partner link name */   
   private String mPartnerLinkName;
   /** port type */   
   private QName mPortType;
   /** operation */   
   private String mOperation;
   /** process id */   
   private long mProcessId;
   /** correlation data */
   private String mCorrelatedData;
   /** message data */
   private String mData;
   
   
   /**
    * No-arg ctor necessary for JavaBean 
    */
   public AeQueuedReceiveDetail()
   {
   }
   
   /**
    * @param aProcessId
    * @param aPartnerLinkName
    * @param aPortType
    * @param aOperation
    */
   @ConstructorProperties({"processId", "partnerLinkName", "portType", "operation", "location", "correlationData", "data"})
   public AeQueuedReceiveDetail(long aProcessId, String aPartnerLinkName,
      QName aPortType, String aOperation, String aLocation, 
         String aCorrelationData, String aData)
   {
      mLocation = aLocation;
      mProcessId = aProcessId;
      mPartnerLinkName = aPartnerLinkName;
      mPortType = aPortType;
      mOperation = aOperation;
      setCorrelatedData(aCorrelationData);
      setData(aData);
   }
   
   /**
    * Accessor for the partner link name.
    */
   public String getPartnerLinkName()
   {
      return mPartnerLinkName;
   }
   
   public void setPartnerLinkName(String aPlink) {
       mPartnerLinkName = aPlink;
   }

   /**
    * Getter for the location
    */
   public String getLocation()
   {
      return mLocation;
   }

   /**
    * Setter for the location
    * @param aString
    */
   public void setLocation(String aString)
   {
      mLocation = aString;
   }

   /**
    * Accessor for the operation.
    */
   public String getOperation()
   {
      return mOperation;
   }

   /**
    * Accessor for the port type QName.
    */
   public QName getPortType()
   {
      return mPortType;
   }
   
   public void setPortType(QName aPortType) {
       mPortType = aPortType;
   }
   
   /**
    * Accessor for the process id.
    */
   public long getProcessId()
   {
      return mProcessId;
   }
   
   public void setProcessId(long aProcessId) {
       mProcessId = aProcessId;
   }

   /**
    * Returns true if the detail contains correlation data. 
    */
   public boolean isCorrelated()
   {
      return getCorrelatedData() != null;
   }
   
   /**
    * Returns true if the detail contains message data. 
    */
   public boolean isMessageDataAvailable()
   {
      return getData() != null;
   }
   
   /**
    * Extract string data from map.
    * @param aMap
    */
   public static String extractMapData( Map aMap )
   {
      // @todo - put this in for the ui - may want to revisit this with something
      // that allows more control over th display
      StringBuffer sb = new StringBuffer();
      String sep = ""; //$NON-NLS-1$
      for( Iterator iter = aMap.entrySet().iterator(); iter.hasNext(); )
      {
         Map.Entry entry = (Map.Entry)iter.next();
         sb.append( sep );
         sb.append( entry.getKey() );
         sb.append( "=" ); //$NON-NLS-1$
         sb.append( entry.getValue() );
         sep="\\n"; //$NON-NLS-1$
      }
      return sb.toString();
   }

public void setCorrelatedData(String correlatedData) {
    mCorrelatedData = correlatedData;
}

public String getCorrelatedData() {
    return mCorrelatedData;
}

public void setData(String data) {
    mData = data;
}

public String getData() {
    return mData;
}
}
