//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/sampledata/castor/AeCastorStructureFactory.java,v 1.11 2008/03/20 14:27:22 kpease Exp $
////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.schema.sampledata.castor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.schema.AeSchemaUtil;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAbstractElement;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAbstractType;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAll;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAny;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAnyAttribute;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAnyTypeElement;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAttribute;
import org.activebpel.rt.xml.schema.sampledata.structure.AeBaseElement;
import org.activebpel.rt.xml.schema.sampledata.structure.AeChoice;
import org.activebpel.rt.xml.schema.sampledata.structure.AeComplexElement;
import org.activebpel.rt.xml.schema.sampledata.structure.AeGroup;
import org.activebpel.rt.xml.schema.sampledata.structure.AeSequence;
import org.activebpel.rt.xml.schema.sampledata.structure.AeSimpleElement;
import org.exolab.castor.xml.schema.AnyType;
import org.exolab.castor.xml.schema.AttributeDecl;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ContentType;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Facet;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.SimpleContent;
import org.exolab.castor.xml.schema.SimpleType;
import org.exolab.castor.xml.schema.Union;
import org.exolab.castor.xml.schema.Wildcard;
import org.exolab.castor.xml.schema.XMLType;


/**
 * Factory class for creating AE schema structure objects from Castor schema objects.
 */
public class AeCastorStructureFactory
{
   /** The name for the wildcard anyAttribute. */
   final static String WILDCARD_ATTR_NAME = "ANY-ATTRIBUTE"; //$NON-NLS-1$

   /** The name for the wildcard element any. */
   final static String WILDCARD_ELEMENT_NAME = "ANY-ELEMENT"; //$NON-NLS-1$

   /**
    * Returns the appropriate element type based on the nature of the element decl
    * passed in.
    * @param aElementDecl
    */
   public static AeBaseElement createElement(ElementDecl aElementDecl)
   {
      AeBaseElement element;

      if (aElementDecl.getType() == null)
      {
         // The type could be null if the schema wasn't defined properly. We'll fall back to generate a string type.
         // fixme (MF) should do a preprocess of the schema elements to detect this in advance
         element = createSimpleElementModel(aElementDecl);
      }
      else if (isAbstract(aElementDecl))
      {
         element = createAbstractElementModel(aElementDecl);
      }
      else if (aElementDecl.getType().isComplexType() && ((ComplexType)aElementDecl.getType()).isAbstract())
      {
         element = new AeAbstractType();
         buildElementBase(element, aElementDecl);
      }
      else if (aElementDecl.getType() instanceof ComplexType)
      {
         // complexType element.
         element = createComplexElementModel(aElementDecl);
      }
      else if (aElementDecl.getType() instanceof AnyType)
      {
         element = createAnyTypeElement(aElementDecl);
      }
      else
      {
         // simpleType element.
         element = createSimpleElementModel(aElementDecl);
      }
      return element;
   }

   /**
    * Creates a structure for the given complex type. There are no minOccurs
    * or other facets to read. The name of the structure will be the name of the
    * type in the default namespace.
    * @param aComplexType
    */
   public static AeBaseElement createElement(ComplexType aComplexType)
   {
      AeBaseElement element = null;
      if (aComplexType.isAbstract())
      {
         element = new AeAbstractType();
      }
      else
      {
         // complexType element.
         AeComplexElement e = new AeComplexElement();
         populateComplexElement(e, aComplexType);
         e.setXsiType(new QName(aComplexType.getSchema().getTargetNamespace(), aComplexType.getName()));
         element = e;
      }
      element.setName(new QName("", aComplexType.getName())); //$NON-NLS-1$
      return element;
   }

   /**
    * Convenience method to check if the element is an abstract element. This
    * accounts for Castor's failure to see if the element is a ref and follow that
    * ref to resolve the element's abstract facet.
    * @param aElementDecl
    */
   protected static boolean isAbstract(ElementDecl aElementDecl)
   {
      return aElementDecl.isAbstract() || (aElementDecl.getReference() != null && aElementDecl.getReference().isAbstract());
   }

   /**
    * Constructs a model that for an element of xs:anyType
    * @param aElementDecl
    */
   private static AeAnyTypeElement createAnyTypeElement(ElementDecl aElementDecl)
   {
      AeAnyTypeElement aeElement = new AeAnyTypeElement();

      buildElementBase(aeElement, aElementDecl);
      return aeElement;

   }

   /**
    * Creates a model for the given Schema simpleType definition.
    *
    * @param aElementDecl
    * @return AeSimpleElement
    */
   private static AeSimpleElement createSimpleElementModel(ElementDecl aElementDecl)
   {
      AeSimpleElement aeElement = new AeSimpleElement();

      buildElementBase(aeElement, aElementDecl);
      aeElement.setDefaultValue(aElementDecl.getDefaultValue());
      aeElement.setFixedValue(aElementDecl.getFixedValue());

      // Set the type.
      SimpleType type;
      if ( aElementDecl.getReference() != null )
         type = (SimpleType) aElementDecl.getReference().getType();
      else
         type = (SimpleType) aElementDecl.getType();

      if (type != null)
      {
         QName dataType = new QName( type.getSchema().getSchemaNamespace(), type.getBuiltInBaseType().getName() );

      // use the built in simple type for the QName
         aeElement.setDataType(dataType);

      // process enumerations.
         processEnumerations(aeElement, type);
      }
      else
      {
         // Handle a missing type with a string
         aeElement.setDataType(AeTypeMapping.XSD_STRING);
      }

      return aeElement;
   }

   /**
    * Walk the type and record any enumeration values 
    * @param aElement
    * @param aType
    */
   private static void processEnumerations(AeSimpleElement aElement, SimpleType aType)
   {
      List enumerations = new ArrayList();
      Enumeration enumer = aType.getLocalFacets();
      if ( enumer != null )
      {
         while ( enumer.hasMoreElements() )
         {
            Facet facet = (Facet)enumer.nextElement();
            if ( facet.getName().equals(Facet.ENUMERATION) )
               enumerations.add(facet.getValue());
            else if ( facet.getName().equals(Facet.MIN_EXCLUSIVE) )
               aElement.setMinExclusive(facet.getValue());
            else if ( facet.getName().equals(Facet.MAX_EXCLUSIVE) )
               aElement.setMaxExclusive(facet.getValue());
            else if ( facet.getName().equals(Facet.MIN_INCLUSIVE) )
               aElement.setMinInclusive(facet.getValue());
            else if ( facet.getName().equals(Facet.MAX_INCLUSIVE) )
               aElement.setMaxInclusive(facet.getValue());
         }
      }
      aElement.setEnumRestrictions(enumerations);
   }

   /**
    * Creates a model for the given Schema abstract complexType definition.
    *
    * @param aElementDecl
    * @return AeAbstractElement
    */
   private static AeAbstractElement createAbstractElementModel(ElementDecl aElementDecl)
   {
      AeAbstractElement aeElement = new AeAbstractElement();
      buildElementBase(aeElement, aElementDecl);
      return aeElement;
   }

   /**
    * Creates a model for the given Schema complexType definition.
    *
    * @param aElementDecl
    * @return AeComplexElement
    */
   private static AeComplexElement createComplexElementModel(ElementDecl aElementDecl)
   {
      AeComplexElement aeElement = new AeComplexElement();

      buildElementBase(aeElement, aElementDecl);
      ComplexType complexType = (ComplexType)aElementDecl.getType();
      populateComplexElement(aeElement, complexType);

      return aeElement;
   }

   /**
    * Populates the complex element with the attributes and mixed properties.
    * @param aeElement
    * @param aComplexType
    */
   protected static void populateComplexElement(AeComplexElement aeElement, ComplexType aComplexType)
   {
      if (!aComplexType.isAbstract())
         aeElement.setAttributes( createAttributes(aComplexType) );
      aeElement.setMixed( aComplexType.getContentType().getType() == ContentType.MIXED );

      if ( aComplexType.isSimpleContent() )
      {
         SimpleContent simpleContent = (SimpleContent)aComplexType.getContentType();
         if (  simpleContent.getType() == ContentType.SIMPLE )
         {
            aeElement.setSimpleContentType(true);
            XMLType type = simpleContent.getSimpleType();
            aeElement.setDataType(new QName(type.getSchema().getSchemaNamespace(), type.getName()) );
         }
      }

   }

   /**
    * Create a model for the given Schema attribute declaration.
    *
    * @param aAttrDecl
    * @return AeAttribute
    */
   public static AeAttribute createAttributeModel(AttributeDecl aAttrDecl)
   {
      AeAttribute aeAttr = new AeAttribute();

      // Set the type.

      SimpleType type;
      if ( aAttrDecl.getReference() != null )
         type = aAttrDecl.getReference().getSimpleType();
      else
         type = aAttrDecl.getSimpleType();

      SimpleType baseType;
      if ( type instanceof Union)
      {
         Union union = (Union)type;
         baseType = (SimpleType)union.getMemberTypes().nextElement();
      }
      else if (type == null)
      {
         // If the type is null, then the type is xsd:anySimpleType
         baseType = aAttrDecl.getSchema().getSimpleType("anySimpleType", IAeConstants.W3C_XML_SCHEMA); //$NON-NLS-1$
      }
      else
      {
         baseType = type.getBuiltInBaseType();
      }

      aeAttr.setDataType(new QName( baseType.getSchema().getSchemaNamespace(), baseType.getName() ));

      String namespace = AeSchemaUtil.getNamespaceURI(aAttrDecl);

      aeAttr.setName( new QName(namespace, aAttrDecl.getName()) );

      aeAttr.setRequired(aAttrDecl.isRequired());
      aeAttr.setOptional(aAttrDecl.isOptional());
      aeAttr.setDefaultValue(aAttrDecl.getDefaultValue());
      aeAttr.setFixedValue(aAttrDecl.getFixedValue());

      // process enumerations.
      List enumerations = new ArrayList();
      if ( type instanceof SimpleType )
      {
         SimpleType simpleType = (SimpleType)type;
         Enumeration enumer = simpleType.getLocalFacets();
         if ( enumer != null )
         {
            while ( enumer.hasMoreElements() )
            {
               Facet facet = (Facet)enumer.nextElement();
               if ( facet.getName().equals(Facet.ENUMERATION) )
                  enumerations.add(facet.getValue());
            }
         }
      }
      aeAttr.setEnumRestrictions(enumerations);

      return aeAttr;
   }

   /**
    * Create a model for the given Schema Group model.
    *
    * @param aGroup
    * @return AeGroup
    */
   public static AeGroup createGroupModel(Group aGroup)
   {
      return new AeGroup();
   }

   /**
    * Create a model for the Schema sequence model group.
    *
    * @param aGroup
    * @return AeSequence
    */
   public static AeSequence createSequenceModel(Group aGroup)
   {
      AeSequence sequence = new AeSequence();
      sequence.setMinOccurs( aGroup.getMinOccurs() );
      sequence.setMaxOccurs( aGroup.getMaxOccurs() );
      return sequence;
   }

   /**
    * Create a model for the Schema choice model group.
    *
    * @param aGroup
    * @return AeChoice
    */
   public static AeChoice creatChoiceModel(Group aGroup)
   {
      AeChoice choice = new AeChoice();
      choice.setMinOccurs( aGroup.getMinOccurs() );
      choice.setMaxOccurs( aGroup.getMaxOccurs() );
      return choice;
   }

   /**
    * Create a model for the Schema all model group.
    *
    * @param aGroup
    * @return AeAll
    */
   public static AeAll createAllModel(Group aGroup)
   {
      AeAll all = new AeAll();
      all.setMinOccurs( aGroup.getMinOccurs() );
      all.setMaxOccurs( aGroup.getMaxOccurs() );
      return all;
   }

   /**
    * Create a model for the Schema any element wildcard (any).
    *
    * @param aWildcard
    * @return AeAny
    */
   public static AeAny createAnyModel(Wildcard aWildcard)
   {
      AeAny any = new AeAny();
      any.setMinOccurs( aWildcard.getMinOccurs() );
      any.setMaxOccurs( aWildcard.getMaxOccurs() );
      any.setName( new QName(getWildcardNamespace(aWildcard), WILDCARD_ELEMENT_NAME) );
      return any;
   }

   /**
    * Returns a list of AeAttributes and AeAnyAttributes defined by the given complex type.
    * @param aComplexType
    * @return List
    */
   private static List createAttributes(ComplexType aComplexType)
   {
      List attribs = new ArrayList();
      Enumeration enumer = aComplexType.getAttributeDecls();
      while ( enumer.hasMoreElements() )
      {
         AttributeDecl attr = (AttributeDecl) enumer.nextElement();
         // Do not create prohibited attributes.
         if ( ! attr.isProhibited() )
         {
            AeAttribute attrib = createAttributeModel(attr);
            QName dataType = attrib.getDataType();
            // Skip attributes of build-in types ID and IDREF.
            if ( ! dataType.equals(AeTypeMapping.XSD_ID) &&
                 ! dataType.equals(AeTypeMapping.XSD_IDREF) )
            {
               attribs.add( createAttributeModel(attr) );
            }
         }
      }

      Wildcard attrAny = aComplexType.getAnyAttribute();
      if ( attrAny != null )
         attribs.add( createAnyAttributeModel(attrAny) );

      return attribs;
   }

   /**
    * Create a model for the Schema anyAttribute wildcard.
    *
    * @param aWildcard
    * @return AeAnyAttribute
    */
   private static AeAnyAttribute createAnyAttributeModel(Wildcard aWildcard)
   {
      AeAnyAttribute anyAttrib = new AeAnyAttribute();
      anyAttrib.setMinOccurs( aWildcard.getMinOccurs() );
      anyAttrib.setMaxOccurs( aWildcard.getMaxOccurs() );
      anyAttrib.setNamespace( getWildcardNamespace(aWildcard) );
      return anyAttrib;
   }

   /**
    * Helper method to set the ae element model details that are common to both complexType and
    * simpleType elements.
    *
    * @param aAeElement
    * @param aElementDecl
    */
   private static void buildElementBase(AeBaseElement aAeElement, ElementDecl aElementDecl)
   {
      handleElementName(aAeElement, aElementDecl);
      aAeElement.setMinOccurs( aElementDecl.getMinOccurs() );
      aAeElement.setMaxOccurs( aElementDecl.getMaxOccurs() );
      aAeElement.setNillable( aElementDecl.isNillable() );
   }

   /**
    * Helper method to get the QName for the given castor element decl.
    *
    * @param aAeElement The element to set the element name and isQualified flag.
    * @param aElementDecl the Element decl source.
    */
   private static void handleElementName(AeBaseElement aAeElement, ElementDecl aElementDecl)
   {
      String namespace = AeSchemaUtil.getNamespaceURI(aElementDecl);
      aAeElement.setName(new QName(namespace, aElementDecl.getName()));
   }

   /**
    * Helper method to return a valid replacement namespace for the given wildcard
    * (any or anyAttribute).
    *
    * @param aWildcard
    * @return String namespace URI.
    */
   private static String getWildcardNamespace(Wildcard aWildcard)
   {
      //todo work in progress, punting for now on wildcards.
      String namespace = null;
      if ( aWildcard.getNamespaces().hasMoreElements() )
      {
         // For now just look at the first namespace if more than one.
         String nsURI = (String)aWildcard.getNamespaces().nextElement();
         if ( AeUtil.notNullOrEmpty(nsURI) )
         {
            if ( ! nsURI.startsWith("##") )                                        //$NON-NLS-1$
            {
               namespace = nsURI;
            }
            else if ( nsURI.equals("##any") )                                     //$NON-NLS-1$
            {
               namespace = aWildcard.getSchema().getTargetNamespace();
            }
            else if ( nsURI.equals("##targetNamespace" ))                         //$NON-NLS-1$
            {

            }
            else if ( nsURI.equals("##other") ) //$NON-NLS-1$
            {
               namespace = aWildcard.getSchema().getTargetNamespace() + "_other"; //$NON-NLS-1$
            }
            else if ( nsURI.equals("##local") )                                  //$NON-NLS-1$
            {
               namespace = null;  // unqualified elements may appear.
            }
         }
      }
      return namespace;
   }
}

