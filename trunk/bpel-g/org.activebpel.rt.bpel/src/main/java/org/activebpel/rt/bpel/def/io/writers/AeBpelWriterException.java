// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/writers/AeBpelWriterException.java,v 1.2 2004/07/08 13:09:52 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.writers;

/**
 * Generic exception for BPEL serialization.
 */
public class AeBpelWriterException extends RuntimeException
{
   /**
    * @param message
    */
   public AeBpelWriterException(String message)
   {
      super(message);
   }

   /**
    * @param message
    * @param cause
    */
   public AeBpelWriterException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
