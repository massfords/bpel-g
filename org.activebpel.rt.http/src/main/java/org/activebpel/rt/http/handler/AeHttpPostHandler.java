//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/handler/AeHttpPostHandler.java,v 1.4.2.1 2008/04/21 16:15:52 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.http.handler;

import org.activebpel.rt.AeException;
import org.activebpel.rt.http.AeHttpRequest;
import org.activebpel.rt.http.AeHttpResponse;
import org.activebpel.rt.http.AeHttpSendHandlerBase;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

/**
 * An Http POST handler supports multipart posts as defined in section 3.3 of 
 * <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC1867</a>
 * @see AeHttpRequest
 */
public class AeHttpPostHandler extends AeHttpSendHandlerBase
{

   /**
    * Constructor
    * @param aSuccessor the next handler of the chain
    */
   public AeHttpPostHandler(AeHttpSendHandlerBase aSuccessor)
   {
      super(aSuccessor);
   }

   /**
    * @see org.activebpel.rt.http.handler.IAeHttpHandler#handle()
    */
   public AeHttpResponse handle() throws AeException
   {

      PostMethod method = new PostMethod(getHttpRequest().getURI());
     
      return prepareAndDeliver(method);
   }

   /**
    * @param method
    * @return
    * @throws AeException
    */
   private AeHttpResponse prepareAndDeliver(PostMethod method) throws AeException
   {
      // set optional body with parameters or Multipart body to send.
      // Note: when there is body content it takes precedence over the parameters as body content
      RequestEntity bodyContent = getHttpRequest().createPostRequest(method);
      if ( bodyContent != null )
         method.setRequestEntity(bodyContent);

      return deliver(method);
   }

   /**
    * @see org.activebpel.rt.http.handler.IAeHttpHandler#canHandle(org.activebpel.rt.http.AeHttpRequest)
    */
   public boolean canHandle(AeHttpRequest aRequest)
   {
      return aRequest.isPost();
   }

   /**
    * @see org.activebpel.rt.http.AeHttpSendHandlerBase#redirect(org.apache.commons.httpclient.HttpMethod)
    */
   protected AeHttpResponse redirect(HttpMethod aMethod) throws AeException
   {
      return prepareAndDeliver((PostMethod)aMethod);
   }

}
