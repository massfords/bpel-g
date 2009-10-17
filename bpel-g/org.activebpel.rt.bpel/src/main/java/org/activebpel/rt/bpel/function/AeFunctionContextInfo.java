//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/function/AeFunctionContextInfo.java,v 1.1 2005/06/08 12:50:28 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.function;

import org.activebpel.rt.util.AeUtil;

/**
 * Wraps the <code>IAeFunctionContext</code> details.  This is an internal 
 * storage construct.
 */
public class AeFunctionContextInfo
{
   /** function context grouping name */
   protected String mName;
   /** namespace to match on */
   protected String mNamespace;
   /** function context impl */
   protected IAeFunctionContext mFunctionContext;
   
   /**
    * Constructor.
    * @param aName User specified name for this grouping.
    * @param aNamespace Namespace to match on. Can be null if a prefix is provided.
    * @param aContext
    */
   public AeFunctionContextInfo( String aName, String aNamespace, IAeFunctionContext aContext )
   {
      mName = aName;
      mNamespace = aNamespace;
      mFunctionContext = aContext;
   }

   /**
    * @return Returns the functionContext.
    */
   public IAeFunctionContext getFunctionContext()
   {
      return mFunctionContext;
   }
   
   /**
    * @return Returns the name.
    */
   public String getName()
   {
      return mName;
   }
   
   /**
    * @return Returns the namespace.
    */
   public String getNamespace()
   {
      return mNamespace;
   }

   /**
    * This impl only compares the name property as there should never be
    * more than one <code>AeFunctionContextInfo</code> registered with the same
    * name.
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aObj)
   {
      if( aObj != null && aObj instanceof AeFunctionContextInfo )
      {
         return AeUtil.compareObjects( ((AeFunctionContextInfo)aObj).getName(), getName() );
      }
      return false;
   }

   /**
    * @return Returns the functionContext classname.
    */
   public String getFunctionContextClassname()
   {
      String className = getFunctionContext().getClass().getName();
      if( getFunctionContext() instanceof AeFunctionContextWrapper )
      {
         className = ((AeFunctionContextWrapper) getFunctionContext()).getDelegate().getClass().getName();
      }
      return className;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return getName().hashCode();
   }
}
