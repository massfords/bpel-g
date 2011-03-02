//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/ser/AeGYearDeserializerFactory.java,v 1.1 2006/05/26 21:38:18 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.ser; 

import javax.xml.namespace.QName;

/**
 * Factory for schema calendar field for deserializer gYear 
 */
public class AeGYearDeserializerFactory extends AeBaseDeserializerFactory
{
   /**
    * Creates a deserializer factory with the given java type and xml type.
    */
   public AeGYearDeserializerFactory(Class javaType, QName xmlType)
   {
      super(AeGYearDeserializer.class, xmlType, javaType);
   }
}
 