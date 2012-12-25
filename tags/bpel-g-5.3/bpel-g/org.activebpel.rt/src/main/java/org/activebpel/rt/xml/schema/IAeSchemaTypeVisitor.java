// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/IAeSchemaTypeVisitor.java,v 1.1 2006/09/07 14:41:12 ewittmann Exp $
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
 * Visitor interface for schema types.
 */
public interface IAeSchemaTypeVisitor
{
   /**
    * Visit the given schema type.
    * 
    * @param aSchemaType
    */
   public void visit(AeSchemaAnyURI aSchemaType);

   /**
    * Visit the given schema type.
    * 
    * @param aSchemaType
    */
   public void visit(AeSchemaDate aSchemaType);

   /**
    * Visit the given schema type.
    * 
    * @param aSchemaType
    */
   public void visit(AeSchemaDateTime aSchemaType);

   /**
    * Visit the given schema type.
    * 
    * @param aSchemaType
    */
   public void visit(AeSchemaDay aSchemaType);

   /**
    * Visit the given schema type.
    * 
    * @param aSchemaType
    */
   public void visit(AeSchemaDuration aSchemaType);

   /**
    * Visit the given schema type.
    * 
    * @param aSchemaType
    */
   public void visit(AeSchemaMonth aSchemaType);

   /**
    * Visit the given schema type.
    * 
    * @param aSchemaType
    */
   public void visit(AeSchemaMonthDay aSchemaType);

   /**
    * Visit the given schema type.
    * 
    * @param aSchemaType
    */
   public void visit(AeSchemaTime aSchemaType);

   /**
    * Visit the given schema type.
    * 
    * @param aSchemaType
    */
   public void visit(AeSchemaYear aSchemaType);

   /**
    * Visit the given schema type.
    * 
    * @param aSchemaType
    */
   public void visit(AeSchemaYearMonth aSchemaType);

   /**
    * Visit the given schema type.
    * 
    * @param aSchemaType
    */
   public void visit(AeSchemaHexBinary aSchemaType);

   /**
    * Visit the given schema type.
    * 
    * @param aSchemaType
    */
   public void visit(AeSchemaBase64Binary aSchemaType);
}
