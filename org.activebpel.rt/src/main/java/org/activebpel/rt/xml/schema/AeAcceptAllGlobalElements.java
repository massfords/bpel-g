package org.activebpel.rt.xml.schema;

import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.SimpleType;
import org.exolab.castor.xml.schema.XMLType;

/**
 * Accepts all global elements that are complex
 */
public class AeAcceptAllGlobalElements implements IAeElementFilter
{
   /**
    * Returns true if the element is a global element of a complex type.
    * 
    * @see org.activebpel.rt.xml.schema.IAeElementFilter#accept(org.exolab.castor.xml.schema.ElementDecl)
    */
   public boolean accept(ElementDecl aElementDecl)
   {
      if (aElementDecl.getParent() == aElementDecl.getSchema())
      {
         XMLType type = aElementDecl.getReference() != null ? aElementDecl.getReference().getType() : aElementDecl.getType();
         return type instanceof ComplexType || type instanceof SimpleType;
      }
      return false;
   }
}