// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/IAeWSResourceStandardNamespaces.java,v 1.1 2007/12/17 16:41:42 ckeller Exp $
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation;

import java.util.Collection;

import org.activebpel.rt.xml.IAeNamespaceContext;


/**
 * Interface used to resolve standard WS-Resource namespace mappings. It can do
 * both prefix-to-namespace <b>and</b> namespace-to-prefix.
 */
public interface IAeWSResourceStandardNamespaces extends IAeNamespaceContext
{
   /**
    * Resolves a namespace prefix into a namespace URI. This method should
    * return null if the namespace could not be found.
    *
    * @param aPrefix The prefix to resolve.
    */
   public String resolvePrefixToNamespace(String aPrefix);

   /**
    * Resolves a namespace to its prefix.
    *
    * @param aNamespace
    */
   public String resolveNamespaceToPrefix(String aNamespace);

   /**
    * Gets all of the standard namespaces.
    */
   public Collection getAllNamespaces();
}
