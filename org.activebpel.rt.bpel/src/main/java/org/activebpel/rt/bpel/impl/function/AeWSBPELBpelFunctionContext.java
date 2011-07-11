// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/AeWSBPELBpelFunctionContext.java,v 1.2 2006/08/17 19:59:26 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.function;

import org.activebpel.rt.bpel.function.AeUnresolvableException;
import org.activebpel.rt.bpel.function.IAeFunction;

/**
 * A BPEL 2.0 version of the standard bpel function context.
 */
public class AeWSBPELBpelFunctionContext extends AeAbstractBpelFunctionContext
{
   /** Constant for doXslTransform bpel function. */
   public static final String DO_XSL_TRANSFORM = "doXslTransform"; //$NON-NLS-1$

   /**
    * Overrides method to disallow the getVariableData() function.
    * 
    * @see org.activebpel.rt.bpel.impl.function.AeAbstractBpelFunctionContext#getFunction(java.lang.String)
    */
   public IAeFunction getFunction(String aLocalName) throws AeUnresolvableException
   {
      if (DO_XSL_TRANSFORM.equals(aLocalName))
      {
         return new AeDoXslTransformFunction();
      }
      else
      {
         return super.getFunction(aLocalName);
      }
   }
}
