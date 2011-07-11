// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/writers/IAeDefWriter.java,v 1.1 2007/09/26 02:17:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.io.writers;

import org.activebpel.rt.xml.def.AeBaseXmlDef;

import org.w3c.dom.Element;

/**
 * Interface for working with AeDef object serializers.
 */
public interface IAeDefWriter
{
   /**
    * Serialize the AeDef object and add it to the parent element.
    * @param aBaseDef the AeDef obj to serialize
    * @param aParentElement
    * @return BPEL element (already added to the parent)
    */
   public Element createElement(AeBaseXmlDef aBaseDef, Element aParentElement );

}
