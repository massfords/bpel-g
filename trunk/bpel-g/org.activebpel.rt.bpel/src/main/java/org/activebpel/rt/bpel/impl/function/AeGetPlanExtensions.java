//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/AeGetPlanExtensions.java,v 1.1 2007/11/21 03:22:18 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function; 

import java.util.Collections;
import java.util.List;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.def.AeBaseDefNamespaceContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Custom function that gets the plan extensions
 * 
 *  getPlanExtensions() -- gets the root element and all of the extensions
 *  
 *  getPlanExtensions(xpath) --- evaluates the xpath using the root extension 
 *                               element as the context 
 */
public class AeGetPlanExtensions extends AeAbstractBpelFunction
{
   /** The name of the function implemented */
   public static final String FUNCTION_NAME = "getPlanExtensions"; //$NON-NLS-1$

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunction#call(org.activebpel.rt.bpel.function.IAeFunctionExecutionContext, java.util.List)
    */
   public Object call(IAeFunctionExecutionContext aContext, List aArgs)
         throws AeFunctionCallException
   {
      IAeProcessPlan plan = aContext.getAbstractBpelObject().getProcess().getProcessPlan();
      
      Element extensions = plan.getExtensions();
      
      if (extensions == null)
      {
         if (aArgs.size() == 0)
         {
            Document doc = AeXmlUtil.newDocument();
            Element e = doc.createElement("extensions"); //$NON-NLS-1$
            doc.appendChild(e);
            return e;
         }
         else
         {
            return Collections.EMPTY_LIST;
         }
      }
      
      if (aArgs.size() == 1)
      {
         try
         {
            return AeXPathUtil.selectNodes(extensions, (String) aArgs.get(0), new AeBaseDefNamespaceContext(aContext.getAbstractBpelObject().getDefinition()));
         }
         catch (AeException e)
         {
            throw new AeFunctionCallException(e);
         }
      }
      else
      {
         return extensions;
      }
   }

}
 