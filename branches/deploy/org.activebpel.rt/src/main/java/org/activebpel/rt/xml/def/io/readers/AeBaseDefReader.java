// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/readers/AeBaseDefReader.java,v 1.2 2007/10/17 18:01:12 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.io.readers;

import org.activebpel.rt.AeException;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.w3c.dom.Element;

/**
 * Simple base class for AeBaseXmlDef object readers.
 * Subclasses provide behavior for creating AeDef object impls
 * and configuring them appropriately.
 */
abstract public class AeBaseDefReader implements IAeDefReader
{
   /**
    * @see org.activebpel.rt.xml.def.io.readers.IAeDefReader#read(org.activebpel.rt.xml.def.AeBaseXmlDef, org.w3c.dom.Element)
    */
   public AeBaseXmlDef read(AeBaseXmlDef aParent, Element aElement) throws AeException
   {
      AeBaseXmlDef childDef = createChild( aParent, aElement );
      if (configureChild( aParent, childDef, aElement ))
         return childDef;
      return null;
   }
   
   /**
    * Create the new AeBaseDef object.
    * @param aParent provided in the case of containers.
    * @param aElement the Element being read
    * @return appropriate AeBaseDef type
    * @throws AeException
    */
   protected abstract AeBaseXmlDef createChild(AeBaseXmlDef aParent, Element aElement) throws AeException;
   
   /**
    * It is expected that reader impls will extract attribute
    * (and possibly other) data from element to set properties
    * on the new def object and then add it to its parent in
    * the appropriate manner.
    * @param aParentDef
    * @param aNewDef
    * @param aElement
    * @throws AeException
    */
   protected abstract boolean configureChild( AeBaseXmlDef aParentDef, AeBaseXmlDef aNewDef, Element aElement ) throws AeException;
}
