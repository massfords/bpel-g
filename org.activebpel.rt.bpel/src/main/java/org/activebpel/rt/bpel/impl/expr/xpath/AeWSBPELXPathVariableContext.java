// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/expr/xpath/AeWSBPELXPathVariableContext.java,v 1.3 2007/11/01 18:23:52 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.expr.xpath;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.expr.AeExpressionException;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerVariableResolver;
import org.activebpel.rt.util.AeUtil;
import org.jaxen.UnresolvableException;
import org.jaxen.VariableContext;
import org.w3c.dom.Document;

/**
 * An implementation of a Jaxen variable context.  This class is used in the BPEL 2.0 version of
 * the XPath expression runner in order to resolve variable references using the syntax outlined
 * in that version of BPEL.
 */
public class AeWSBPELXPathVariableContext implements VariableContext
{
   /** The function exec context. */
   private IAeFunctionExecutionContext mFunctionExecutionContext;
   /** Varariable resolver. */
   private IAeExpressionRunnerVariableResolver mVariableResolver;

   /**
    * Constructor.
    *
    * @param aContext
    */
   public AeWSBPELXPathVariableContext(IAeFunctionExecutionContext aContext, IAeExpressionRunnerVariableResolver aVariableResolver)
   {
      setFunctionExecutionContext(aContext);
      setVariableResolver(aVariableResolver);
   }

   /**
    * @see org.jaxen.VariableContext#getVariableValue(java.lang.String, java.lang.String, java.lang.String)
    */
   public Object getVariableValue(String aNamespaceURI, String aPrefix, String aLocalName) throws UnresolvableException
   {
      Object result = null;

      // The variable must be unqualified.
      if (AeUtil.isNullOrEmpty(aNamespaceURI))
      {
         try
         {
            result = getVariableResolver().getVariableData(aLocalName);
            // Must return an Element for Jaxen to continue stepping
            if (result instanceof Document)
            {
               result = ((Document) result).getDocumentElement();
            }            
         }
         catch (Exception ex)
         {
            throw new UnresolvableException(ex.getMessage());
         }
         
         if (result == null)
         {
           throw new AeExpressionException( new AeBpelException(
                  AeMessages.format("AeGetVariableDataFunction.VARIABLE_NOT_INITIALIZED_ERROR", aLocalName), getFunctionExecutionContext().getFaultFactory().getUninitializedVariable() ) ); //$NON-NLS-1$            
         }
      }
      else
      {
         // This is the case of the qualified variable - which could not possibly be resolved.
         //
         // Note that this exception should never happen, since we would presumably have caught
         // this during validation (qualified variable reference).
         throw new UnresolvableException(AeMessages.format("AeWSBPEL20XPathVariableContext.UNRESOLVED_VARIABLE_ERROR", aLocalName)); //$NON-NLS-1$
      }

      // Convert the variable value to something that Jaxen can properly use.
      return getFunctionExecutionContext().getTypeConverter().convertToExpressionType(result);
   }

   /**
    * @return Returns the functionExecutionContext.
    */
   protected IAeFunctionExecutionContext getFunctionExecutionContext()
   {
      return mFunctionExecutionContext;
   }

   /**
    * @param mContext The functionExecutionContext to set.
    */
   protected void setFunctionExecutionContext(IAeFunctionExecutionContext mContext)
   {
      mFunctionExecutionContext = mContext;
   }

   /**
    * @return variable resolver.
    */
    protected IAeExpressionRunnerVariableResolver getVariableResolver()
    {
       return mVariableResolver;
    }

    /**
     * Sets the variable resolver.
     * @param aVariableResolver
     */
    protected void setVariableResolver(IAeExpressionRunnerVariableResolver aVariableResolver)
    {
       mVariableResolver = aVariableResolver;
    }
}
