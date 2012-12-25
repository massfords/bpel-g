package org.activebpel.rt.xml.schema;

import org.exolab.castor.xml.schema.ComplexType;

/**
 * interface for filtering complex types
 */
public interface IAeComplexTypeFilter
{
   /**
    * Returns true if the complex type passes the filter
    * @param aComplexType
    */
   public boolean accept(ComplexType aComplexType);
}