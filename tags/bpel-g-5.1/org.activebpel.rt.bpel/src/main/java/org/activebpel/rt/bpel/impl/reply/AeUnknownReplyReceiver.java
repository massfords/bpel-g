//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/reply/AeUnknownReplyReceiver.java,v 1.1 2006/05/24 23:07:01 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.reply;

import org.activebpel.rt.bpel.AeBusinessProcessException;

/**
 * Exception that is used to indicate that a <code>IAeReplyReceiver</code>
 * implementation for a given durable reply type was not found.
 */
public class AeUnknownReplyReceiver extends AeBusinessProcessException
{

   /**
    * Default ctor.
    */
   public AeUnknownReplyReceiver()
   {
      super();
   }

   /**
    * Constructs the exception given the reply type.
    * @param aInfo
    */
   public AeUnknownReplyReceiver(String aInfo)
   {
      super(aInfo);
   }

   /**
    * 
    * @param aInfo
    * @param aRootCause
    */
   public AeUnknownReplyReceiver(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
   }
}
