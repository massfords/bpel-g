//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/IAeSchemaType.java,v 1.2 2006/09/07 14:41:12 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.schema;


/**
 * An interface that all schema type classes should implement.
 */
public interface IAeSchemaType
{
   /**
    * Called to accept the schema type visitor.  All implementations should simply call
    * <code>aVisitor.visit(this)</code>.
    * 
    * @param aVisitor
    */
   public void accept(IAeSchemaTypeVisitor aVisitor);
}
