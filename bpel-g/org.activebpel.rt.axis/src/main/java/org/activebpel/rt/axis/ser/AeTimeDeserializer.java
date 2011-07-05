// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/ser/AeTimeDeserializer.java,v 1.5 2006/09/07 15:19:54 ewittmann Exp $
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

import org.activebpel.rt.xml.schema.AeSchemaTime;

/**
 * A custom Axis time deserializer.
 */
public class AeTimeDeserializer extends AeAbstractSchemaTypeDeserializer<AeSchemaTime>
{
   /**
     * 
     */
    private static final long serialVersionUID = 289283105570820366L;

/**
    * The Deserializer is constructed with the xmlType and javaType
    */
   public AeTimeDeserializer(Class<AeSchemaTime> javaType, QName xmlType)
   {
      super(javaType, xmlType);
   }

   /**
    * @see org.activebpel.rt.axis.ser.AeAbstractSchemaTypeDeserializer#makeValueInternal(java.lang.String)
    */
   protected AeSchemaTime makeValueInternal(String aSource)
   {
      return new AeSchemaTime(aSource);
   }
}
