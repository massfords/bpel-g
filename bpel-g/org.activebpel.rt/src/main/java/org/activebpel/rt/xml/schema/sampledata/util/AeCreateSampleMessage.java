//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/sampledata/util/AeCreateSampleMessage.java,v 1.3 2007/08/13 22:26:12 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.schema.sampledata.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.wsdl.Message;
import javax.wsdl.Part;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.xml.schema.sampledata.AeSampleDataPreferences;
import org.activebpel.rt.xml.schema.sampledata.AeSimpleTypeSampleDataProducer;
import org.activebpel.rt.xml.schema.sampledata.castor.AeCastorSampleDataGenerator;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.XMLType;
import org.w3c.dom.Document;

/**
 * Util class for creating sample data for a WSDL Message. This util accepts
 * a Message and returns a LinkedHashMap with name value pairs where the name
 * is the WSDL Part and the value is a Document with the generated sample or
 * and "" if the part was not a complex type or element.
 */
public class AeCreateSampleMessage
{
   /** simple type data producer */
   private static AeSimpleTypeSampleDataProducer sSimpleTypeDataProducer = new AeSimpleTypeSampleDataProducer();

   /** preferences used for generating samples */
   private AeSampleDataPreferences mSampleDataPreferences;

   /**
    * No arg ctor
    */
   public AeCreateSampleMessage()
   {
   }

   /**
    * Creates a sample for the given message
    * @param aMessage
    */
   public LinkedHashMap create(AeBPELExtendedWSDLDef aDef, Message aMessage) throws AeException
   {
      LinkedHashMap map = new LinkedHashMap();

      List parts = aMessage.getOrderedParts(null);

      for (Iterator iter = parts.iterator(); iter.hasNext();)
      {
         Part part = (Part) iter.next();

         if (!aDef.isComplexEncodedType(part))
         {
            map.put(part, sSimpleTypeDataProducer.getSampleData(part.getTypeName()));
            continue;
         }

         Document doc = null;

         if (part.getElementName() != null)
         {
            ElementDecl elementDecl = aDef.findElement(part.getElementName());
            if (elementDecl == null)
            {
               throw new AeException( AeMessages.format("AeCreateSampleMessage.ELEM_DECL_NOT_FOUND", new String[]{part.getElementName().toString(), part.getName()}) ); //$NON-NLS-1$
            }
            doc = AeCastorSampleDataGenerator.generateXML(elementDecl, getSampleDataPreferences());
         }
         else
         {
            XMLType type = aDef.findType(part.getTypeName());
            if (type == null)
            {
               throw new AeException( AeMessages.format("AeCreateSampleMessage.PART_TYPE_NOT_FOUND", new String[]{part.getTypeName().toString(), part.getName()}) ); //$NON-NLS-1$
            }

            if (type instanceof ComplexType)
            {
               doc = AeCastorSampleDataGenerator.generateXML((ComplexType)type, getSampleDataPreferences());
            }
         }

         if (doc != null)
         {
            map.put(part, doc);
         }
         else
         {
            map.put(part, ""); //$NON-NLS-1$
         }
      }
      return map;
   }

   /**
    * @return the sampleDataPreferences
    */
   public AeSampleDataPreferences getSampleDataPreferences()
   {
      if (mSampleDataPreferences == null)
      {
         setSampleDataPreferences(new AeSampleDataPreferences());
      }
      return mSampleDataPreferences;
   }

   /**
    * @param aSampleDataPreferences the sampleDataPreferences to set
    */
   public void setSampleDataPreferences(AeSampleDataPreferences aSampleDataPreferences)
   {
      mSampleDataPreferences = aSampleDataPreferences;
   }
}
