// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/IAeDefRegistry.java,v 1.2 2007/10/04 20:45:42 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.io;

import javax.xml.namespace.QName;

import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.io.readers.IAeDefReader;
import org.activebpel.rt.xml.def.io.writers.IAeDefWriter;

/**
 * Interface for retrieving AeDef object serializers and
 * deserializers.
 */
public interface IAeDefRegistry
{
   /**
    * Retrieve the appropriate reader based on the parent
    * def object and QName mapping.
    * @param aParentDef
    * @param aQName
    * @return IAeBpelDefReader impl
    */
   public IAeDefReader getReader( AeBaseXmlDef aParentDef, QName aQName );

   /**
    * Returns the extension reader configured for the registry.
    */
   public IAeDefReader getExtensionReader();
   
   /**
    * Sets the extension reader
    * @param aExtensionReader 
    */
   public void setExtensionReader(IAeDefReader aExtensionReader);
   
   /**
    * Retrieve the appropriate writer based on the parent class
    * and the current AeDef object.
    * @param aParentClass
    * @param aDef
    * @return IAeBpelDefWriter impl
    */
   public IAeDefWriter getWriter( Class aParentClass, AeBaseXmlDef aDef );
}
