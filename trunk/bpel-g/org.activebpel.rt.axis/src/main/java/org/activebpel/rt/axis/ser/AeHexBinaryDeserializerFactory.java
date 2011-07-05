//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/ser/AeHexBinaryDeserializerFactory.java,v 1.1 2006/09/07 15:19:54 ewittmann Exp $
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
 * Factory for hexBinary deserializer 
 */
public class AeHexBinaryDeserializerFactory extends AeBaseDeserializerFactory
{
   /**
     * 
     */
    private static final long serialVersionUID = 5754590358919941735L;

/**
    * Creates a deserializer factory with the given java type and xml type.
    */
   public AeHexBinaryDeserializerFactory(Class<AeHexBinaryDeserializer> javaType, QName xmlType)
   {
      super(javaType, xmlType, javaType);
   }
}
 