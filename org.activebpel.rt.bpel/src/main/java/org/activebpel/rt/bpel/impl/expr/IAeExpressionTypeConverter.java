//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/expr/IAeExpressionTypeConverter.java,v 1.2 2006/07/10 16:32:47 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.expr;


/**
 * This interface provides type conversion functionality.  Each expression language implementation 
 * may need to convert data of certain Java types into other Java types.  For example, the Jaxen XPath
 * implementation needs to convert BigInteger and Float objects to Doubles.
 */
public interface IAeExpressionTypeConverter
{
   /**
    * Converts the given engine type (an object coming from the BPEL engine) to a type that
    * can be properly handled by the expression language library.
    * 
    * @param aEngineType
    */
   public Object convertToExpressionType(Object aEngineType);
   
   /**
    * Converts the given expression type (an object created by the expression language library
    * while executing the expression) to a new type that can be used by the BPEL engine.
    * 
    * @param aExpressionType
    */
   public Object convertToEngineType(Object aExpressionType);
}
