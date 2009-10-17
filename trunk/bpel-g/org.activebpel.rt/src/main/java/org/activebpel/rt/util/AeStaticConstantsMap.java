//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeStaticConstantsMap.java,v 1.1 2005/02/19 00:28:58 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;

/**
 * Implements a map between the names and values of the static fields in a Java
 * class or interface.
 */
public class AeStaticConstantsMap
{
   /** The class or interface whose static fields will be mapped. */
   private final Class mClass;

   /** Whether {@link #init()} called yet. */
   private boolean mInited = false;

   /** Map from the names of the class's static fields to their values. */
   private Map mNamesToValuesMap;

   /** Map from the values of the class's static fields to their names. */
   private Map mValuesToNamesMap;

   /**
    * Constructs a map between the names and values of the static fields in the
    * specified class or interface.
    */
   public AeStaticConstantsMap(Class aClass)
   {
      mClass = aClass;
   }

   /**
    * Makes sure that the internal maps are initialized.
    */
   protected void init()
   {
      if (!mInited)
      {
         mInited = true;

         Field[] fields = mClass.getDeclaredFields();
         mNamesToValuesMap = new HashMap();
         mValuesToNamesMap = new HashMap();

         for (int i = 0; i < fields.length; ++i)
         {
            Field field = fields[i];

            if (Modifier.isStatic(field.getModifiers()))
            {
               try
               {
                  String name = field.getName();
                  Object value = field.get(null);

                  mNamesToValuesMap.put(name, value);
                  mValuesToNamesMap.put(value, name);
               }
               catch (IllegalArgumentException e)
               {
                  AeException.logError(e, AeMessages.getString("AeStaticConstantsMap.ERROR_0") + field); //$NON-NLS-1$
               }
               catch (IllegalAccessException e)
               {
                  AeException.logError(e, AeMessages.getString("AeStaticConstantsMap.ERROR_0") + field); //$NON-NLS-1$
               }
            }
         }
      }
   }

   /**
    * Returns the name of the static field with the specified value.
    */
   public String getName(Object aValue)
   {
      init();
      return (String) mValuesToNamesMap.get(aValue);
   }

   /**
    * Returns the value of the static field with the specified name.
    */
   public Object getValue(Object aName)
   {
      init();
      return mNamesToValuesMap.get(aName);
   }
}
