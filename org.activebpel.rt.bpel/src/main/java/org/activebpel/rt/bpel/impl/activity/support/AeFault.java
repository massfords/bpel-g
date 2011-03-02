// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeFault.java,v 1.16 2006/10/17 21:23:08 tzhang Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Models BPEL fault objects. These faults can be generated through the
 * execution of an <code>invoke</code>, <code>throw</code>, or through a BPEL
 * error like a <code>bpws:correlationViolation</code> (among others).
 */
public class AeFault implements IAeFault
{
   /** fault name */
   private QName mName;
   /** fault message data */
   private IAeMessageData mMessageData;
   /** fault element data. */
   private Element mElementData;
   /** fault source */
   private IAeBpelObject mSource ;
   /** fault info, from exception, if any */
   private String mInfo = ""; //$NON-NLS-1$
   /** stacktrace from exception, if any */
   private String mDetailedInfo;
   /** return true if this fault has not caused a process to suspend */
   private boolean mSuspendable = true;
   /** return true if this fault is rethrowable */
   private boolean mRethrowable = true;

   /**
    * Ctor takes name and variable (as message data).
    * @param aName
    * @param aMessageData can be null if name is supplied
    */
   public AeFault(QName aName, IAeMessageData aMessageData)
   {
      mName = aName;
      mMessageData = aMessageData;
   }
   
   /**
    * Constructor that takes the name and the variable (as Element data).
    * 
    * @param aName
    * @param aElementData
    */
   public AeFault(QName aName, Element aElementData)
   {
      mName = aName;
      setElementData(aElementData);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeFaultTypeInfo#getElementType()
    */
   public QName getElementType()
   {
      if (hasElementData())
      {
         return AeXmlUtil.getElementType(getElementData());
      }
      return null;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeFaultTypeInfo#getMessageType()
    */
   public QName getMessageType()
   {
      if (hasMessageData())
      {
         return getMessageData().getMessageType();
      }
      return null;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeFaultTypeInfo#getSinglePartElementType()
    */
   public QName getSinglePartElementType()
   {
      if (hasMessageData())
      {
         IAeMessageData messageData = getMessageData();
         if (messageData.getPartCount() == 1)
         {
            Object data = messageData.getData((String) messageData.getPartNames().next());
            if (data instanceof Document)
            {
               Element e = ((Document)data).getDocumentElement();
               if (e != null)
               {
                  return AeXmlUtil.getElementType(e);
               }
            }
         }
      }
      return null;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeFaultTypeInfo#getFaultName()
    */
   public QName getFaultName()
   {
      return mName;
   }

   /**
    * Getter for the message
    * @return IAeMessageData or null
    */
   public IAeMessageData getMessageData()
   {
      return mMessageData;
   }

   /**
    * Returns true if the fault contains message data.
    */
   public boolean hasMessageData()
   {
      return getMessageData() != null;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeFault#isRethrowable()
    */
   public boolean isRethrowable()
   {
      return mRethrowable;
   }

   /**
    * @param aRethrowable The rethrowable to set.
    */
   public void setRethrowable(boolean aRethrowable)
   {
      mRethrowable = aRethrowable;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeFault#getSource()
    */
   public IAeBpelObject getSource()
   {
      return mSource ;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeFault#setSource(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void setSource(IAeBpelObject aSource)
   {
      mSource = aSource ;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeFault#getInfo()
    */
   public String getInfo()
   {
      return mInfo ;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeFault#setInfo(java.lang.String)
    */
   public void setInfo(String aInfo)
   {
      mInfo = aInfo ;
   }


   /**
    * @see org.activebpel.rt.bpel.IAeFault#getDetailedInfo()
    */
   public String getDetailedInfo()
   {
      return mDetailedInfo;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeFault#setDetailedInfo(java.lang.String)
    */
   public void setDetailedInfo(String aDetailedInfo)
   {
      mDetailedInfo = aDetailedInfo;
   }


   /**
    * @see org.activebpel.rt.bpel.IAeFault#isSuspendable()
    */
   public boolean isSuspendable()
   {
      return mSuspendable;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeFault#setSuspendable(boolean)
    */
   public void setSuspendable(boolean aValue)
   {
      mSuspendable = aValue;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeFault#getElementData()
    */
   public Element getElementData()
   {
      return mElementData;
   }

   /**
    * @param aElementData The elementData to set.
    */
   protected void setElementData(Element aElementData)
   {
      mElementData = aElementData;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeFault#hasElementData()
    */
   public boolean hasElementData()
   {
      return getElementData() != null;
   }
   

   /**
    * Return true if there is any data in this fault.
    * @see org.activebpel.rt.bpel.IAeFaultTypeInfo#hasData()
    */
   public boolean hasData()
   {
      return hasElementData() || hasMessageData();
   }
}
