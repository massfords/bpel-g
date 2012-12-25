//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/attachment/AeAttachmentBeanBase.java,v 1.1 2007/08/13 19:36:34 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview.attachment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activebpel.rt.bpeladmin.war.web.AeAbstractAdminBean;
import org.activebpel.rt.util.AeUtil;

/**
 * Base class for the attachment JSP beans.
 */
public abstract class AeAttachmentBeanBase extends AeAbstractAdminBean
{
   /**
    * Request or command was successful.
    */
   public static final int SUCCESS = 0;

   /** Command failed. */
   public static final int FAILURE = 1;

   /** Error */
   public static final int ERROR = 2;

   /** Command ignored */
   public static final int IGNORE = 3;

   /** The process id. */
   private String mPid;

   /** The (server compatible) location path. */
   private String mPath;

   /** Response status code */
   private int mStatusCode = SUCCESS;

   /** Http servlet request. */
   private HttpServletRequest mRequest;

   /** Http servlet response. */
   private HttpServletResponse mResponse;

   /**
    * Set the process id
    * @param aPid
    */
   public void setPid(String aPid)
   {
      mPid = aPid;
   }

   /**
    * Return the pid as a long.
    */
   public long getPidAsLong()
   {
      return Long.parseLong(getPid());
   }

   /**
    * @return Returns the pid.
    */
   public String getPid()
   {
      return mPid;
   }

   /**
    * Set the variable locator path
    * @param aPath
    */
   public void setPath(String aPath)
   {
      mPath = aPath;
   }

   /**
    * @return Returns the locationPath.
    */
   public String getPath()
   {
      return mPath;
   }

   /**
    * @return Returns the status code.
    */
   public int getStatusCode()
   {
      return mStatusCode;
   }

   /**
    * Sets the response status code.
    * @param aStatusCode
    */
   public void setStatusCode(int aStatusCode)
   {
      mStatusCode = aStatusCode;
   }

   /**
    * @return the error
    */
   public boolean isError()
   {
      return getStatusCode() == ERROR;
   }

   /**
    * Sets the error message from the throwable.
    * @param aError
    */
   public void setError(Throwable aError)
   {
      setStatusCode(ERROR);
      setStatusDetail(aError);
      setErrorDetail(true);
   }

   /**
    * Convenience method to return the message from the root cause of the error.
    * @param aError
    * @return root cause message.
    */
   protected String createExceptionMessage(Throwable aError)
   {
      if ( aError.getCause() != null && AeUtil.notNullOrEmpty(aError.getCause().getMessage()) )
      {
         return aError.getCause().getMessage();
      }
      else
      {
         return aError.getMessage();
      }
   }

   /**
    * Sets the message from the throwable.
    * @param aError
    */
   public void setStatusDetail(Throwable aError)
   {
      setStatusDetail(createExceptionMessage(aError));
   }

   /**
    * Called by the JSP set the reques object.
    * @param aRequest
    * @throws Exception
    */
   public void setRequest(HttpServletRequest aRequest) throws Exception
   {
      mRequest = aRequest;
   }

   /**
    * Called by the JSP set the response object.
    * @param aResponse
    */
   public void setResponse(HttpServletResponse aResponse)
   {
      mResponse = aResponse;
   }

   /**
    * @return Http servlet request if set or <code>null</code> otherwise.
    */
   protected HttpServletRequest getRequest()
   {
      return mRequest;
   }

   /**
    * @return Http servlet response if set or <code>null</code> otherwise.
    */
   protected HttpServletResponse getResponse()
   {
      return mResponse;
   }

}
