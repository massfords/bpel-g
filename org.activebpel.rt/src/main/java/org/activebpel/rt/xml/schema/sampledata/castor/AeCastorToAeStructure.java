//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/sampledata/castor/AeCastorToAeStructure.java,v 1.7 2008/02/17 21:09:21 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.schema.sampledata.castor; 

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.schema.AeSchemaUtil;
import org.activebpel.rt.xml.schema.sampledata.IAeSampleDataPreferences;
import org.activebpel.rt.xml.schema.sampledata.structure.AeBaseElement;
import org.activebpel.rt.xml.schema.sampledata.structure.AeComplexElement;
import org.activebpel.rt.xml.schema.sampledata.structure.AeGroup;
import org.activebpel.rt.xml.schema.sampledata.structure.AeStructure;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.ModelGroup;
import org.exolab.castor.xml.schema.Order;
import org.exolab.castor.xml.schema.Particle;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.Wildcard;
import org.exolab.castor.xml.schema.XMLType;

/**
 *  Creates an AE schema tree model (AeStructure) from a Castor XML Schema ElementDecl object.
 *  The ElementDecl may be of simple or complex type.
 */
public class AeCastorToAeStructure
{
   /** The top-level parent structure object. */
   private AeStructure mRoot;

   /** The current structure object being processed. */
   private AeStructure mCurrent;
   
   /** Stack for regulating recursion depth. */
   private Stack mCastorStack = new Stack();

   /** Sample data preferences. */
   private IAeSampleDataPreferences mPreferences;
   
   /**
    * Generates an AE structure object which models the given Castor XML Schema ElementDecl.
    * 
    * @param aElementDecl
    * @param aPreferences
    * @return AeStructure
    */
   public static AeStructure build(ElementDecl aElementDecl, IAeSampleDataPreferences aPreferences)
   {
      AeCastorToAeStructure gen = new AeCastorToAeStructure(aPreferences);
      gen.handleElementDecl(aElementDecl);
      return gen.getRoot();
   }
   
   /**
    * Creates a structure for the given ComplexType.
    * @param aComplexType
    * @param aPreferences
    */
   public static AeStructure build(ComplexType aComplexType, IAeSampleDataPreferences aPreferences)
   {
      AeCastorToAeStructure gen = new AeCastorToAeStructure(aPreferences);
      gen.handleRootComplexType(aComplexType);
      return gen.getRoot();
   }
   
   /**
    * Ctor 
    * @param aPreferences
    */
   public AeCastorToAeStructure(IAeSampleDataPreferences aPreferences)
   {
      setPreferences(aPreferences);
   }

   /**
    * Push the given Castor object onto the stack and make the corresponding 
    * AeStructure the current model being processed.
    * 
    * @param aStructure AE structure object representing the Castor object.
    * @param aObject Castor object being processed.
    */
   private void push(AeStructure aStructure, Object aObject)
   {
      AeStructure parent = peekStructure();
      if (parent != null)
      {
         parent.addChild(aStructure);
      }
      else
      {
         mRoot = aStructure;
      }
      mCastorStack.push(aObject);
      mCurrent = aStructure;
   }
   
   /**
    * Removes the Castor object from the top of the stack and makes the current AeStructure's
    * parent the current AeStructure.
    */
   private void pop()
   {
      mCastorStack.pop();
      mCurrent = mCurrent.getParent();
   }
   
   /**
    * Returns the current AeStructure being processed.
    * 
    * @return AeStructure.
    */
   private AeStructure peekStructure()
   {
      return mCurrent;
   }
   
   /**
    * Processes a Schema Element Decl and its children.
    * 
    * @param aElementDecl
    */
   protected void handleElementDecl(ElementDecl aElementDecl)
   {
      AeBaseElement element = AeCastorStructureFactory.createElement(aElementDecl);
      
      XMLType type = aElementDecl.getType();
      
      push(element, type);
      try
      {
         if (element.isAbstractElement())
         {
            // this element is abstract so we won't be generating any sample data
            // for it so there is no reason to visit its type. We will still have
            // to visit the SG elements in order to produce structures for their
            // types.
            List sgElements = getSubstitutionGroupMembers(aElementDecl);
            for (Iterator iter = sgElements.iterator(); iter.hasNext();)
            {
               ElementDecl sgElement = (ElementDecl) iter.next();
               handleElementDecl(sgElement);
            }
         }
         else
         {
            handleType(type);
         }
      }
      finally
      {
         pop();
      }
   }

   /**
    * Creates an element structure in the default namespace with an xsi:type
    * attribute for this complex type.
    * @param aComplexType
    */
   protected void handleRootComplexType(ComplexType aComplexType)
   {
      AeBaseElement element = AeCastorStructureFactory.createElement(aComplexType);
      
      push(element, aComplexType);
      try
      {
         handleType(aComplexType);
      }
      finally
      {
         pop();
      }
   }

   /**
    * Handle the given type (complexType, simpleType, anyType)
    * @param aType
    */
   protected void handleType(XMLType aType)
   {
      // type could be null in schemas w/ bugs in it. We'll try to handle as best we can.
      if (aType == null)
         return;
      
      if (aType.isComplexType())
      {
         ComplexType complexType = (ComplexType) aType;
         
         if (shouldTraverse(complexType))
         {
            if ( complexType.isAbstract() )
            {
               // This type is abstract so it will never be instantiated.
               // Instead, we should locate all of the possible derived types
               // and include them.
               Schema schema = complexType.getSchema();
               for(Enumeration enoom = schema.getComplexTypes(); enoom.hasMoreElements();)
               {
                  ComplexType type = (ComplexType) enoom.nextElement();
                  if (type != complexType && !type.isAbstract() && AeSchemaUtil.isTypeDerivedFromType(type, complexType))
                  {
                     // create structure to hold particles
                     // push structure
                     // call handleType(type)
                     // pop
                     AeComplexElement complexElement = new AeComplexElement();
                     AeCastorStructureFactory.populateComplexElement(complexElement, type);
                     complexElement.setName(new QName(type.getSchema().getTargetNamespace(), type.getName()));
                     push(complexElement, null);
                     try
                     {
                        handleType(type);
                     }
                     finally
                     {
                        pop();
                     }
                     
                  }
               }
            }
            else
            {
               List particles = new ArrayList();
               
               addAllInheritedParticles(complexType, particles);

               // iterate over all inherited particles plus our particles (inherited first)
               for(Iterator it=particles.iterator(); it.hasNext(); )
               {
                  Particle particle = (Particle)it.next();
                  handleParticle(particle);
               }
            }
         }
         else
         {
            // Remove current structure from its parent as the recursion limit
            // has been met so we don't want to include this structure. 
            AeStructure parent = mCurrent.getParent();
            parent.getChildren().remove(mCurrent);
         }
      }
      else if (aType.isAnyType())
      {
         // do nothing, we already created an any struct and put it on the stack
      }
      else if (aType.isSimpleType())
      {
         // do nothing, we already created a simple struct and put it on the stack
      }
   }
   
   /**
    * Using recursion, adds all particles and inherited particles to the given list for the 
    * given complexType.
    * 
    * @param aType
    * @param aParticles
    */
   protected void addAllInheritedParticles(ComplexType aType, List aParticles)
   {
      // load any particles from the parent
      if(aType.getBaseType() != null && aType.getBaseType() instanceof ComplexType && !aType.isRestricted())
      {
         addAllInheritedParticles((ComplexType)aType.getBaseType(), aParticles);
      }

      // add all of our particles to list
      for(int i=0; i<aType.getParticleCount(); i++)
      {
         Particle particle = aType.getParticle(i);
         aParticles.add(particle);
      }
   }

   /**
    * Process the given particle (element, group or wildcard).
    * @param particle
    */
   protected void handleParticle(Particle particle)
   {
      if (particle instanceof Group)
      {
         handleGroup((Group) particle);
      } 
      else if (particle instanceof ElementDecl)
      {
         handleElementDecl((ElementDecl) particle);
      }
      else if (particle instanceof Wildcard)
      {
         handleWildcard((Wildcard)particle);
      }
   }
   
   /**
    * Process the given Schema Group.
    * @param aGroup
    */
   protected void handleGroup(Group aGroup)
   {
      boolean isRef = AeUtil.notNullOrEmpty(aGroup.getReferenceId());
      
      if (isRef)
      {
         AeGroup group = (AeGroup)AeCastorStructureFactory.createGroupModel(aGroup);
         push(group, aGroup);
      }
      
      try
      {
         if ( shouldTraverse(aGroup) )  // Recursion check
         {
            if ( (aGroup instanceof ModelGroup) )
            {
               handleGroupParticles(aGroup);  // <xsd:group>
            }
            else
            {
               AeStructure structure = null;
               short groupType = aGroup.getOrder().getType();
               
               if ( groupType == Order.SEQUENCE )
                  structure = AeCastorStructureFactory.createSequenceModel(aGroup);
               else if ( groupType == Order.CHOICE)
                  structure = AeCastorStructureFactory.creatChoiceModel(aGroup);
               else // Order.ALL 
                  structure = AeCastorStructureFactory.createAllModel(aGroup);
   
               push(structure, null);
               try
               {
                  handleGroupParticles(aGroup);
               }
               finally
               {
                  pop();
               }
            }
         }
      }
      finally
      {
         if (isRef)
         {
            pop();
         }
      }
   }

   /**
    * Process the given Schema Group's child particles.
    * @param aGroup
    */
   protected void handleGroupParticles(Group aGroup)
   {
      for(int i=0; i<aGroup.getContentModelGroup().getParticleCount(); i++)
      {
         Particle particle = aGroup.getContentModelGroup().getParticle(i);
         handleParticle(particle);
      }
   }
   
   /**
    * Determines if the recursion limit has been met for the given object.
    * 
    * @param aObject the object to be traversed.
    * @return boolean true if can traverse, false should not traverse.
    */
   private boolean shouldTraverse(Object aObject)
   {
      return getRecursionDepth(aObject) <= getPreferences().getRecursionLimit();
   }
   
   /**
    * Gets the current recursion depth for the given object.
    * 
    * @param aObject
    * @return int.
    */
   protected int getRecursionDepth(Object aObject)
   {
      // Walk the stack from bottom to top keeping track of how many times the given 
      // object appears in the stack.
      int depth = 0;
      for ( int i = mCastorStack.size()-1; i > 0; i-- )
      {
         if (AeUtil.compareObjects(mCastorStack.get(i), aObject))
            depth++;
      }
      return depth;
   }
   
   /**
    * Handle Schema Wildcard.
    */
   protected void handleWildcard(Wildcard aWildcard)
   {
      push(AeCastorStructureFactory.createAnyModel(aWildcard), null);
      pop();
   }
   
   /**
    * Returns the top-level root structure object.
    * 
    * @return AeStructure.
    */
   public AeStructure getRoot()
   {
      return mRoot;
   }

   /**
    * @return the mPreferences
    */
   protected IAeSampleDataPreferences getPreferences()
   {
      return mPreferences;
   }

   /**
    * @param aPreferences
    */
   private void setPreferences(IAeSampleDataPreferences aPreferences)
   {
      mPreferences = aPreferences;
   }

   /**
    * Returns all of the SG members for this element. Not using the method on
    * the elementDecl (getSubstitutionGroupMembers) since it doesn't handle the
    * QName check correctly.
    * @param aElementDecl
    */
   protected static List getSubstitutionGroupMembers(ElementDecl aElementDecl)
   {
      // TODO (JP) need to check for sg elements in imported schemas
      ElementDecl elementDecl = aElementDecl;
      if (aElementDecl.getReference() != null)
      {
         elementDecl = aElementDecl.getReference();
      }

      QName headQName = new QName(elementDecl.getSchema().getTargetNamespace(), elementDecl.getName());
      List result = new LinkedList();
      Enumeration enumeration = elementDecl.getSchema().getElementDecls();
      while (enumeration.hasMoreElements())
      {
         ElementDecl temp = (ElementDecl) enumeration.nextElement();
         String subName = temp.getSubstitutionGroup();
         // might not be a SG element
         if (subName != null)
         {
            String prefix = AeXmlUtil.extractPrefix(subName);
            String localPart = AeXmlUtil.extractLocalPart(subName);
            String ns = null;
            if (prefix != null)
            {
               ns = temp.getSchema().getNamespace(prefix);
            }
            else
            {
               ns = temp.getSchema().getTargetNamespace();
            }
            QName qname = new QName(ns, localPart);
            if (headQName.equals(qname))
            {
               result.add(temp);
            }
         }
      }
      return result;
   }
}
 