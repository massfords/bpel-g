//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeAbstractResourceTag.java,v 1.2 2007/08/07 18:43:17 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.activebpel.rt.util.AeUtil;

/**
 * An abstract class that implements the resource bundle management.  Extending this
 * class will allow other tags to access externalized strings.
 */
public abstract class AeAbstractResourceTag extends BodyTagSupport
{
   private static final String NOT_PRESENT = "none"; //$NON-NLS-1$
   
   /** Collection of bundles based on locale */
   protected static Hashtable sBundleTable = new Hashtable();

   /**
    * Gets the resource bundle to use.
    */
   public static synchronized ResourceBundle getResourceBundle(ServletContext aServletContext, ServletRequest aServletRequest)
   {
      // If the bundle for the requesting locale has not been loaded, load it and save it.
      Locale locale = aServletRequest.getLocale();
      String country = locale.getCountry();
      if (AeUtil.isNullOrEmpty(country))
         country = NOT_PRESENT;
      String language = locale.getLanguage();
      if (AeUtil.isNullOrEmpty(language))
         language = NOT_PRESENT;
      
      String bundleKey = language + "_" + country; //$NON-NLS-1$
      
      ResourceBundle bundle = (ResourceBundle)sBundleTable.get(bundleKey);
      if (bundle == null)
      {
         try
         {
            String bundleLoc = aServletContext.getInitParameter("resource-bundle-class"); //$NON-NLS-1$
            String bundlePrefix = aServletContext.getInitParameter("bundle-prefix"); //$NON-NLS-1$
            Class c = Class.forName(bundleLoc);
            Constructor constructor = c.getConstructor( new Class[] { String.class, ServletContext.class, ServletRequest.class } );
            bundle = (ResourceBundle) constructor.newInstance( new Object[] { bundlePrefix, aServletContext, aServletRequest } );
            sBundleTable.put(bundleKey, bundle);
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
      return bundle;
   }

   /**
    * Given a page context and a property name, returns the resource string value.  In 
    * other words, this method gets the exernalized string given a key into the bundle.
    * 
    * @param aPropertyName
    */
   protected String getResourceString(String aPropertyName)
   {
      ResourceBundle bundle = getResourceBundle(pageContext.getServletContext(), pageContext.getRequest());
      return bundle.getString(aPropertyName);
   }

}
