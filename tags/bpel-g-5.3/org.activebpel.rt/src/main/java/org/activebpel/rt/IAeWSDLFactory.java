// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/IAeWSDLFactory.java,v 1.4 2006/06/26 16:46:46 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt;

import org.xml.sax.InputSource;

/**
 * Interface to define a factory mechanism for obtaining a WSDL source
 */
public interface IAeWSDLFactory
{
   /**
    * Gets the InputStream for passed wsdl file after first checking catalog for local
    * access (via classpath) to passed url. The WSDL URL should be an absolute path to
    * the resource.
    * @param aWsdlUrl The url of the wsdl to load.
    * @return the InputStream representing the passed wsdl url.
    * @throws AeException If a failure is encountered loading the WSDL Document.
    */
   public InputSource getWSDLSource(String aWsdlUrl) throws AeException;
   
   /**
    * Returns the wsdl source for a passed namespace or null if none.
    * @param aNamespace The namespace to retrieve the input source for.
    */
   public InputSource getWSDLForNamespace(String aNamespace) throws AeException;
   
   /**
    * Returns the url for the wsdl source for a passed namespace or null if none.
    * @param aNamespace The namespace to retrieve the input source for.
    */
   public String getWSDLLocationForNamespace(String aNamespace) throws AeException;
}
