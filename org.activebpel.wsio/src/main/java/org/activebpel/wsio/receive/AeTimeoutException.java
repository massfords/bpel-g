//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/receive/AeTimeoutException.java,v 1.3 2008/02/21 19:56:39 RNaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio.receive; 

/**
 * Signals that the request to the engine timed out before a response was received. 
 */
public class AeTimeoutException extends AeRequestException
{
   /**
    * Creates the exception with the specified message.
    * 
    * @param aMessage
    */
   public AeTimeoutException(String aMessage)
   {
      super(aMessage);
   }
}
