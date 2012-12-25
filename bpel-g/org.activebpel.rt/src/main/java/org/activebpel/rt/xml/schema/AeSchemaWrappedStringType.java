//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/AeSchemaWrappedStringType.java,v 1.2 2006/09/07 14:41:12 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.schema; 


/**
 * Base class for schema types that have little or no parsing and simply wrap a string.
 * No validation is done on the string method. 
 */
public abstract class AeSchemaWrappedStringType implements IAeSchemaType
{
   /** uri that we're wrapping */
   protected String mValue;
   
   /**
    * Ctor takes its wrapped value
    * @param aValue
    */
   public AeSchemaWrappedStringType(String aValue)
   {
      mValue = aValue;
   }

   /**
    * Return the URI
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return mValue;
   }

   /**
    * @return Returns the value.
    */
   protected String getValue()
   {
      return mValue;
   }

   /**
    * @param aValue The value to set.
    */
   protected void setValue(String aValue)
   {
      mValue = aValue;
   }
}
 