// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/fastdom/AeIllegalAddException.java,v 1.1 2004/09/07 22:08:24 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.fastdom;

/**
 * Implements the exception that reports violations when adding a node to the
 * fast, lightweight DOM.
 */
public class AeIllegalAddException extends RuntimeException
{
   /**
    * Default constructor.
    */
   public AeIllegalAddException()
   {
      super();
   }

   /**
    * Constructs an exception with the specified message.
    *
    * @param aMessage
    */
   public AeIllegalAddException(String aMessage)
   {
      super(aMessage);
   }

   /**
    * Constructs an exception with the specified message and root cause.
    *
    * @param aMessage
    * @param aCause
    */
   public AeIllegalAddException(String aMessage, Throwable aCause)
   {
      super(aMessage, aCause);
   }
}
