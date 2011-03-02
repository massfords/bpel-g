//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/IAeWebServiceMessageData.java,v 1.3 2007/05/01 16:59:40 KRoe Exp $
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
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Wraps any message data returned from the invoke call.
 */
public interface IAeWebServiceMessageData extends Serializable
{
   /**
    * Returns the type of message this data is representing. 
    */
   public QName getMessageType();
   
   /**
    * Get the message part data.
    */
   public Map getMessageData();
   
   /**
    * Get the message attachments.
    */
   public List getAttachments();
}
