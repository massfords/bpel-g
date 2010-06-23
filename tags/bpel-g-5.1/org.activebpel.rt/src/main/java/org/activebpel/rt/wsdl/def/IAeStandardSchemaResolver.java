//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/IAeStandardSchemaResolver.java,v 1.2 2007/12/14 01:03:41 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def;

import org.xml.sax.InputSource;

/**
 * Implementors of this interface will resolve "standard" or "well known" schemas given
 * the namespace of the schema.  For example, the Soap Encoding schema found at 
 * "http://schemas.xmlsoap.org/soap/encoding/" will be resolved to a valid URL object.
 * The list of well known schemas is dynamic and implementation dependent.
 */
public interface IAeStandardSchemaResolver
{
   /**
    * Resolves a "standard" schema by namespace.
    * 
    * @param aNamespace
    */
   public InputSource resolve(String aNamespace);
   
   /**
    * Returns true if the resolver is capable of resolving the namespace
    * @param aNamespace
    */
   public boolean canResolve(String aNamespace);
}
