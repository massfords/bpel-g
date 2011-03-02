//$Header$
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def; 


/**
 * Parents of 'messageExchanges' constructs must implement this interface.
 */
public interface IAeMessageExchangesParentDef
{
   /**
    * Gets the message exchanges def.
    */
   public AeMessageExchangesDef getMessageExchangesDef();

   /**
    * Sets the message exchanges def.
    * 
    * @param aDef
    */
   public void setMessageExchangesDef(AeMessageExchangesDef aDef);
}
 
