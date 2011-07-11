//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/AeSchemaAnyURI.java,v 1.2 2006/09/07 14:41:12 ewittmann Exp $
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
 * Wrapper object for xsd:anyURI simple data type. This ensures that Axis serializes the data correctly 
 */
public class AeSchemaAnyURI extends AeSchemaWrappedStringType
{
   /**
    * Ctor accepts the uri
    * @param aURI
    */
   public AeSchemaAnyURI(String aURI)
   {
      super(aURI);
   }
   
   /**
    * Gets the AnyURI value.
    */
   public String getURI()
   {
      return getValue();
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaType#accept(org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor)
    */
   public void accept(IAeSchemaTypeVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
 