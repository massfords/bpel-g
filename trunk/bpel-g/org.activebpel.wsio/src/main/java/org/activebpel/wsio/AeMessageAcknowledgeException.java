//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/AeMessageAcknowledgeException.java,v 1.1 2006/05/25 00:06:54 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio;

/**
 * Exception related to durable invoke and reply acknowledgement.
 */
public class AeMessageAcknowledgeException extends Exception
{

   /**
    * Constructs a exception.
    */
   public AeMessageAcknowledgeException()
   {
      super();
   }

   /**
    * Creates an exception given the exception message.
    * @param aMessage
    */
   public AeMessageAcknowledgeException(String aMessage)
   {
      super(aMessage);
   }

   /**
    * Creates an exception given the root cause.
    * @param aCause
    */
   public AeMessageAcknowledgeException(Throwable aCause)
   {
      super(aCause);
   }

   /**
    * Creates an exception given the root cause and the message.
    * @param aMessage
    * @param aCause
    */
   public AeMessageAcknowledgeException(String aMessage, Throwable aCause)
   {
      super(aMessage, aCause);
   }

}
