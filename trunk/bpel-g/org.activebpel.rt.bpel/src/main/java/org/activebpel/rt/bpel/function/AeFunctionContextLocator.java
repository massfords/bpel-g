//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/function/AeFunctionContextLocator.java,v 1.5.4.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.function;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.impl.function.AeInvalidFunctionContextException;
import org.activebpel.rt.util.AeUtil;
import org.jaxen.FunctionContext;

/**
 * Standard impl for <code>IAeFunctionContextLocator</code>. 
 */
public class AeFunctionContextLocator implements IAeFunctionContextLocator
{
   // error message constants
   protected static final String FAILED_TO_FIND   = "AeFunctionContextLocator.ERROR_1"; //$NON-NLS-1$
   protected static final String FAILED_TO_CREATE = "AeFunctionContextLocator.ERROR_2"; //$NON-NLS-1$
   
   // constant for location string separator
   private static final String LOCATION_SEP = ";"; //$NON-NLS-1$
   
   /** Class loader used to load function contexts if no location is specified. */
   protected ClassLoader mDefaultLoader; 

   /**
    * If no location (empty string or null) has been specified, the context class
    * is loaded via the default class loader.  Otherwise, the location is taken to 
    * be one or more absolute file paths separated by a semi-colon (;).  The location
    * string will be used to create a <code>URLClassLoader</code> with the default
    * class loader set as its parent.  This new class loader will be used to find
    * the <code>IAeExpressionFunctionContext</code> class.
    * 
    * @param aNamespace
    * @param aLocation
    * @param aClassName
    * @see org.activebpel.rt.bpel.function.IAeFunctionContextLocator#locate(java.lang.String, java.lang.String, java.lang.String)
    */
   public IAeFunctionContext locate(String aNamespace, String aLocation, String aClassName) throws AeInvalidFunctionContextException
   {
      if (AeUtil.isNullOrEmpty(aNamespace))
      {
         throw new AeInvalidFunctionContextException(AeMessages.getString("AeFunctionContextLocator.MISSING_NAMESPACE_FOR_FUNC_CTX_ERROR")); //$NON-NLS-1$
      }
      ClassLoader fcClassLoader = getDefaultClassLoader();
      if (AeUtil.notNullOrEmpty(aLocation))
      {
         fcClassLoader = createCustomClassLoader(aLocation);
      }

      Class functionContextClass = null;

      try
      {
         functionContextClass = fcClassLoader.loadClass(aClassName);
      }
      catch (Throwable cnf)
      {
         throw new AeInvalidFunctionContextException(AeMessages.format(FAILED_TO_FIND, aClassName), cnf);
      }

      try
      {
         Object fcObject = functionContextClass.newInstance();
         // if we got a straight FunctionContext impl then wrap it in our 
         // ae function context (deprecated?)
         if (fcObject instanceof FunctionContext)
         {
            fcObject = new AeFunctionContextWrapper(aNamespace, (FunctionContext) fcObject);
         }
         return (IAeFunctionContext) fcObject;
      }
      catch (Throwable t)
      {
         throw new AeInvalidFunctionContextException(AeMessages.format(FAILED_TO_CREATE, aClassName), t);
      }
   }
   
   /**
    * Create the classloader capable of loading the specified <code>FunctionContext</code>.
    * @param aLocation
    */
   protected ClassLoader createCustomClassLoader( String aLocation )
   {
      URL[] urls = resolve( aLocation );
      return URLClassLoader.newInstance( urls, getDefaultClassLoader() );
   }
   
   /**
    * Setter for the default class loader.
    * @param aClassLoader
    */
   public void setDefaultClassLoader(ClassLoader aClassLoader)
   {
      mDefaultLoader = aClassLoader;
   }

   /**
    * Accessor for the default <code>ClassLoader</code> used to resolve <code>FunctionContext</code>
    * impls.  If no default <code>ClassLoader</code> has been explicitly set
    * then the context class loader for the current thread is returned.
    */
   protected ClassLoader getDefaultClassLoader()
   {
      if( mDefaultLoader == null )
      {
         mDefaultLoader = Thread.currentThread().getContextClassLoader();
      }
      return mDefaultLoader;
   }
   
   /**
    * Resolve the location arg to an array of <code>URL</code> objects.
    * @param aLocation
    */
   public URL[] resolve(String aLocation)
   {
      String realLocation = resolveRealLocation( aLocation );
      return resolveUrls( realLocation );
   }
   
   /**
    * Replaces any specified variables, such as <code>{server.home}</code>, with
    * values in <code>System.properties</code>.
    * @param aLocation
    */
   protected String resolveRealLocation( String aLocation )
   {
      return AeUtil.replaceAntStyleParams( aLocation, System.getProperties() );
   }
   
   /**
    * Helper method to return an array of URL objects corresponding to the given ";" delimited 
    * string of classpath files.
    * @param aClasspath a ';' delimited concatenation of jar and zip files.
    * @return URL[] 
    */
   protected URL[] resolveUrls(String aClasspath)
   {
      ArrayList<URL> urlList = new ArrayList<>();
      StringTokenizer classFile = new StringTokenizer( aClasspath, LOCATION_SEP );
      int files = classFile.countTokens();
      if ( files > 0 )
      {
         for ( int i = 0 ; i < files ; i++ )
         {
            String filename = classFile.nextToken();
            File file = new File(filename.trim());
            try
            {
               urlList.add( file.toURI().toURL() );
            }
            catch (MalformedURLException e)
            {
            }
         }
      }
      return urlList.toArray( new URL[urlList.size()] );
   }
}
