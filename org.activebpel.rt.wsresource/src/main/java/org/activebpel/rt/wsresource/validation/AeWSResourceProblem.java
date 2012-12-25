//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/AeWSResourceProblem.java,v 1.1 2008/01/28 18:35:21 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsresource.validation;

import javax.xml.namespace.QName;

/**
 * Represents a single web service resource validation problem.
 */
public class AeWSResourceProblem
{
   /** The problem id. */
   private QName mId;
   /** The problem severity. */
   private int mSeverity;
   /** The message. */
   private String mMessage;
   /** XPath pointing to the XML node that caused the problem. */
   private String mXPath;
   /** Line number of the problem (mutually exclusive with mXPath). */
   private int mLineNumber;

   /**
    * C'tor.
    *
    * @param aId
    * @param aSeverity
    * @param aMessage
    * @param aXPath
    */
   public AeWSResourceProblem(QName aId, int aSeverity, String aMessage, String aXPath)
   {
      setId(aId);
      setSeverity(aSeverity);
      setMessage(aMessage);
      setXPath(aXPath);
   }

   /**
    * C'tor.
    *
    * @param aId
    * @param aSeverity
    * @param aMessage
    * @param aLineNumber
    */
   public AeWSResourceProblem(QName aId, int aSeverity, String aMessage, int aLineNumber)
   {
      setId(aId);
      setSeverity(aSeverity);
      setMessage(aMessage);
      setLineNumber(aLineNumber);
   }

   /**
    * @return Returns the message.
    */
   public String getMessage()
   {
      return mMessage;
   }

   /**
    * @param aMessage the message to set
    */
   protected void setMessage(String aMessage)
   {
      mMessage = aMessage;
   }

   /**
    * @return Returns the severity.
    */
   public int getSeverity()
   {
      return mSeverity;
   }

   /**
    * @param aSeverity the severity to set
    */
   protected void setSeverity(int aSeverity)
   {
      mSeverity = aSeverity;
   }

   /**
    * @return Returns the xPath.
    */
   public String getXPath()
   {
      return mXPath;
   }

   /**
    * @param aPath the xPath to set
    */
   protected void setXPath(String aPath)
   {
      mXPath = aPath;
   }

   /**
    * @return Returns the lineNumber.
    */
   public int getLineNumber()
   {
      return mLineNumber;
   }

   /**
    * @param aLineNumber the lineNumber to set
    */
   protected void setLineNumber(int aLineNumber)
   {
      mLineNumber = aLineNumber;
   }

   /**
    * Returns true if an xpath is set.
    */
   public boolean hasXPath()
   {
      return getXPath() != null;
   }

   /**
    * @return Returns the id.
    */
   public QName getId()
   {
      return mId;
   }

   /**
    * @param aId the id to set
    */
   protected void setId(QName aId)
   {
      mId = aId;
   }
}
