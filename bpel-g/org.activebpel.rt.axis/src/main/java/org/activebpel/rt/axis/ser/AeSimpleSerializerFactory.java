//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/ser/AeSimpleSerializerFactory.java,v 1.2 2006/09/07 15:19:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.ser;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseSerializerFactory;

/**
 * A custom serializer for derived simple types.
 */
public class AeSimpleSerializerFactory extends BaseSerializerFactory
{

   /**
    * Creates a serializer factory with the given xml and java types.
    * 
    * @param aXmlType
    */
   public AeSimpleSerializerFactory(QName aXmlType)
   {
      super(AeSimpleSerializer.class, aXmlType, AeSimpleValueWrapper.class);
   }
}