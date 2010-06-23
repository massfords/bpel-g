//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/invoke/AeInvokePrepareException.java,v 1.1 2006/05/25 00:06:54 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio.invoke;

/**
 * Exception that is thrown during <code>IAeTwoPhaseInvokeHandler::prepare</code> method.
 */
public class AeInvokePrepareException extends Exception
{

   /**
    * Default constructor. 
    */
   public AeInvokePrepareException()
   {
   }

   /**
    * Creates exception given messsage. 
    * @param aMessage
    */
   public AeInvokePrepareException(String aMessage)
   {
      super(aMessage);
   }

   /**
    * Creates exception given root cause.
    * @param aCause
    */
   public AeInvokePrepareException(Throwable aCause)
   {
      super(aCause);
   }

   /**
    * Creates the exception given message and root cause.
    * @param aMessage
    * @param aCause
    */
   public AeInvokePrepareException(String aMessage, Throwable aCause)
   {
      super(aMessage, aCause);
   }

}
