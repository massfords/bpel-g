//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/handler/AeHttpPutHandler.java,v 1.4 2008/03/26 15:43:27 jbik Exp $
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
import org.apache.commons.httpclient.methods.PutMethod;

/**
 * An Http PUT handler
 */
public class AeHttpPutHandler extends AeHttpSendHandlerBase
{

   /**
    * Constructor
    * @param aSuccessor the next handler of the chain
    */
   public AeHttpPutHandler(AeHttpSendHandlerBase aSuccessor)
   {
      super(aSuccessor);
   }

   /**
    * @see org.activebpel.rt.http.handler.IAeHttpHandler#handle()
    */
   public AeHttpResponse handle() throws AeException
   {

      String url = formUrl(getHttpRequest().getURI(), getHttpRequest().getUrlParams(), true);
      PutMethod method = new PutMethod(url);

      return prepareAndDeliver(method);
   }

   /**
    * @param method
    * @return
    * @throws AeException
    */
   private AeHttpResponse prepareAndDeliver(PutMethod method) throws AeException
   {
      // set body to send
      method.setRequestEntity(getHttpRequest().createRequestBody());

      return deliver((HttpMethod)method);
   }

   /**
    * @see org.activebpel.rt.http.handler.IAeHttpHandler#canHandle(org.activebpel.rt.http.AeHttpRequest)
    */
   public boolean canHandle(AeHttpRequest aRequest)
   {
      return aRequest.isPut();
   }

   /**
    * @see org.activebpel.rt.http.AeHttpSendHandlerBase#redirect(org.apache.commons.httpclient.HttpMethod)
    */
   protected AeHttpResponse redirect(HttpMethod aMethod) throws AeException
   {
      return prepareAndDeliver((PutMethod)aMethod);
   }

}
