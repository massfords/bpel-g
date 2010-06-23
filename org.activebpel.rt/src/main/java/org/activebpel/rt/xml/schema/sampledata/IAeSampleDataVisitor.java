//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/sampledata/IAeSampleDataVisitor.java,v 1.3 2007/08/10 02:13:06 mford Exp $
////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.schema.sampledata; 

import org.activebpel.rt.xml.schema.sampledata.structure.AeAbstractElement;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAbstractType;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAll;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAny;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAnyAttribute;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAnyTypeElement;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAttribute;
import org.activebpel.rt.xml.schema.sampledata.structure.AeChoice;
import org.activebpel.rt.xml.schema.sampledata.structure.AeComplexElement;
import org.activebpel.rt.xml.schema.sampledata.structure.AeGroup;
import org.activebpel.rt.xml.schema.sampledata.structure.AeSequence;
import org.activebpel.rt.xml.schema.sampledata.structure.AeSimpleElement;

/**
 * Interface for the schema to XML sample document generation visitor.
 */
public interface IAeSampleDataVisitor
{
   
   /**
    * @param aAll
    */
   public void visit(AeAll aAll);
   
   /**
    * @param aAny
    */
   public void visit(AeAny aAny);
   
   /**
    * @param aAttribute
    */
   public void visit(AeAttribute aAttribute);
   
   /**
    * @param aChoice
    */
   public void visit(AeChoice aChoice);
   
   /**
    * @param aComplexElement
    */
   public void visit(AeComplexElement aComplexElement);
   
   /**
    * @param aSimpleElement
    */
   public void visit(AeSimpleElement aSimpleElement);
   
   /**
    * @param aAnyTypeElement
    */
   public void visit(AeAnyTypeElement aAnyTypeElement);
   
   /**
    * @param aGroup
    */
   public void visit(AeGroup aGroup);
   
   /**
    * @param aSequence
    */
   public void visit(AeSequence aSequence);
   
   /**
    * @param aAnyAttribuite
    */
   public void visit(AeAnyAttribute aAnyAttribuite);
   
   /**
    * @param aAbstractElement
    */
   public void visit(AeAbstractElement aAbstractElement);

   /**
    * @param aType
    */
   public void visit(AeAbstractType aType);
}
 
