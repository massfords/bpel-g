// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/AeBusinessProcessException.java,v 1.6 2008/03/23 01:40:10 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import org.activebpel.rt.AeException;

/**
 * Describes the standard exception thrown by business process layer
 */
public class AeBusinessProcessException extends AeException
{
   /**
    * Default constructor. 
    */
   public AeBusinessProcessException()
   {
      super();
   }

   /**
    * Construct a new business process exception with the passed info string.
    * @see java.lang.Throwable#Throwable(String)
    */
   public AeBusinessProcessException(String aInfo)
   {
      super(aInfo);
   }

   /**
    * Construct with a root exception (used primarily for rethrowing an underlying exception).
    * @param aInfo
    * @param aRootCause
    */
   public AeBusinessProcessException(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
   }
}
