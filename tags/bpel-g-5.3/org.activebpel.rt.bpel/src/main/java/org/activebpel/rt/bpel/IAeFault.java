// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeFault.java,v 1.11 2006/09/11 23:06:28 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;


import java.io.Serializable;

import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.message.IAeMessageData;
import org.w3c.dom.Element;

/**
 * Interface describing a fault which can be the result of an operation or runtime error.
 * This interface also includes runtime information like the source of the fault as well
 * as optional error messages or stacktrace.
 */
public interface IAeFault extends IAeFaultTypeInfo, Serializable
{
   /**
    * Set the source of the fault.
    * @param aSource
    */
   public void setSource( IAeBpelObject aSource );

   /**
    * Getter for the source of the fault.
    *
    * @return IAeBpelObject
    */
   public IAeBpelObject getSource();

   /**
    * Getter for the message
    * @return IAeMessageData or null
    */
   public IAeMessageData getMessageData();

   /**
    * Getter for the element data.
    */
   public Element getElementData();

   /**
    * Returns true if the fault is rethrowable.
    */
   public boolean isRethrowable();

   /**
    * Set the info of the fault.
    * @param aInfo
    */
   public void setInfo( String aInfo );

   /**
    * Returns the info / msg string of the exception that caused this fault.
    */
   public String getInfo();

   /**
    * Gets detailed info relating to the fault. This might include a stacktrace or other
    * useful debugging information.
    */
   public String getDetailedInfo();

   /**
    * Sets detailed info relating to the fault.
    *
    * @param aDetailInfo
    */
   public void setDetailedInfo(String aDetailInfo);

   /**
    * Return true if this fault can cause the process to be suspended.
    * By default, this value will return true.  If will only return false
    * if this fault has already caused the process to be suspended.
    */
   public boolean isSuspendable();

   /**
    * Set the suspendable flag to the given value.
    * @param aValue
    */
   public void setSuspendable( boolean aValue );
}
