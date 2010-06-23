//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/IAeNamespaceContext.java,v 1.1 2006/09/27 19:54:36 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml;

import java.util.Set;

/**
 * This interface defines a standard expression namespace context.  This namespace context
 * is used by expression runners during expression execution.
 */
public interface IAeNamespaceContext
{
   /**
    * Resolves a namespace prefix into a namespace URI.  This method should return null if the namespace
    * could not be found.
    * 
    * @param aPrefix The prefix to resolve.
    */
   public String resolvePrefixToNamespace(String aPrefix);

   /**
    * Resolves a namespace to its list of prefixes (each namespace could be mapped to 0 or more prefix).
    * 
    * @param aNamespace
    */
   public Set resolveNamespaceToPrefixes(String aNamespace);

}
