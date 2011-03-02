package org.activebpel.rt.xml.schema;

import org.exolab.castor.xml.schema.ComplexType;

/**
 * Accepts compatibles types. A type is deemed to be compatible if any of the 
 * following are true:
 * - type is the same as the target's ComplexType member data
 * - type is derived from the target's ComplexType
 * 
 * produces a List of compatible ComplexType objects.
 */
public class AeAcceptAllCompatibleComplexTypes implements IAeComplexTypeFilter
{
   /** provides context for determining compatibility */
   private ComplexType mComplexType;
   
   /**
    * Ctor
    * @param aComplexType
    */
   public AeAcceptAllCompatibleComplexTypes(ComplexType aComplexType)
   {
      setComplexType(aComplexType);
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.IAeComplexTypeFilter#accept(org.exolab.castor.xml.schema.ComplexType)
    */
   public boolean accept(ComplexType aComplexType)
   {
      // rule one: type is the same as the target's ComplexType member data
      if (aComplexType == getComplexType())
         return true;
      
      // rule two: type is derived from the target's ComplexType
      if (AeSchemaUtil.isTypeDerivedFromType(aComplexType, getComplexType()))
      {
         return true;
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
}