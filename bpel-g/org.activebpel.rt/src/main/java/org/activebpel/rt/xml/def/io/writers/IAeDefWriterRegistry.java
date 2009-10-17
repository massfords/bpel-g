// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/writers/IAeDefWriterRegistry.java,v 1.2 2007/10/16 16:56:54 jbik Exp $
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.def.io.writers;

import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Defines a Def Writer Registry.
 */
public interface IAeDefWriterRegistry
{
   /**
    * Retrieve the writer class for the AeDef object.
    * 
    * @param aParentClass the parent class
    * @param aDef the base def object to be serialized
    * @return the appropriate writer
    */
   public IAeDefWriter getWriter(Class aParentClass, AeBaseXmlDef aDef);
   
   /**
    * @param aDef
    * @return true if the aDef passed is supported by the registry
    */
   public boolean isSupported(AeBaseXmlDef aDef);
}
