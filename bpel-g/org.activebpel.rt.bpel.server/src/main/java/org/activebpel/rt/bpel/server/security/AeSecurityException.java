//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/security/AeSecurityException.java,v 1.1 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.security;

import org.activebpel.rt.AeException;

/**
 * Extension of AeException for Security exceptions
 */
public class AeSecurityException extends AeException
{

   /**
    * No arg constructor
    *
    */
   public AeSecurityException()
   {

   }

   /**
    * Constructor with message
    * 
    * @param aInfo
    */
   public AeSecurityException(String aInfo)
   {
      super(aInfo);
   }

   /**
    * Constructor with root cause
    * 
    * @param aRootCause
    */
   public AeSecurityException(Throwable aRootCause)
   {
      super(aRootCause);
   }

   /**
    * Constructor with message and root cause
    * 
    * @param aInfo
    * @param aRootCause
    */
   public AeSecurityException(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
   }

}
