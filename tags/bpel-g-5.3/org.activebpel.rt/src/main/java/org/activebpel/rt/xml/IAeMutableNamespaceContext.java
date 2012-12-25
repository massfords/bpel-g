//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/IAeMutableNamespaceContext.java,v 1.4 2007/03/14 14:25:38 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml; 

/**
 * Provides a way of resolving a namespace to a prefix or creating a new ns mapping.
 */
public interface IAeMutableNamespaceContext extends IAeNamespaceContext
{
   /**
    * Gets the prefix mapped to the given namespace or creates a new one. If the preferred
    * prefix is supplied then this will serve as the new prefix mapping or the base of the 
    * mapping if already mapped to another namespace.
    * 
    * Note: By default, this method will return a prefix even if the namespace
    *       matches the default declared namespace. This is useful for encoding
    *       QNames as attribute or element values.
    * 
    * @param aPreferredPrefix - optional preferred prefix
    * @param aNamespace
    */
   public String getOrCreatePrefixForNamespace(String aPreferredPrefix, String aNamespace);

   /**
    * Gets the prefix mapped to the given namespace or creates a new one. If the preferred
    * prefix is supplied then this will serve as the new prefix mapping or the base of the 
    * mapping if already mapped to another namespace.
    * @param aPreferredPrefix - optional preferred prefix
    * @param aNamespace
    * @param aAllowDefaultNamespace - if true, then we'll return "" if the
    *        namespace matches the declared default namespace. If false, then
    *        a prefix will be created even if it matches the default namespace.
    *        This is useful for encoding QNames.
    */
   public String getOrCreatePrefixForNamespace(String aPreferredPrefix, String aNamespace, boolean aAllowDefaultNamespace);
}
 