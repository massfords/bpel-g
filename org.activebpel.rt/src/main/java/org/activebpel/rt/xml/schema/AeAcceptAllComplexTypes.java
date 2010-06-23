package org.activebpel.rt.xml.schema;

import org.exolab.castor.xml.schema.ComplexType;

/**
 * Accepts all global complex types 
 */
public class AeAcceptAllComplexTypes implements IAeComplexTypeFilter
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeComplexTypeFilter#accept(org.exolab.castor.xml.schema.ComplexType)
    */
   public boolean accept(ComplexType aComplexType)
   {
      return aComplexType.getParent() == aComplexType.getSchema();
   }
}