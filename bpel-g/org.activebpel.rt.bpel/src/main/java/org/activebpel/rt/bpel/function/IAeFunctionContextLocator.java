//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/function/IAeFunctionContextLocator.java,v 1.1 2005/06/08 12:50:28 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.function;

import org.activebpel.rt.bpel.impl.function.AeInvalidFunctionContextException;

/**
 * Interface for class responsible for locating, loading and instantiating
 * <code>IAeFunctionContext</code> impls.
 */
public interface IAeFunctionContextLocator
{
   /**
    * Find, load, instantiate and return the <code>IAeFunctionContext</code> impl.
    * 
    * @param aNamespace The namespace of the function context.
    * @param aLocation A location hint for locating the <code>IAeFunctionContext</code>class.  This may be null or empty.
    * @param aClassName The class name of the <code>IAeFunctionContext</code> impl.
    * @throws AeInvalidFunctionContextException
    */
   public IAeFunctionContext locate( String aNamespace, String aLocation, String aClassName ) throws AeInvalidFunctionContextException;
}
