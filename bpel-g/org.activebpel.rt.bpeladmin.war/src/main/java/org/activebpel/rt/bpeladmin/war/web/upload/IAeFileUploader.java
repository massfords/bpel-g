//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/upload/IAeFileUploader.java,v 1.1 2007/08/13 19:37:01 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface for client view of file uploads from the web perspective.
 */
public interface IAeFileUploader
{

   // constants for query string params
   public static final String THRESHOLD_BYTES = "threshold-size"; //$NON-NLS-1$

   public static final String MAX_SIZE = "max-size"; //$NON-NLS-1$

   /**
    * Return true if the request contains multipart content.
    * @param aRequest
    */
   public boolean isMultiPartContent( HttpServletRequest aRequest );
   
   /**
    * Handle the details of parsing the upload data from the request. 
    * @param aRequest
    * @param aResponse
    */
   public void uploadFile( HttpServletRequest aRequest, HttpServletResponse aResponse );
   
}
