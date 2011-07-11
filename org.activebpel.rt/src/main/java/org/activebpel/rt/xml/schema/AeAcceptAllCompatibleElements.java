package org.activebpel.rt.xml.schema;

import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ElementDecl;

/**
 * Accepts compatible elements. An element is deemed to be compatible if any 
 * of the following are true:
 * - element is the same as the target's ElementDecl member data
 * - element is an SG element for the target's ElementDecl
 * - element's type is the same type as the target's ComplexType
 * - element's type is derived from the target's ComplexType
 * 
 * produces a list of compatible Element objects.
 */
public class AeAcceptAllCompatibleElements extends AeAcceptAllGlobalElements
{
   /** element the provides the context for determining compatible elements */
   private ElementDecl mElement;
   /** complex type that provides the context for determining compatible elements */
   private ComplexType mComplexType;
   
   /**
    * Ctor
    * @param aElementDecl - provides context for determining compatibility
    * @param aComplexType - provides context for determining compatibility
    */
   public AeAcceptAllCompatibleElements(ElementDecl aElementDecl, ComplexType aComplexType)
   {
      setElement(aElementDecl);
      setComplexType(aComplexType);
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.AeAcceptAllGlobalElements#accept(org.exolab.castor.xml.schema.ElementDecl)
    */
   public boolean accept(ElementDecl aElementDecl)
   {
      boolean isValid = super.accept(aElementDecl);
      
      if (isValid)
      {
         // first rule: element is the same as the target's ElementDecl member data
         if (aElementDecl == getElement())
            return true;
         
         // second rule: element is an SG element for the target's ElementDecl
         if (AeSchemaUtil.getSubstitutionGroupLevel(getElement(), aElementDecl) > 0 )
         {
            return true;
         }
         
         if (getComplexType() != null)
         {
            // third rule: element's type is the same type as the target's ComplexType
            if (aElementDecl.getType() == getComplexType())
            {
               return true;
            }
            // fourth rule: element's type is derived from the target's ComplexType
            else if (aElementDecl.getType() instanceof ComplexType && AeSchemaUtil.isTypeDerivedFromType((ComplexType)aElementDecl.getType(), getComplexType()))
            {
               return true;
            }
         }
      }
      
      return false;
   }

   /**
    * @return the complexType
    */
   public ComplexType getComplexType()
   {
      return mComplexType;
   }

   /**
    * @param aComplexType the complexType to set
    */
   public void setComplexType(ComplexType aComplexType)
   {
      mComplexType = aComplexType;
   }

   /**
    * @return the element
    */
   public ElementDecl getElement()
   {
      return mElement;
   }

   /**
    * @param aElement the element to set
    */
   public void setElement(ElementDecl aElement)
   {
      mElement = aElement;
   }
   
}