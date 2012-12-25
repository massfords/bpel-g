//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr.bsf/src/org/activebpel/rt/bpel/ext/expr/bsf/impl/python/AeBSFPythonExpressionRunner.java,v 1.2 2006/07/10 16:42:26 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.bsf.impl.python;

import org.activebpel.rt.bpel.ext.expr.bsf.impl.AeBSFExpressionRunner;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionTypeConverter;

/**
 * Implements a Python expression runner by extending the BSF expression runner. This trivial extension simply
 * provides the BSF engine type to use and provides a type converter.
 */
public class AeBSFPythonExpressionRunner extends AeBSFExpressionRunner
{
   /**
    * Overrides method to supply the python impl's BSF engine type.
    * 
    * @see org.activebpel.rt.bpel.ext.expr.bsf.impl.AeBSFExpressionRunner#getEngineType()
    */
   protected String getEngineType()
   {
      return "jython"; //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.AeAbstractExpressionRunner#createExpressionTypeConverter(org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext)
    */
   protected IAeExpressionTypeConverter createExpressionTypeConverter(IAeExpressionRunnerContext aContext)
   {
      return new AeBSFPythonExpressionTypeConverter();
   }
}