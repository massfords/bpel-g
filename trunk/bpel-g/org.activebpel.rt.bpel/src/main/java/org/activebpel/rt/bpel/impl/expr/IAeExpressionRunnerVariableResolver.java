//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/expr/IAeExpressionRunnerVariableResolver.java,v 1.1 2007/11/01 18:23:52 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.expr;

import org.activebpel.rt.bpel.IAeVariableView;
import org.activebpel.rt.bpel.impl.AeUninitializedVariableException;

/**
 * Defines a common interface for finding variables and variable
 * data with in a expression runner implementation.
 */
public interface IAeExpressionRunnerVariableResolver
{
   /**
    * Finds and returns variable given name or returns <code>null</code> if not found.
    * @param aName variable name.
    * @return variable instance or null.
    */
   public IAeVariableView findVariable(String aName);

   /**
    * Returns true if the variable exists.
    * @param aName
    * @return true if variable is found.
    */
   public boolean variableExists(String aName);


   /**
    * Returns variable data.
    * @param aName
    * @return variable data.
    */
   public Object getVariableData(String aName) throws AeUninitializedVariableException;

}
