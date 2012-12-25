// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/graph/IAeGraphImageResponse.java,v 1.2 2007/04/11 17:54:59 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.graph;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Defines interface for sending the response from an instance of
 * {@link AeGraphImageDriver}.
 */
public interface IAeGraphImageResponse
{
   /**
    * Adds a header to the response.
    *
    * @param aHeaderName
    * @param aHeaderValue
    */
   public void addHeader(String aHeaderName, String aHeaderValue);

   /**
    * Returns the response output stream.
    */
   public OutputStream getOutputStream() throws IOException;

   /**
    * Sets the response content type.
    *
    * @param aContentType
    */
   public void setContentType(String aContentType);
}
