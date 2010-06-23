// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeJavaTypesWrapper.java,v 1.2 2005/01/14 16:30:35 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import java.io.Serializable;

/**
 * Exposes well known java types via bean propery accessors.
 * Current impl supports only strings.
 */
public class AeJavaTypesWrapper implements Serializable
{
   /** Null types wrapper. */
   private static final AeJavaTypesWrapper EMPTY = new AeJavaTypesWrapper(""); //$NON-NLS-1$
   
   /** Property value. */
   protected String mString;
   
   /**
    * Utility method for wrapping a string array.
    * @param aArray
    */
   public static AeJavaTypesWrapper[] wrap( String[] aArray )
   {
      if( aArray == null )
      {
         return new AeJavaTypesWrapper[]{EMPTY};
      }
      else
      {
         AeJavaTypesWrapper[] wrapper = new AeJavaTypesWrapper[aArray.length];
         for( int i = 0; i < aArray.length; i++ )
         {
            wrapper[i] = new AeJavaTypesWrapper( aArray[i] );
         }
         return wrapper;
      }
   }

   /**
    * Constructor.
    * @param aString 
    */
   public AeJavaTypesWrapper( String aString )
   {
      mString = aString;
   }
   
   /**
    * Returns the string value of the property.
    */
   public String getString()
   {
      return mString;
   }
}
