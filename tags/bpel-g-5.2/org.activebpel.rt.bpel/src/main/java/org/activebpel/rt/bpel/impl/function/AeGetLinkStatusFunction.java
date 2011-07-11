//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/AeGetLinkStatusFunction.java,v 1.4 2007/09/04 15:51:33 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function;

import java.util.List;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeFaultFactory;
import org.activebpel.rt.bpel.impl.activity.support.AeLink;
import org.activebpel.rt.bpel.impl.expr.AeExpressionException;

/**
 * Class representing the XPath function used by expression evaluator to handle 
 * the BPEL getLinkStatus function call.
 */
public class AeGetLinkStatusFunction extends AeAbstractBpelFunction
{
   public static final String FUNCTION_NAME = "getLinkStatus"; //$NON-NLS-1$
   // error message constants
   private static final String INVALID_ARGS        = AeMessages.getString( "AeAbstractBpelObject.ERROR_37" ); //$NON-NLS-1$
   private static final String LINK_FAULT_MSG      = AeMessages.getString( "AeAbstractBpelObject.ERROR_38" ); //$NON-NLS-1$
   
   /**
    * Constructor.
    */
   public AeGetLinkStatusFunction()
   {
      super(FUNCTION_NAME);
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunction#call(org.activebpel.rt.bpel.function.IAeFunctionExecutionContext, java.util.List)
    */
   public Object call(IAeFunctionExecutionContext aContext, List aArgs) throws AeFunctionCallException
   {
      if (aArgs.size() != 1)
      {
         throw new AeFunctionCallException( INVALID_ARGS );
      }

      AeLink link = aContext.getAbstractBpelObject().findTargetLink(getStringArg(aArgs,0));
      if (link == null)
      {
         AeBpelException e = new AeBpelException(LINK_FAULT_MSG, AeFaultFactory.getBadProcess());
         throw new AeExpressionException(e);
      }
      
      return Boolean.valueOf(link.getStatus());
   }
}
