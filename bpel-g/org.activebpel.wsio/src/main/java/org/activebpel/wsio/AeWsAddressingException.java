//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/AeWsAddressingException.java,v 1.1 2006/08/08 16:37:50 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio;

/**
 * Exception thrown by addressing layer when a set of headers
 * violates the rules set by WS-Addressing  
 */
public class AeWsAddressingException extends Exception
{

   /**
    * Default Constructor
    *
    */
   public AeWsAddressingException()
   {
      super();
   }

   /**
    * Constructor with additional message text
    * @param aMessage
    */
   public AeWsAddressingException(String aMessage)
   {
      super(aMessage);
   }

   /**
    * Constructor with additional message text and root cause
    * @param aMessage
    * @param aCause
    */
   public AeWsAddressingException(String aMessage, Throwable aCause)
   {
      super(aMessage, aCause);
   }

   /**
    * Constructor with a root cause only
    * @param aCause
    */
   public AeWsAddressingException(Throwable aCause)
   {
      super(aCause);
   }

}
