//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/attachment/AeCopyAllAttachmentsFunction.java,v 1.4 2008/02/05 01:34:09 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function.attachment;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;

/**
 * Class representing the function used by expression evaluators to handle the BPEL -
 * copyAllAttachments(fromVarnames, toVarname) function call.
 * <p>
 * <em>Description:</em> Copies all attachments from the variables identified by the fromVarnames to the
 * variable identified by the toVarname.
 * </p>
 * <em>Parameters:</em>
 * <ul>
 * <li>fromVarnames string containing the list of names of the variables to copy attachments from</li>
 * <li>toVarname string containing the name of the variable to copy attachments to</li>
 * </ul>
 * <p>
 * <em>Return:</em> Integer, containing the number of attachments copied.
 * </p>
 */
public class AeCopyAllAttachmentsFunction extends AeAbstractAttachmentFunction
{
   /** The name of the function implemented */
   public static final String FUNCTION_NAME = "copyAllAttachments"; //$NON-NLS-1$

   /**
    * Constructor.
    */
   public AeCopyAllAttachmentsFunction()
   {
      super(FUNCTION_NAME);
   }

   /**
    * Execution of XPath function.
    * @see org.jaxen.Function#call(org.jaxen.Context, java.util.List)
    */
   public Object call(IAeFunctionExecutionContext aContext, List aArgs) throws AeFunctionCallException
   {
      // Validate that we have the proper number of arguments
      int numArgs = aArgs.size();
      if (numArgs != 2)
         throwFunctionException(INVALID_PARAMS, getFunctionName());

      // Get the from and to variable object from the first and second function argument
      String fromVariableNames = getStringArg(aArgs,0);
      String toVariableName = getStringArg(aArgs,1);

      // Resolve the toVariable first.
      IAeVariable toVariable = getVariable(aContext.getAbstractBpelObject(), toVariableName);

      // Now get the list of from variables
      Collection fromVariables = resolveVariables(aContext, fromVariableNames, toVariableName);

      int preCopyCount = toVariable.getAttachmentData().size();
      int count = 0;
      for (Iterator varIter = fromVariables.iterator(); varIter.hasNext(); )
      {
         IAeVariable fromVariable = (IAeVariable) varIter.next();
         toVariable.getAttachmentData().addAll(fromVariable.getAttachmentData());
         count += toVariable.getAttachmentData().size();
      }
      return new Integer(count - preCopyCount);
   }
}
