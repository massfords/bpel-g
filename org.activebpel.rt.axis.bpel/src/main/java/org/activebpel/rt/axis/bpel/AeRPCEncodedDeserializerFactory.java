package org.activebpel.rt.axis.bpel;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.rpc.encoding.Deserializer;

import org.apache.axis.Constants;
import org.apache.axis.encoding.DeserializerFactory;

// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/AeRPCEncodedDeserializerFactory.java,v 1.2 2005/06/22 17:10:55 MFord Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

/**
 * Factory for creating rpc-encoded deserializers 
 */
public class AeRPCEncodedDeserializerFactory implements DeserializerFactory
{
   /** Supported mechanisms is singleton list for axis only deserialization */
   private static final List SUPPORTED_MECHANISMS = Collections.singletonList(Constants.AXIS_SAX);
   /** context provides access to the schema types that we need to handle deserialization */
   private IAeTypesContext mTypesContext;
   
   /**
    * Constructor for deserializer factory is configured with all of the schemas
    * it needs to create a deserializer instance.
    * @param aTypesContext
    */
   public AeRPCEncodedDeserializerFactory(IAeTypesContext aTypesContext)
   {
      mTypesContext = aTypesContext;
   }

   /**
    * @see javax.xml.rpc.encoding.DeserializerFactory#getDeserializerAs(java.lang.String)
    */
   public Deserializer getDeserializerAs(String mechanismType)
   {
      return new AeRPCEncodedDeserializer(mTypesContext);
   }

   /**
    * @see javax.xml.rpc.encoding.DeserializerFactory#getSupportedMechanismTypes()
    */
   public Iterator getSupportedMechanismTypes()
   {
      return SUPPORTED_MECHANISMS.iterator();
   }

}
