// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/sampledata/castor/AeCastorSampleDataGenerator.java,v 1.1 2007/06/08 03:32:21 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.schema.sampledata.castor;

import org.activebpel.rt.xml.schema.sampledata.AeSampleDataVisitor;
import org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences;
import org.activebpel.rt.xml.schema.sampledata.structure.AeStructure;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ElementDecl;
import org.w3c.dom.Document;

/**
 * A utility class to make generating XML from schemas more
 * manageable.
 */
public class AeCastorSampleDataGenerator
{
   /**
    * Generates XML from a Complex Type.  Uses the given data generation
    * preferences.
    * 
    * @param aElementDecl
    * @param aPreferences
    */
   public static Document generateXML(ElementDecl aElementDecl, IAeSampleDataPreferences aPreferences)
   {
      AeSampleDataVisitor visitor = new AeSampleDataVisitor(aPreferences);
      AeStructure root = AeCastorToAeStructure.build(aElementDecl, aPreferences);
      root.accept(visitor);
      return visitor.getDocument();
   }
   
   /**
    * Generates XML from a Complex Type.  Uses the given data generation
    * preferences.
    * 
    * @param aComplexType
    * @param aPreferences
    */
   public static Document generateXML(ComplexType aComplexType, IAeSampleDataPreferences aPreferences)
   {
      AeSampleDataVisitor visitor = new AeSampleDataVisitor(aPreferences);
      AeStructure root = AeCastorToAeStructure.build(aComplexType, aPreferences);
      root.accept(visitor);
      return visitor.getDocument();
   }
}
