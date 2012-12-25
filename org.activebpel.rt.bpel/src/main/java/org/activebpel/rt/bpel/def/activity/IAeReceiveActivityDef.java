//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/IAeReceiveActivityDef.java,v 1.4 2008/03/15 22:13:09 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity; 

import org.activebpel.rt.bpel.def.AeBaseDef;

/**
 * This method exposes some useful methods for determining if the receive 
 * (or onMessage) is a one-way or request-response. 
 */
public interface IAeReceiveActivityDef extends IAePartnerLinkActivityDef
{
   /**
    * Returns true if the receive is a one-way message that doesn't have a reply.
    */
   public boolean isOneWay();
   
   /**
    * Set by the visitor during the creation of the process def after its determined
    * if the receive is a one way or request-response.
    * 
    * @param aFlag
    */
   public void setOneWay(boolean aFlag);
   
   /**
    * Getter for the message exchange
    */
   public String getMessageExchange();
   
   /**
    * Getter for the location path
    */
   public String getLocationPath();
   
   /**
    * Gets the context to use for resolving references to plinks, variables, and correlation sets
    */
   public AeBaseDef getContext();
   
   /** 
    * Returns a human readable string describing the type of receive activity for this instance. 
    * Used for reporting errors.
    */
   public String getTypeDisplayText();
   
}
 