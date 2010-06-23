// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/readers/IAeDefReader.java,v 1.1 2007/09/26 02:17:16 mford Exp $
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
 * Interface for working with AeDef object readers.  Readers
 * are responsible for creating their AeDef object counterpart,
 * setting the properties on def object based on element attributes
 * and adding the def to its parent object (via the IAeReaderContext).
 */
public interface IAeDefReader
{
   /**
    * Deserialize the current element to it def type and add it to the parent def.
    * 
    * @param aParent add child to this def
    * @param aElement to be deserialized
    * @return newly created AeDef object
    * @throws AeException
    */
   public AeBaseXmlDef read(AeBaseXmlDef aParent, Element aElement) throws AeException;
}
