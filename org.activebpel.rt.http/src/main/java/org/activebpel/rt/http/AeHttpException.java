//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/AeHttpException.java,v 1.2 2008/03/26 15:41:36 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.http;

import org.activebpel.rt.AeException;

/**
 * Base class for Http service related exceptions.
 */
public class AeHttpException extends AeException
{
   /** Http Status code. */
   private int mCode;
   
   /** The http method name */
   private String mMethodName;
   
   /** Http Status message */
   private String mStatusMessage;
   
   /**
    * Default ctor.
    */
   public AeHttpException()
   {
   }
   
   /**
    * Creates AeRestException with the given message.
    * @param aInfo
    */
   public AeHttpException(int aCode,String aMethodName,String aStatusMessage, String aInfo)
   {
      super(aInfo);
      mCode = aCode;
      mMethodName = aMethodName;
      mStatusMessage = aStatusMessage;
   }
   
   /**
    * Creates AeRestException with the given message.
    * @param aInfo
    */
   public AeHttpException(String aInfo)
   {
      super(aInfo);
   }

   /**
    * Creates AeHttpException with the given root cause.
    * @param aRootCause
    */   
   public AeHttpException(Throwable aRootCause)
   {
      super(aRootCause);
   }

   /**
    * Creates AeHttpException with the given message and root cause.
    * @param aInfo
    * @param aRootCause
    */
   public AeHttpException(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
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
    * @return the statusMessage
    */
   public String getStatusMessage()
   {
      return mStatusMessage;
   }

   /**
    * @return the methodName
    */
   public String getMethodName()
   {
      return mMethodName;
   }

   
   
}
