// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/writers/IAeDefWriterFactory.java,v 1.1 2007/09/26 02:17:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.def.io.writers;

import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.w3c.dom.Element;

/**
 * A factory interface for creating def writers.
 */
public interface IAeDefWriterFactory
{
   /**
    * Creates a writer def visitor that the dispatch writer can dispatch to.
    * 
    * @param aDef
    * @param aParentElement
    * @param aNamespaceUri
    * @param aTagName
    */
   public IAeDefWriter createDefWriter(AeBaseXmlDef aDef, Element aParentElement,
         String aNamespaceUri, String aTagName);
}
