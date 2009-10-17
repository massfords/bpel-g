//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/function/AeFunctionContextContainer.java,v 1.6 2007/11/01 18:23:52 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.function;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.impl.function.AeInvalidFunctionContextException;
import org.activebpel.rt.util.AeUtil;

/**
 * Container for custom function contexts.
 */
public class AeFunctionContextContainer
{
   /** Internal storage for mapping namespace to function contexts. */
   private Map mNamespaceToFunctionContextMap;
   /** Locator for creating function contexts. */
   private IAeFunctionContextLocator mLocator;
   /** bpel expression function context */
   private IAeFunctionContext mBpelFunctionContext;
   /** bpel 2.0 expression function context */
   private IAeFunctionContext mBpel20FunctionContext;
   /** bpel extension function context */
   private IAeFunctionContext mBpelExtFunctionContext;

   /**
    * Constructor.
    * @param aLocator
    */
   public AeFunctionContextContainer( IAeFunctionContextLocator aLocator )
   {
      mLocator = aLocator;
      mNamespaceToFunctionContextMap = new HashMap();
   }

   /** 
    * @return Returns ns to function context map.
    */
   protected Map getNamespaceToFunctionContextMap()
   {
      return mNamespaceToFunctionContextMap;
   }
   
   /**
    * Return all of the custom <code>AeFunctionContextInfo</code> objects registered
    * with the container.
    */
   public Collection getFunctionContexts()
   {
      return getNamespaceToFunctionContextMap().values();
   }

   /**
    * Gets a list of all the function context namespaces.
    */
   public Collection getFunctionContextNamespaces()
   {
      List list = new LinkedList(getNamespaceToFunctionContextMap().keySet());
      list.add(IAeBPELConstants.BPWS_NAMESPACE_URI);
      list.add(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI);
      list.add(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI);
      return list;
   }

   /**
    * Remove the context with the given name from the container.
    * 
    * @param aName
    */
   public void remove( String aName )
   {
      for (Iterator iter = getNamespaceToFunctionContextMap().entrySet().iterator(); iter.hasNext(); )
      {
         Map.Entry entry = (Map.Entry) iter.next();
         AeFunctionContextInfo info = (AeFunctionContextInfo) entry.getValue();
         if (aName.equals(info.getName()))
         {
            iter.remove();
         }
      }
   }

   /**
    * Find <code>IAeFunctionContext</code> based on the given namespace.
    * 
    * @param aNamespace
    */
   public IAeFunctionContext getFunctionContext(String aNamespace)
   {
      IAeFunctionContext found = null;

      // TODO (EPW) treat these contexts the same as custom function contexts.
      if (IAeBPELConstants.BPWS_NAMESPACE_URI.equals(aNamespace))
      {
         found = getBpelContext();
      }
      else if (IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI.equals(aNamespace))
      {
         found = getBpel20Context();
      }
      else if (IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI.equals(aNamespace))
      {
         found = getBpelExtContext();
      }
      else
      {
         AeFunctionContextInfo info = (AeFunctionContextInfo) getNamespaceToFunctionContextMap().get(aNamespace);
         if (info != null)
         {
            found = info.getFunctionContext();
         }
      }
      
      return found;
   }
   
   /**
    * Clear out all registered custom function contexts. 
    */
   public void clearCustomFunctions()
   {
      getNamespaceToFunctionContextMap().clear();
   }

   /**
    * Add the function context to the container.
    * 
    * @param aName The user specified name for the grouping
    * @param aNamespace Namespace to match on.
    * @param aClassName IAeExpressionFunctionContext impl class name.
    * @throws AeException Throw if there is a problem finding/creating the <code>IAeFunctionContext</code> impl.
    */
   public void addFunctionContext(String aName, String aNamespace, String aClassName) throws AeException
   {
      if (AeUtil.isNullOrEmpty(aClassName))
      {
         throw new AeException(AeMessages.getString("AeFunctionContextContainer.ERROR_0") + aNamespace); //$NON-NLS-1$
      }
      if (AeUtil.isNullOrEmpty(aNamespace))
      {
         throw new AeException(AeMessages.getString("AeFunctionContextContainer.FUNCTION_CONTEXT_WITHOUT_NAMESPACE_ERROR")); //$NON-NLS-1$
      }

      IAeFunctionContext functionContext = loadFunctionContext(aNamespace, null, aClassName);
      addFunctionContext(aName, aNamespace, functionContext);
   }
   
   /**
    * Add the function context to the container.
    * 
    * @param aName The user specified name for the grouping
    * @param aNamespace Namespace to match on.
    * @param aContext
    */
   public void addFunctionContext( String aName, String aNamespace, IAeFunctionContext aContext )
   {
      AeFunctionContextInfo context = new AeFunctionContextInfo(aName, aNamespace, aContext);
      store(context);
   }
   
   /**
    * Load the <code>IAeFunctionContext</code>.
    * 
    * @param aNamespace
    * @param aLocation
    * @param aClassName
    * @throws AeInvalidFunctionContextException
    */
   public IAeFunctionContext loadFunctionContext(String aNamespace, String aLocation, String aClassName) throws AeInvalidFunctionContextException
   {
      return getLocator().locate(aNamespace, aLocation, aClassName);
   }

   /**
    * Store the <code>AeFunctionContextInfo</code> in the container.
    * 
    * @param aContextInfo
    */
   protected void store(AeFunctionContextInfo aContextInfo)
   {
      getNamespaceToFunctionContextMap().put(aContextInfo.getNamespace(), aContextInfo);
   }
   
   /**
    * Accessor for <code>IAeFunctionContextLocator</code>.
    */
   protected IAeFunctionContextLocator getLocator()
   {
      return mLocator;
   }

   /**
    * @return Returns the bpelContext.
    */
   protected IAeFunctionContext getBpelContext()
   {
      return mBpelFunctionContext;
   }

   /**
    * @return Returns the bpelContext.
    */
   protected IAeFunctionContext getBpel20Context()
   {
      return mBpel20FunctionContext;
   }

   /**
    * Setter for bpel function context.
    */
   public void setBpelContext(IAeFunctionContext aContext)
   {
      mBpelFunctionContext = aContext;
   }

   /**
    * Setter for bpel function context.
    */
   public void setBpel20Context(IAeFunctionContext aContext)
   {
      mBpel20FunctionContext = aContext;
   }

   /**
    * @return Returns the bpelExtContext.
    */
   protected IAeFunctionContext getBpelExtContext()
   {
      return  mBpelExtFunctionContext;
   }

   /**
    * Setter for bpel extension function context info.
    */
   public void setBpelExtContext(IAeFunctionContext aContext)
   {
      mBpelExtFunctionContext = aContext;
   }
}
