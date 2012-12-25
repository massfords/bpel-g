//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/function/IAeFunctionContext.java,v 1.1 2005/06/08 12:50:28 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.function;


/**
 * Interface for <code>IAeFunctionContext</code> impls.
 */
public interface IAeFunctionContext
{
   /**
    * An implementation should return a <code>IAeFunction</code> implementation object
    * based on the name of the function requested.
    *
    * <p>
    * It must not use the prefix parameter to select an implementation,
    * because a prefix could be bound to any namespace; the prefix parameter
    * could be used in debugging output or other generated information.
    * The prefix may otherwise be completely ignored.
    * </p>
    * 
    * @param aFunctionName The name of the function
    * @return  a IAeFunction implementation object.
    * @throws AeUnresolvableException when the function cannot be found.
    */
   public IAeFunction getFunction(String aFunctionName) throws AeUnresolvableException;

}
