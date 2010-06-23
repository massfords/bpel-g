// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeFromQueryVariableContext.java,v 1.1 2007/10/03 12:39:50 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.activity.assign.to;

import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.expr.xpath.AeXPathVariableReference;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext;
import org.activebpel.rt.bpel.impl.expr.AeExpressionException;
import org.activebpel.rt.bpel.impl.expr.xpath.AeXPathExpressionTypeConverter;
import org.activebpel.rt.bpel.xpath.AeXPathHelper;
import org.activebpel.rt.util.AeUtil;
import org.jaxen.UnresolvableException;
import org.jaxen.VariableContext;
import org.w3c.dom.Document;

/**
 * Implements a Jaxen variable context using the copy operation context for
 * variable resolution.
 */
public class AeFromQueryVariableContext implements VariableContext
{
   /** The copy operation context to use to resolve variables. */
   private IAeCopyOperationContext mCopyOperationContext;
   
   /**
    * Creates a variable context for the to-query to-spec impl.
    * 
    * @param aCopyOpContext
    */
   public AeFromQueryVariableContext(IAeCopyOperationContext aCopyOpContext)
   {
      setCopyOperationContext(aCopyOpContext);
   }

   /**
    * @see org.jaxen.VariableContext#getVariableValue(java.lang.String, java.lang.String, java.lang.String)
    */
   public Object getVariableValue(String aNamespaceURI, String aPrefix, String aLocalName)
         throws UnresolvableException
   {
      Object result = null;

      try
      {
         // The variable must be unqualified.
         if (AeUtil.isNullOrEmpty(aNamespaceURI))
         {
            AeXPathVariableReference varRef = new AeXPathVariableReference(aLocalName);
            
            // Find the variable with the given name.
            String variableName = varRef.getVariableName();
            String partName = varRef.getPartName();
            IAeVariable variable = getVariable(variableName, partName);

            // Is the variable a message variable?
            if (variable.isMessageType())
            {
               result = variable.getMessageData().getData(partName);
               // Note: always return an Element rather than a Document so that a relative path
               // can then be applied to it.
               if (result instanceof Document)
                  result = ((Document) result).getDocumentElement();
            }
            else if (variable.isType())
            {
               result = variable.getTypeData();
            }
            else if (variable.isElement())
            {
               result = variable.getElementData();
            }
         }
      }
      catch (AeBpelException ex)
      {
         throw new AeExpressionException(ex);
      }
      
      AeXPathExpressionTypeConverter converter = new AeXPathExpressionTypeConverter(AeXPathHelper.getInstance(getCopyOperationContext().getBPELNamespace()));
      return converter.convertToExpressionType(result);
   }

   /**
    * Gets the variable/part.
    * 
    * @param aVariableName
    * @param aPartName
    */
   protected IAeVariable getVariable(String aVariableName, String aPartName)
   {
      return getCopyOperationContext().getVariable(aVariableName);
   }

   /**
    * @return Returns the copyOperationContext.
    */
   public IAeCopyOperationContext getCopyOperationContext()
   {
      return mCopyOperationContext;
   }

   /**
    * @param aCopyOperationContext The copyOperationContext to set.
    */
   public void setCopyOperationContext(IAeCopyOperationContext aCopyOperationContext)
   {
      mCopyOperationContext = aCopyOperationContext;
   }

}
