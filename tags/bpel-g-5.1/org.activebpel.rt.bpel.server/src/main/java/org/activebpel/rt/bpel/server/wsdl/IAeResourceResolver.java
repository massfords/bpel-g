//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/wsdl/IAeResourceResolver.java,v 1.2 2007/05/10 21:18:22 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.wsdl;

import java.io.IOException;

import org.xml.sax.InputSource;

/**
 * Interface for defining a resource resolver.  A resource resolver is
 * responsible for finding and loading resources by location hints.
 */
public interface IAeResourceResolver
{
   /**
    * Return the InputSource for the resource deployment (or null if none is
    * found).
    * @param aLocationHint
    */
   public InputSource getInputSource(String aLocationHint) throws IOException;

   /**
    * Returns true if the resolver contains a mapping for the location hint.
    * @param aLocationHint
    */
   public boolean hasMapping(String aLocationHint);

}