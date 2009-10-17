// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/AeCatalogServlet.java,v 1.8 2008/02/21 18:14:06 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeUtil;

/**
 * Servlet designed to provide the WSDL which is part of a BPR deployment.
 */
public class AeCatalogServlet extends HttpServlet
{
   /**
    * Handle POST request for WSDL catalog entry. 
    * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   public void doPost(HttpServletRequest aRequest, HttpServletResponse aResponse)
       throws ServletException, IOException
   {
      doGet(aRequest, aResponse);
   }
   
   /**
    * Handle GET request for WSDL catalog entry. 
    * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   public void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException, IOException
   {
      String pathInfo = AeUtil.getSafeString(aRequest.getPathInfo()).trim();
      String requestURL = aRequest.getRequestURL().toString();
      String entry = AeUtil.getSafeString(aRequest.getParameter("location")).trim(); //$NON-NLS-1$
      boolean isWsdl= true;
      // first check to see if requested resource is specified as part of the query string using param name 'location'.
      if (entry.length() > 1)
      {
         // check entry for wsdl extension
         isWsdl = entry.toLowerCase().endsWith(AeWsdlImportFixup.WSDL_EXTENSION);

         // Build requestURL so that it is in the expected (legacy) format:
         // as http://host:port/active-bpel/catalog/project:/a/b/c
         
         // First get the current request url and strip out extra path info (if any)
         if (pathInfo.length() > 0 && requestURL.endsWith(pathInfo))
         {
            requestURL = requestURL.substring(0, requestURL.length() - aRequest.getPathInfo().length());
         }         
         // append entry info
         requestURL = requestURL + "/" + entry; //$NON-NLS-1$
      }
      else if (pathInfo.length() > 1)
      {
         // use entry based on extra path info.
         // Eg: http://host:8080/active-bpel/catalog/project:/a/b/c
         // PathInfo: /project:a/b/c
         // entry: project:/a/b/c (note this may have some url encoding)
         entry = pathInfo.substring(1);
         
         // check if this is has a wsdl extension
         isWsdl = entry.toLowerCase().endsWith(AeWsdlImportFixup.WSDL_EXTENSION);

         // put the :/ back in the entry back by using the wsdl trimmers replacement
         // entry = project:/a/b/c (any url encoding is removed)
         entry = AeWsdlImportFixup.decodeLocation(entry);
      }
      else
      {
         aResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, AeMessages.getString("AeCatalogServlet.1")); //$NON-NLS-1$
         return;         
      }      
      
      String output = null;
      try
      {
         if (isWsdl)
         {
            // get wsdl file
            output = AeCatalogHelper.getCatalogWsdl(entry, requestURL);
         }
         else
         {
            // all others: schema/xsd, xsl.
            output = AeCatalogHelper.getCatalogSchema(entry, requestURL);
         }
      }
      catch(Exception e)
      {
         aResponse.setContentType("text/plain"); //$NON-NLS-1$
         aResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         AeException.printError(aResponse.getWriter(), e );    
         return;
      }
            
      if (output != null)
      {
         // make sure that the charset is UTF-8.
         aResponse.setContentType("application/xml; charset=utf-8"); //$NON-NLS-1$
         aResponse.setStatus(HttpServletResponse.SC_OK);
         aResponse.getWriter().println(output);         
      }
      else
      {
         // todo (PJ) send redirect if protocol is http, https or file and host is not this server instance.
         String msg = AeMessages.format("AeCatalogServlet.RESOURCE_NOT_FOUND", entry); //$NON-NLS-1$
         aResponse.setContentType("text/plain"); //$NON-NLS-1$
         aResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
         aResponse.getWriter().println(msg);         
      }
   }
}