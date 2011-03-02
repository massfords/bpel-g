//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/receive/IAeExtendedMessageContext.java,v 1.2 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.receive;

import java.util.Map;

import javax.security.auth.Subject;

import org.activebpel.rt.bpel.impl.reply.IAeDurableReplyInfo;
import org.activebpel.wsio.receive.IAeMessageContext;
import org.w3c.dom.Element;

/**
 * Extended Message Context interface supporting the additional information required 
 * by our pluggable receive handlers such as those for SOAP messages, including WSRM
 */
public interface IAeExtendedMessageContext extends IAeMessageContext
{
   /**
    * @return the url request came in on, as determined by the inbound transport handler
    */
   public String getTransportUrl();
   
   /**
    * @return the encoding style (Encoded or Literal)
    */
   public String getEncodingStyle();
   
   /**
    * @return Element containing selected header elements mapped from the inbound message
    */
   public Element getMappedHeaders();
   
   /**
    * @return Information required to support creation of a durable reply for this request
    */
   public IAeDurableReplyInfo getDurableReplyInfo();
   
   /**
    * @param aKey
    * @return property object associated with the key
    */
   public Object getProperty(Object aKey);
   
   /**
    * @return map of properties
    */
   public Map getProperties();   
   
   /**
    * Add a reference property to be serialied as a SOAPHeaderElement
    * @param aRefProp
    */
   public void addReferenceProperty(Element aRefProp);

   /**
    * @return the subject containing the principals used to authorize a request
    */
   public Subject getSubject();
}
