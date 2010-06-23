//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/AeIdentityException.java,v 1.4 2007/06/11 20:03:58 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity;

import org.activebpel.rt.AeException;

/**
 * Base class for Identity service related exceptions.
 */
public class AeIdentityException extends AeException
{
   /** Search service is not yet initialized. */
   public static final int UNINITIALIZED_EXCEPTION = 1;
   
   /** Configuration related exceptions. */
   public static final int CONFIGURATION_EXCEPTION = 2;

   /** Error code. */
   private int mCode;
   
   /**
    * Default ctor.
    */
   public AeIdentityException()
   {
   }
   
   /**
    * Creates AeIdentityException with the given message.
    * @param aInfo
    */
   public AeIdentityException(String aInfo)
   {
      super(aInfo);
   }

   /**
    * Creates AeIdentityException with the given root cause.
    * @param aRootCause
    */   
   public AeIdentityException(Throwable aRootCause)
   {
      super(aRootCause);
   }

   /**
    * Creates AeIdentityException with the given message and root cause.
    * @param aInfo
    * @param aRootCause
    */
   public AeIdentityException(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
   }
   
   /**
    * Creates AeIdentityException with the given code and message.
    * @param aInfo
    */
   public AeIdentityException(int aCode, String aInfo)
   {
      super(aInfo);
      setCode(aCode);
   }
   
   /**
    * Creates AeIdentityException with the given error code, message and root cause.
    * @param aCode
    * @param aInfo
    * @param aRootCause
    */
   public AeIdentityException(int aCode, String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
      setCode(aCode);
   }   
   
   /**
    * @return the code
    */
   public int getCode()
   {
      return mCode;
   }
   
   /**
    * @param aCode the code to set
    */
   public void setCode(int aCode)
   {
      mCode = aCode;
   }
   
   /** 
    * @return Returns detailed error description based on the code and the error message.
    */
   public String getDetailedMessage()
   {
      if (getCode() == UNINITIALIZED_EXCEPTION)
      {
         return AeMessages.format("AeIdentityException.UNINITIALIZED_EXCEPTION", getInfo()); //$NON-NLS-1$
      }
      else if (getCode() == CONFIGURATION_EXCEPTION)
      {
         return AeMessages.format("AeIdentityException.CONFIGURATION_EXCEPTION", getInfo()); //$NON-NLS-1$
      }
      else
      {
         return getInfo();
      }
   }
}
