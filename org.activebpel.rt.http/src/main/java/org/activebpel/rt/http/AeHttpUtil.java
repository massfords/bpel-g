//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/AeHttpUtil.java,v 1.4.4.1 2008/04/21 16:15:52 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeFileUtil;
import org.activebpel.rt.util.AeMimeUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.wsio.IAeWebServiceAttachment;

/**
 * HTTP Service Utilities
 */
public class AeHttpUtil
{

   public static final String MULTIPART_BOUNDARY_PARAMETER_NAME = "http.method.multipart.boundary"; //$NON-NLS-1$

   /**
    * Converts a query string to a map of parameters
    * @param aQueryString
    * @return the map of query parameters
    */
   public static Map createQueryStringMap(String aQueryString)
   {
      Map pMap = null;

      StringTokenizer st = new StringTokenizer(aQueryString, "&"); //$NON-NLS-1$
      while (st.hasMoreTokens())
      {
         if ( pMap == null )
            pMap = new LinkedHashMap();

         String pair = st.nextToken();
         String[] tokens = pair.split("="); //$NON-NLS-1$
         if ( tokens.length == 2 )
            pMap.put(tokens[0], tokens[1]);
         else if ( tokens.length == 1 )
            pMap.put(tokens[0], null);
      }

      return pMap == null ? Collections.EMPTY_MAP : pMap;
   }

   /**
    * Helper method to add a name/value pair to an http property map. The map supports duplicates (when the
    * aAllowDuplicates is set).
    * @param aAllowDuplicates when set to <code>true</code>, the map values are kept in an ArrayList;
    *            otherwise, the map value is a String
    * @param aContextMap
    * @param aName the map entry key
    * @param aValue the map entry value
    */
   public static void addToMap(Map aContextMap, String aName, String aValue, boolean aAllowDuplicates)
   {
      if ( aContextMap == null )
         aContextMap = new LinkedHashMap();

      if ( aAllowDuplicates )
      {
         if ( !aContextMap.containsKey(aName) )
         {
            List valueList = new ArrayList();
            aContextMap.put(aName, valueList);
         }

         List valueList = (List)aContextMap.get(aName);
         if ( !valueList.contains(aValue) )
            valueList.add(aValue);
      }
      else
      {
         aContextMap.put(aName, aValue);
      }
   }

   /**
    * Normalizes the case (as in upper/lower case) of header names by copying and forming the keys.
    * @param aMap
    */
   public static Map normalizeHeaders(Map aMap)
   {
      return normalizeHeaders(aMap, null);
   }

   /**
    * Normalizes the case (as in upper/lower case) of header names by copying and forming the keys.
    * @param aMap
    * @param aFilter the set of map key to be removed from the normalized result map
    */
   public static Map normalizeHeaders(Map aMap, Set aFilter)
   {
      if ( aMap.size() == 0 )
         return aMap;
      if ( aFilter == null )
         aFilter = Collections.EMPTY_SET;

      Map nMap = new LinkedHashMap();
      for (Iterator hItr = aMap.entrySet().iterator(); hItr.hasNext();)
      {
         Map.Entry hdr = (Map.Entry)hItr.next();
         String key = AeMimeUtil.formMimeKey((String)hdr.getKey());
         if ( !aFilter.contains(key.toLowerCase()) )
            nMap.put(key, (String)hdr.setValue(hdr));
      }
      return nMap;
   }

   /**
    * Parses a Content-Disposition header and returns a map of it's elements
    * @param aDisposition
    * @return the map of the disposition fields i.e <code>name</code> and optional <code>filename</code>
    * @throws AeException if the Content-Disposition is not properly formatted
    */
   public static Map parseDispositionFields(String aDisposition) throws AeException
   {
      Map elementMap = new LinkedHashMap();
      StringTokenizer outer = null;
      StringTokenizer inner = null;
      String element = null;
      String key = null;
      String val = null;
      int startIndex = 0;
      int stopIndex = 0;

      if ( aDisposition == null )
         return elementMap;

      outer = new StringTokenizer(aDisposition, "; ", false); //$NON-NLS-1$
      while (outer.hasMoreTokens())
      {
         element = outer.nextToken();
         inner = new StringTokenizer(element, "=", false); //$NON-NLS-1$
         if ( inner.countTokens() == 2 )
         {
            key = inner.nextToken();
            val = inner.nextToken();
            if ( val.startsWith("\"") ) //$NON-NLS-1$
            {
               startIndex = 1;
               stopIndex = (val.length() > 0) ? val.length() - 1 : 0;
            }
            else
            {
               startIndex = 0;
               stopIndex = val.length();
            }

            if ( startIndex != 0 || stopIndex != val.length() )
            {
               val = val.substring(startIndex, stopIndex);
            }

            elementMap.put(key, val);
         }
      }

      return elementMap;
   }

   /**
    * Parses the passed string for a name=value returning the value.
    * <p>
    * All exceptions are purposely ignored as the intent of this method is to be used in bpel process
    * javascript expressions.
    * </p>
    * <i>For example, see <code>rest-google-docs-actions.bpel</code></i>
    * @param aParamName the name of the parameter to find
    * @param aFormAsString the string containing all form properties
    * @return the value of the form parameter or an empty string; never returns null
    */
   public static String getFormParam(String aParamName, String aFormAsString)
   {
      String str;
      BufferedReader reader = new BufferedReader(new StringReader(aFormAsString));
      try
      {
         while ((str = reader.readLine()) != null)
         {
            if ( str.length() > 0 )
            {
               StringTokenizer st = new StringTokenizer(str, "="); //$NON-NLS-1$
               if ( st.countTokens() == 2 )
               {
                  String name = st.nextToken();
                  String value = st.nextToken();
                  if ( name.equals(aParamName) )
                     return value;
               }
            }
         }

      }
      catch (IOException e)
      {
         // eat it
      }
      return ""; //$NON-NLS-1$
   }

   /**
    * @return an iterator on the set of attachment Content-Id headers
    */
   public static Iterator getAttachmentNames(List aAttchments)
   {
      Set attachmentNames = new TreeSet();
      for (Iterator aItr = aAttchments.iterator(); aItr.hasNext();)
      {
         IAeWebServiceAttachment attachment = (IAeWebServiceAttachment)aItr.next();
         attachmentNames.add(attachment.getContentId());
      }
      return attachmentNames.iterator();
   }

   /**
    * Convenience method, converts an input stream to a string
    * @param aStream
    * @throws AeException
    */
   public static String stream2String(InputStream aStream) throws AeException
   {
      // TODO (JB) what about encoding ??

      if ( aStream == null )
         return null;
      try
      {
         BufferedReader in = new BufferedReader(new InputStreamReader(aStream));
         StringBuffer buffer = new StringBuffer();
         String line;
         try
         {
            while ((line = in.readLine()) != null)
            {
               buffer.append(line);
            }
         }
         catch (IOException ex)
         {
            throw new AeException(ex);
         }
         return buffer.toString();
      }
      finally
      {
         AeCloser.close(aStream);
      }
   }

   /**
    * Convenience method to write a textual http response
    * @param aResponse the http servlet response object
    * @param aBody the string to write
    * @return true - when the was some output written; otherwise returns false
    * @throws IOException
    */
   public static boolean writeText(HttpServletResponse aResponse, String aBody) throws IOException
   {
      if ( AeUtil.isNullOrEmpty(aBody) )
         return false;

      PrintWriter out = aResponse.getWriter();
      try
      {
         out.write(aBody);
         out.flush();
      }
      finally
      {
         out.flush();
         AeCloser.close(out);
      }
      return true;
   }

   /**
    * Convenience method to write a textual http response
    * @param aResponse the http servlet response object
    * @param aBodyStreams the streams to write
    * @return true - when the was some output written; otherwise returns false
    * @throws IOException
    */
   public static boolean writeBinary(HttpServletResponse aResponse, InputStream[] aBodyStreams) throws IOException
   {
      if ( aBodyStreams == null )
         return false;

      ServletOutputStream out = aResponse.getOutputStream();
      try
      {
         for (int i = 0; i < aBodyStreams.length; i++)
         {
            AeFileUtil.copy(aBodyStreams[i], out);
            out.flush();
            AeCloser.close(aBodyStreams[i]);
         }
      }
      finally
      {
         AeCloser.close(out);
      }
      return true;
   }
}
