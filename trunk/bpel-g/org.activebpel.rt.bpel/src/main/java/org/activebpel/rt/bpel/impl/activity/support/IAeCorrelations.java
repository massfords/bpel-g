//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/IAeCorrelations.java,v 1.4 2006/10/26 14:01:36 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support;

import java.util.Iterator;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Interface that encapsulates the initiation or validation of an activity's
 * correlations from a message.
 */
public interface IAeCorrelations
{
   /**
    * Initiates or validates the correlation sets with the message data
    * @param aData the data for the message
    * @param aMessagePartsMap the definition of the message parts
    * @throws AeBusinessProcessException
    */
   public void initiateOrValidate(IAeMessageData aData, AeMessagePartsMap aMessagePartsMap)
         throws AeBusinessProcessException;
   
   /**
    * Getter for the correlation defs
    */
   public Iterator getCorrelationDefs();
}