//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/IAeXMLDBStorage.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb;

/**
 * Interface for a XMLDB storage object.  All XMLDB storages should implement this interface.
 */
public interface IAeXMLDBStorage
{
   /** Represents a NULL xsd:integer. */
   public static final IAeXMLDBNull NULL_INTEGER = new IAeXMLDBNull()
   {
      /**
       * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBNull#getValue()
       */
      public String getValue()
      {
         return "0"; //$NON-NLS-1$
      }
   };
   
   /** Represents a NULL xsd:int. */
   public static final IAeXMLDBNull NULL_INT = new IAeXMLDBNull()
   {
      /**
       * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBNull#getValue()
       */
      public String getValue()
      {
         return "0"; //$NON-NLS-1$
      }
   };

   /** Represents a NULL xsd:string. */
   public static final IAeXMLDBNull NULL_STRING = new IAeXMLDBNull()
   {
      /**
       * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBNull#getValue()
       */
      public String getValue()
      {
         return ""; //$NON-NLS-1$
      }
   };

   /** Represents a NULL xsd:dateTime. */
   public static final IAeXMLDBNull NULL_DATETIME = new IAeXMLDBNull()
   {
      /**
       * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBNull#getValue()
       */
      public String getValue()
      {
         return "1970-01-01T12:00:00Z"; //$NON-NLS-1$
      }
   };

   /** Represents a NULL ae:AeAnyNullable typically used to store XML documents in XMLDB. */
   public static final IAeXMLDBNull NULL_DOCUMENT = new IAeXMLDBNull()
   {
      /**
       * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBNull#getValue()
       */
      public String getValue()
      {
         return null;
      }
   };

   /** Represents a NULL xsd:double. */
   public static final IAeXMLDBNull NULL_DOUBLE = new IAeXMLDBNull()
   {
      /**
       * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBNull#getValue()
       */
      public String getValue()
      {
         return "0.0"; //$NON-NLS-1$
      }
   };

}
