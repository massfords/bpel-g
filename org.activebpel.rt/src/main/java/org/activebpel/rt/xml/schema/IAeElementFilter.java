package org.activebpel.rt.xml.schema;

import org.exolab.castor.xml.schema.ElementDecl;

/**
 * interface for filtering elements
 */
public interface IAeElementFilter
{
   /**
    * Returns true if the element passes the filter
    * @param aElement
    */
   public boolean accept(ElementDecl aElement);
}