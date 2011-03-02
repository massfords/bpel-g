// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/readers/IAeDefReaderRegistry.java,v 1.2 2007/10/17 18:01:12 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.def.io.readers;

import javax.xml.namespace.QName;

import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Defines a Def reader registry.
 */
public interface IAeDefReaderRegistry
{
   /**
    * Return the appropriate IAeDefReader impl for this
    * parent def and QName mapping.
    * 
    * @param aParentDef parent AeBaseXmlDef in the object model
    * @param aElementQName the child element QName
    * @return IAeDefReader impl for deserializing this element or null if not found
    */
   public IAeDefReader getReader(AeBaseXmlDef aParentDef, QName aElementQName)
         throws UnsupportedOperationException;

   /**
    * Gets a reader to read an extension
    */
   public IAeDefReader getExtensionReader();
}
