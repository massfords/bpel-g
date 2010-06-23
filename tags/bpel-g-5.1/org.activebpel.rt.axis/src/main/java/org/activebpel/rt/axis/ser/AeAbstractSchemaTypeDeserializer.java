// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/ser/AeAbstractSchemaTypeDeserializer.java,v 1.1 2006/09/07 15:19:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.axis.ser;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.axis.AeMessages;
import org.apache.axis.encoding.ser.SimpleDeserializer;

/**
 * Base class for Ae deserializers.
 */
public abstract class AeAbstractSchemaTypeDeserializer extends SimpleDeserializer
{
   /**
    * The Deserializer is constructed with the xmlType and javaType
    */
   public AeAbstractSchemaTypeDeserializer(Class javaType, QName xmlType)
   {
      super(javaType, xmlType);
   }

   /**
    * @see org.apache.axis.encoding.ser.SimpleDeserializer#makeValue(java.lang.String)
    */
   public Object makeValue(String aSource)
   {
      try
      {
         return makeValueInternal(aSource);
      }
      catch (Exception e) 
      {
         AeException.logError(e, AeMessages.getString("AeAbstractSchemaTypeDeserializer.ERROR_0")); //$NON-NLS-1$
         return aSource;
      }
   }

   /**
    * Internal method that subclasses must implement - does the actual deserialization.
    * 
    * @param aSource
    */
   protected abstract Object makeValueInternal(String aSource);
}
