//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeXMLDBUtil.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb;

import java.util.Date;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;

/**
 * A class with some static methods to do some standard XMLDB related operations.
 */
public class AeXMLDBUtil
{
   /**
    * Returns either a AeSchemaDateTime for the given date or the special NULL object.
    * 
    * @param aDate
    */
   public static Object getDateTimeOrNull(Date aDate)
   {
      if (aDate == null)
      {
         return IAeXMLDBStorage.NULL_DATETIME;
      }
      else
      {
         return new AeSchemaDateTime(aDate);
      }
   }
   
   /**
    * Returns a XMLDB safe value. If the string is not <code>null</code>, then this method
    * returns the string unchanged. Otherwise, <code>IAeXMLDBStorage.NULL_STRING</code> is
    * returned.
    * @param aString
    * @return The string if is not null, otherwise returns <code>IAeXMLDBStorage.NULL_STRING</code>.
    */
   public static Object getStringOrNull(String aString)
   {
      if (aString != null)
      {
         return aString;
      }
      else
      {
         return IAeXMLDBStorage.NULL_STRING;
      }
   }   
   
   /**
    * Returns a XMLDB safe value. If the string is not <code>null</code>, then this method
    * returns the string unchanged. Otherwise, <code>IAeXMLDBStorage.NULL_STRING</code> is
    * returned.
    * @param aString
    * @return The string if is not null, otherwise returns <code>IAeXMLDBStorage.NULL_STRING</code>.
    */
   public static Object getStringOrNullNoEmpty(String aString)
   {
      if (AeUtil.notNullOrEmpty(aString))
      {
         return aString;
      }
      else
      {
         return IAeXMLDBStorage.NULL_STRING;
      }
   }   
}
