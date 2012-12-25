//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/IAeHttpServiceManager.java,v 1.2 2008/03/26 15:43:08 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.http;

import org.activebpel.rt.AeException;

/**
 * Interface for AeHttpServiceManger
 */
public interface IAeHttpServiceManager
{

   /**
    * Send the request to an Http service
    * @param aRequest
    * @return the http transmission response data
    * @throws AeException
    */
   public AeHttpResponse send(AeHttpRequest aRequest) throws AeException;
   
   /**
    * @return the engine configuration for the HTTP transport
    * @throws AeException
    */
   public AeHttpConfig getHttpConfig() throws AeException;
}
