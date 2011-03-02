//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/graph/IAeXmlDefGraphNodeProperty.java,v 1.3 2008/02/13 01:45:55 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.graph;


/**
 * Define a simple property for a xml def graph node.
 */
public interface IAeXmlDefGraphNodeProperty
{
   /**
    * Returns property name.
    * @return name of property.
    */
   public String getName();
   
   /**
    * Returns property value.
    * @return value
    */
   public String getValue();
   
   /**
    * Returns location path
    */
   public String getLocationPath();
   
   /**
    * Returns true if this property is a blel variable
    */
   public boolean isVariable();
   
   /**
    * Returns true if this is a detail property
    */
   public boolean isDetail();
   
   /**
    * Returns true of location path is set
    */
   public boolean isHasLocationPath();
   
   /**
    * Removes true if this property has to be removed - when present
    */
   public boolean isRemove();
}
