//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/function/IAeFunctionFactory.java,v 1.2 2007/12/28 00:03:57 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.function;

import java.util.Set;

/**
 * This interface represents a factory that will create instances of IAeFunction from a
 * given namespace and function name.  Typically an implementation will contain multiple
 * function contexts, each function context mapped to one or more namespaces.  A call
 * to getFunction will then look up the correct function context based on the namespace 
 * and then ask that context for the function.  In addition, this interface can be used
 * to enumerate all of the namespaces for which it has function contexts.
 */
public interface IAeFunctionFactory
{
   /**
    * Looks up a function given the namespace and function name.  The impl should locate a function
    * context that is configured for the given namespace and then ask that context for a function of
    * the given name.  Throws an unresolvable exception only if the context is found but there is no
    * function in the context with the given name.  If no context is found, then null is returned.
    * 
    * @param aNamespace
    * @param aFunctionName
    * @throws AeUnresolvableException only if the context is found but there is no function in the context with the given name
    */
   public IAeFunction getFunction(String aNamespace, String aFunctionName) throws AeUnresolvableException;

   /**
    * Gets a list of all namespaces for which function contexts are configured.  A list of Strings is
    * returns (each namespace is a String in the List).
    */
   public Set getFunctionContextNamespaceList();
   
}
