//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/IAeWSIOActivity.java,v 1.3 2007/11/15 02:24:40 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeMessageValidator;
import org.activebpel.rt.bpel.impl.activity.support.IAeCorrelations;

/**
 * interface for the web service in/out activities: receive, reply, invoke, onMessage, onEvent
 */
public interface IAeWSIOActivity extends IAeBpelObject
{
   /**
    * Setter for the message validator
    * @param aValidator
    */
   public void setMessageValidator(IAeMessageValidator aValidator);
   
   /**
    * Setter for the request correlations
    * @param aCorrelations
    */
   public void setRequestCorrelations(IAeCorrelations aCorrelations);
   
   /**
    * Setter for the response correlations
    * @param aCorrelations
    */
   public void setResponseCorrelations(IAeCorrelations aCorrelations);
}
 