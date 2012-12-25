//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/search/AeIdentitySearchException.java,v 1.2 2007/04/16 17:09:18 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.search;

import org.activebpel.rt.identity.AeIdentityException;

/**
 * Exceptions related to identity searches.
 */
public class AeIdentitySearchException extends AeIdentityException
{
   /** Search result not found. */
   public static final int NOT_FOUND_EXCEPTION = 10;
   
   /**
    * Default ctor.
    */
   public AeIdentitySearchException()
   {
   }

   /**
    * Ctor
    * @param aInfo
    */
   public AeIdentitySearchException(String aInfo)
   {
      super(aInfo);
   }

   /**
    * Ctor.
    * @param aRootCause
    */
   public AeIdentitySearchException(Throwable aRootCause)
   {
      super(aRootCause);
   }

   /**
    * Ctor
    * @param aInfo
    * @param aRootCause
    */
   public AeIdentitySearchException(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
   }
   
   /**
    * Ctor with given error code and message.
    * @param aCode
    * @param aInfo
    */
   public AeIdentitySearchException(int aCode, String aInfo)
   {
      super(aCode, aInfo);
   }   
   
   /**
    * Ctor.
    * @param aCode
    * @param aInfo
    * @param aRootCause
    */
   public AeIdentitySearchException(int aCode, String aInfo, Throwable aRootCause)
   {
      super(aCode, aInfo, aRootCause);
   }   
}
