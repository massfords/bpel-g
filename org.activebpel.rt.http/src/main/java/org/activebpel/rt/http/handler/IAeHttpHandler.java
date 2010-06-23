//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/handler/IAeHttpHandler.java,v 1.1 2008/01/24 17:49:33 jbik Exp $
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

/**
 * Interface implemented by http method handlers
 */
public interface IAeHttpHandler
{
   /**
    * Handle the delivery of the http request
    * @return the response data from the http peer
    * @throws AeException
    */
   public AeHttpResponse handle() throws AeException;
   
   /**
    * @param aRequest 
    * @return true - the handler can handle the request; false - not responsible for the request
    */
   public boolean canHandle(AeHttpRequest aRequest);
}
