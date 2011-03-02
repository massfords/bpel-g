//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeEngineAlert.java,v 1.3 2006/10/20 14:41:25 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl; 

import java.util.Date;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.IAeEngineAlert;
import org.w3c.dom.Document;

/**
 * Impl of the engine alert interface
 */
public class AeEngineAlert extends AeEngineEvent implements IAeEngineAlert
{
   /** details for the alert */
   private Document mDetails;
   /** location of the activity that generated the alert */
   private String mLocation;
   /** name of the fault that generated the alert */
   private QName mFaultName;

   /**
    * Ctor for the engine alert
    * @param aProcessID id of the process that generated the alert
    * @param aEventID type of alert
    * @param aProcessName qname of the process that generated the alert 
    * @param aLocation location of the activity within the process that generated the alert
    * @param aFaultName optional qname of the fault
    * @param aDetails option details related to the alert - possibly variable data
    */
   public AeEngineAlert(long aProcessID, int aEventID, QName aProcessName, String aLocation, QName aFaultName, Document aDetails)
   {
      super(aProcessID, aEventID, aProcessName);
      setFaultName(aFaultName);
      setDetails(aDetails);
      setLocation(aLocation);
   }

   /**
    * Ctor for the engine alert
    * @param aProcessID id of the process that generated the alert
    * @param aEventID type of alert
    * @param aProcessName qname of the process that generated the alert 
    * @param aLocation location of the activity within the process that generated the alert
    * @param aFaultName optional qname of the fault
    * @param aDetails option details related to the alert - possibly variable data
    * @param aTimestamp the timestamp of the event/alert
    */
   public AeEngineAlert(long aProcessID, int aEventID, QName aProcessName, String aLocation,
         QName aFaultName, Document aDetails, Date aTimestamp)
   {
      super(aProcessID, aEventID, aProcessName, aTimestamp);
      setFaultName(aFaultName);
      setDetails(aDetails);
      setLocation(aLocation);
   }
   
   /**
    * @return Returns the faultName.
    */
   public QName getFaultName()
   {
      return mFaultName;
   }

   /**
    * @param aFaultName The faultName to set.
    */
   public void setFaultName(QName aFaultName)
   {
      mFaultName = aFaultName;
   }



   /**
    * setter for the details
    * @param aDetails
    */
   protected void setDetails(Document aDetails)
   {
      mDetails = aDetails;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeEngineAlert#getDetails()
    */
   public Document getDetails()
   {
      return mDetails;
   }
   
   /**
    * Setter for the location
    * @param aLocation
    */
   protected void setLocation(String aLocation)
   {
      mLocation = aLocation;
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeEngineAlert#getLocation()
    */
   public String getLocation()
   {
      return mLocation;
   }
}
 