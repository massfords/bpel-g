//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/readers/AeExtensionElementReader.java,v 1.1 2008/02/29 23:40:23 vvelusamy Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.io.readers;

import org.activebpel.rt.AeException;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;
import org.w3c.dom.Element;

/**
 * Special reader for reading in extension elements.
 */
public class AeExtensionElementReader implements IAeDefReader
{
   /**
    * @see org.activebpel.rt.xml.def.io.readers.IAeDefReader#read(org.activebpel.rt.xml.def.AeBaseXmlDef, org.w3c.dom.Element)
    */
   public AeBaseXmlDef read(AeBaseXmlDef aParent, Element aElement) throws AeException
   {
      AeExtensionElementDef extElemDef = new AeExtensionElementDef(aElement);
      extElemDef.setParentXmlDef(aParent);
      aParent.addExtensionElementDef(extElemDef);
      return extElemDef;
   }
}