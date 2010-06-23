// $Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/invoke/IAeInvoke.java,v 1.13 2008/03/11 03:15:38 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio.invoke;

import java.io.Serializable;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.wsio.IAeWebServiceMessageData;

/**
 * Wraps the invoke data.
 */
public interface IAeInvoke extends Serializable, IAeTransmission
{
   /**
    * Accessor for the process id.
    */
   public long getProcessId();

   /**
    * Accessor for the process <code>QName</code>.
    */
   public QName getProcessName();

   /**
    * Accessor for the partner link (may be either a simple PL name or full location).
    */
   public String getPartnerLink();

   /**
    * Accessor for the partner endpoint reference.
    */
   public String getPartnerEndpointReferenceString();

   /***
    * Accessor for my endpoint reference.
    */
   public String getMyEndpointReferenceString();

   /**
    * Accessor for the operation.
    */
   public String getOperation();

   /**
    * Accessor for the input message data.
    */
   public IAeWebServiceMessageData getInputMessageData();

   /**
    * Return true if this is a one way invoke.
    */
   public boolean isOneWay();

   /**
    * Accessor for the location path.
    */
   public String getLocationPath();

   /**
    * Accessor for the location id.
    */
   public int getLocationId();
   
   /**
    * Return the custom invoker uri string or null if none was specified.
    * @deprecated use getInvokeHandler
    */
   public String getCustomInvokerUri();

   /**
    * Setter for the invoke handler
    *
    * @param aHandler
    */
   public void setInvokeHandler(String aHandler);

   /**
    * Returns the uri for the invoke handler or null if none defined
    */
   public String getInvokeHandler();

   /**
    * Gets the port type for the invoke.
    */
   public QName getPortType();

   /**
    * The <code>Map</code> of (string) name/value pairs that will be sent to the
    * business process instance when the invoke is executed.
    */
   public Map getBusinessProcessProperties();
   
   /**
    * Gets the name of the principal that initiated the process
    */
   public String getProcessInitiator();
}
