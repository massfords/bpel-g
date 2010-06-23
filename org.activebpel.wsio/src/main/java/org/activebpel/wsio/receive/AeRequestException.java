//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/receive/AeRequestException.java,v 1.1 2005/01/12 22:05:22 mford Exp $
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
 * Base class for exceptions that occur when sending a message to the engine.
 */
public class AeRequestException extends Exception
{
   /**
    * No-arg ctor 
    */
   public AeRequestException()
   {
      
   }
   
   /**
    * Creates the exception with the specified message.
    * 
    * @param aMessage
    */
   public AeRequestException(String aMessage)
   {
      super(aMessage);
   }
}
 