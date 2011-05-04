// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/ser/AeDateTimeDeserializerFactory.java,v 1.2 2006/05/12 20:40:09 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.ser;

import javax.xml.namespace.QName;

/**
 * A custom Axis dateTime deserializer factory.
 */
public class AeDateTimeDeserializerFactory extends AeBaseDeserializerFactory
{
   /**
     * 
     */
    private static final long serialVersionUID = 4250559487738235409L;

/**
    * Creates a deserializer factory with the given java type and xml type.
    */
   public AeDateTimeDeserializerFactory(Class javaType, QName xmlType)
   {
      super(AeDateTimeDeserializer.class, xmlType, javaType);
   }
}
