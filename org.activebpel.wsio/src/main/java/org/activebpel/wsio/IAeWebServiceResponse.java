//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/IAeWebServiceResponse.java,v 1.6 2005/07/25 16:48:30 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio;

import java.io.Serializable;
import java.util.Map;

import javax.xml.namespace.QName;


/**
 * Wraps the invoke response data.
 */
public interface IAeWebServiceResponse extends Serializable
{
   /**
    * Accessor for the error code QName.
    */
   public QName getErrorCode();
   
   /**
    * Accessor for any message data.  This may be null.
    */
   public IAeWebServiceMessageData getMessageData();
   
   /**
    * Return true if the response wraps a fault.
    */
   public boolean isFaultResponse();
   
   /**
    * Returns an error message associated with the fault or null if there is none.
    */
   public String getErrorString();
   
   /**
    * Returns a stacktrace or other detailed information associated with the fault or null if there was none. 
    */
   public String getErrorDetail();
   
   /**
    * Return the <code>Map</code> of (string) name/value pairs from the
    * business process.
    */
   public Map getBusinessProcessProperties();
   
   /**
    * Flag that indicates that the actual response will be coming later. (Early Reply to the client)
    * @return Returns true if the response is early reply.
    */
   public boolean isEarlyReply();   
}
