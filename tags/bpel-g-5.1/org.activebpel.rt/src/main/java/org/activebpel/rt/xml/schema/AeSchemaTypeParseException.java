// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/AeSchemaTypeParseException.java,v 1.1 2006/09/07 14:41:12 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.schema;


/**
 * An exception that is thrown when a schema type fails to parse.
 */
public class AeSchemaTypeParseException extends RuntimeException
{
   /**
    * Constructor.
    */
   public AeSchemaTypeParseException(String aMessage)
   {
      super(aMessage);
   }
}
