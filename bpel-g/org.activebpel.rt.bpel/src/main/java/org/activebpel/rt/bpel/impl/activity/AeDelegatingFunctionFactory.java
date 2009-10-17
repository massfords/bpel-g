//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeDelegatingFunctionFactory.java,v 1.2 2008/02/05 04:24:06 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.function.AeUnresolvableException;
import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.bpel.function.IAeFunctionFactory;

/**
 * This factory delegates the work to an appropriate delegate based on the namespace   
 */
public class AeDelegatingFunctionFactory implements IAeFunctionFactory
{
   // common error messages
   public static final String NO_FUNCTION_FOUND_ERROR = AeMessages.getString("AeAbstractFunctionContext.0"); //$NON-NLS-1$

   /** contains a list of all functional factories that this factory can delegate getFunction() to */
   private List mDelegates = new LinkedList();
   
   /**
    * C'tor that accepts two function factories 
    * @param aDelegateOne
    * @param aDelegateTwo
    */
   public AeDelegatingFunctionFactory(IAeFunctionFactory aDelegateOne, IAeFunctionFactory aDelegateTwo)
   {
      add(aDelegateOne);
      add(aDelegateTwo);
   }

   /**
    * Adds this factory to the list of delegates
    * @param aFactory
    */
   protected void add(IAeFunctionFactory aFactory)
   {
      getDelegates().add(aFactory);
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionFactory#getFunction(java.lang.String, java.lang.String)
    */
   public IAeFunction getFunction(String aNamespace, String aFunctionName) throws AeUnresolvableException
   {
      for(Iterator iter=getDelegates().iterator(); iter.hasNext(); )
      {
         IAeFunctionFactory delegate = (IAeFunctionFactory) iter.next();
         if (delegate.getFunctionContextNamespaceList().contains(aNamespace))
         {
            return delegate.getFunction(aNamespace, aFunctionName);
         }
      }
      // We don't want to throw here since the function may be one of the built
      // in functions that can be handled by the expression runner. (i.e. number())
      return null;
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionFactory#getFunctionContextNamespaceList()
    */
   public Set getFunctionContextNamespaceList()
   {
      Set namespacesList = new LinkedHashSet();
      for(Iterator iter=getDelegates().iterator(); iter.hasNext(); )
      {
         IAeFunctionFactory delegate = (IAeFunctionFactory) iter.next();
         namespacesList.addAll(delegate.getFunctionContextNamespaceList());
      }
      return namespacesList;
   }

   /**
    * @return the iterator over the delegates list
    */
   protected List getDelegates()
   {
      return mDelegates;
   }

   /**
    * @param aDelegates the delegates to set
    */
   protected void setDelegates(List aDelegates)
   {
      mDelegates = aDelegates;
   }

}
