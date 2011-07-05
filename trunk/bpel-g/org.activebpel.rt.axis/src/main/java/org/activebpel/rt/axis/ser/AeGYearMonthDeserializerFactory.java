//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/ser/AeGYearMonthDeserializerFactory.java,v 1.1 2006/05/26 21:38:18 mford Exp $
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
 * Factory for schema calendar field gYear deserializer  
 */
public class AeGYearMonthDeserializerFactory extends AeBaseDeserializerFactory
{
   /**
     * 
     */
    private static final long serialVersionUID = -5901465643831892121L;

/**
    * Creates a deserializer factory with the given java type and xml type.
    */
   public AeGYearMonthDeserializerFactory(Class<AeGYearMonthDeserializer> javaType, QName xmlType)
   {
      super(AeGYearMonthDeserializer.class, xmlType, javaType);
   }
}
 