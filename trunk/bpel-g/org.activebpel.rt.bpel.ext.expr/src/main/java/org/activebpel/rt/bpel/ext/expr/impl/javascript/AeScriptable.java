//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/impl/javascript/AeScriptable.java,v 1.1 2005/06/08 13:11:37 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.impl.javascript;

import org.mozilla.javascript.Scriptable;

/**
 * This is a base class that implements the Scriptable interface.  All methods are defined, so 
 * subclasses should impl only those methods that are needed.
 */
public abstract class AeScriptable implements Scriptable
{
   /** The parent scope - set during construction. */
   private Scriptable mParentScope;
   
   /**
    * Construct a scriptable with the given parent scope.
    * 
    * @param aParentScope
    */
   public AeScriptable(Scriptable aParentScope)
   {
      setParentScope(aParentScope);
   }

   /**
    * @see org.mozilla.javascript.Scriptable#getClassName()
    */
   public String getClassName()
   {
      return AeScriptable.class.getName();
   }

   /**
    * @see org.mozilla.javascript.Scriptable#get(java.lang.String, org.mozilla.javascript.Scriptable)
    */
   public Object get(String name, Scriptable start)
   {
      return null;
   }

   /**
    * @see org.mozilla.javascript.Scriptable#get(int, org.mozilla.javascript.Scriptable)
    */
   public Object get(int index, Scriptable start)
   {
      return NOT_FOUND;
   }

   /**
    * @see org.mozilla.javascript.Scriptable#has(java.lang.String, org.mozilla.javascript.Scriptable)
    */
   public boolean has(String name, Scriptable start)
   {
      return false;
   }

   /**
    * @see org.mozilla.javascript.Scriptable#has(int, org.mozilla.javascript.Scriptable)
    */
   public boolean has(int index, Scriptable start)
   {
      return false;
   }

   /**
    * @see org.mozilla.javascript.Scriptable#put(java.lang.String, org.mozilla.javascript.Scriptable, java.lang.Object)
    */
   public void put(String name, Scriptable start, Object value)
   {
      // Do nothing - this is ready only!
   }

   /**
    * @see org.mozilla.javascript.Scriptable#put(int, org.mozilla.javascript.Scriptable, java.lang.Object)
    */
   public void put(int index, Scriptable start, Object value)
   {
      // Do nothing - this is ready only!
   }

   /**
    * @see org.mozilla.javascript.Scriptable#delete(java.lang.String)
    */
   public void delete(String name)
   {
      // Do nothing - this is ready only!
   }

   /**
    * @see org.mozilla.javascript.Scriptable#delete(int)
    */
   public void delete(int index)
   {
      // Do nothing - this is ready only!
   }

   /**
    * @see org.mozilla.javascript.Scriptable#getPrototype()
    */
   public Scriptable getPrototype()
   {
      return null;
   }

   /**
    * @see org.mozilla.javascript.Scriptable#setPrototype(org.mozilla.javascript.Scriptable)
    */
   public void setPrototype(Scriptable prototype)
   {
   }

   /**
    * @see org.mozilla.javascript.Scriptable#getParentScope()
    */
   public Scriptable getParentScope()
   {
      return mParentScope;
   }

   /**
    * @see org.mozilla.javascript.Scriptable#setParentScope(org.mozilla.javascript.Scriptable)
    */
   public void setParentScope(Scriptable aParent)
   {
      mParentScope = aParent;
   }

   /**
    * @see org.mozilla.javascript.Scriptable#getIds()
    */
   public Object[] getIds()
   {
      return new Object[0];
   }

   /**
    * @see org.mozilla.javascript.Scriptable#getDefaultValue(java.lang.Class)
    */
   public Object getDefaultValue(Class hint)
   {
      return null;
   }

   /**
    * @see org.mozilla.javascript.Scriptable#hasInstance(org.mozilla.javascript.Scriptable)
    */
   public boolean hasInstance(Scriptable instance)
   {
      return false;
   }

}
