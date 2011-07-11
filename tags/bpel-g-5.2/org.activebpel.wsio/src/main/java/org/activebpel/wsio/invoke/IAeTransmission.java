//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/invoke/IAeTransmission.java,v 1.1 2008/03/11 03:15:38 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio.invoke; 

/**
 * Interface for messages that will be transmitted through the engine's transmission
 * tracker.  
 */
public interface IAeTransmission
{
   /** 
    * Returns the transmission id used in durable invokes. If id is not available,
    * this method returns 0.
    * @return transmission journal id if available.
    */
   public long getTransmissionId();
   
   /**
    * Called by durable invoke handlers when the invoke has been assigned a transmission id
    * and journaled. 
    * @param aTransimissionId unique identifier for invokes outbound message (transmission).
    */
   public void setTransmissionId(long aTransimissionId);
}
 