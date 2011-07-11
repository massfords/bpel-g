//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/AeMessages.java,v 1.3 2005/07/12 00:23:04 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Accessor class for externalized strings
 */
public class AeMessages
{
   private static final String BUNDLE_NAME = "org.activebpel.rt.bpel.server.messages";//$NON-NLS-1$

   private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

   private AeMessages()
   {
   }

   /**
    * Returns message formatted from externalized format string in resource bundle.
    *
    * @param aKey
    * @param argument
    */
   public static String format(String aKey, int argument)
   {
      return format(aKey, new Integer(argument));
   }

   /**
    * Returns message formatted from externalized format string in resource bundle.
    *
    * @param aKey
    * @param argument
    */
   public static String format(String aKey, long argument)
   {
      return format(aKey, new Long(argument));
   }

   /**
    * Returns message formatted from externalized format string in resource bundle.
    *
    * @param aKey
    * @param argument
    */
   public static String format(String aKey, Object argument)
   {
      Object[] arguments = (argument instanceof Object[]) ? ((Object[]) argument) : new Object[] { argument };
      return format(aKey, arguments);
   }

   /**
    * Returns message formatted from externalized format string in resource bundle.
    *
    * @param aKey
    * @param arguments
    */
   public static String format(String aKey, Object[] arguments)
   {
      return MessageFormat.format(getString(aKey), arguments);
   }

   /**
    * Returns externalized string from resource bundle or key surrounded by exclamation points
    * if not found.
    * @param key - Key of string to return
    * @return - String from bundle
    */       
   public static String getString(String key)
   {
      try
      {
         return RESOURCE_BUNDLE.getString(key);
      }
      catch (MissingResourceException e)
      {
         return '!' + key + '!';
      }
   }
}