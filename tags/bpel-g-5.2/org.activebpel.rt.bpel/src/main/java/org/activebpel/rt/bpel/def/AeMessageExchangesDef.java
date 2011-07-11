//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/AeMessageExchangesDef.java,v 1.3 2006/11/03 22:48:01 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.util.AeUtil;

/**
 * Simple container for string values used for binding receives and replies
 */
public class AeMessageExchangesDef extends AeBaseContainer<AeMessageExchangeDef, AeMessageExchangeDef>
{
   private static final long serialVersionUID = -1116836666830553841L;

   /** true if the messageExchanges implicitly declares the "default" messageExchange value */
   private boolean mDefaultDeclared = false;

   /** cached set of values for the messageExchanges contained within this def */
   private Set<String> mValues = null;

   /** Indicates if this def is an implicit construct. */
   private boolean mImplict;
   
   /**
    * Default c'tor.
    */
   public AeMessageExchangesDef()
   {
      super();
   }

   /**
    * @return the implict
    */
   public boolean isImplict()
   {
      return mImplict;
   }

   /**
    * @param aImplict the implict to set
    */
   public void setImplict(boolean aImplict)
   {
      mImplict = aImplict;
   }


   /**
    * Returns true if the message exchange value is contained within the set of declared message exchanges
    * or if the value is empty and the def implicitly declares a default.
    * @param aValue
    */
   public boolean declaresMessageExchange(String aValue)
   {
      return getMessageExchangeValues().contains(aValue) || (isDefaultDeclared() && AeUtil.isNullOrEmpty(aValue));
   }

   /**
    * Gets an Iterator over the message exchange defs.
    */
   public Iterator<? extends Object> getMessageExchangeDefs()
   {
      return getValues();
   }

   /**
    * @return Returns the messageExchanges.
    */
   public Set getMessageExchangeValues()
   {
      if (mValues == null)
      {
         Set<String> set = new HashSet<String>();
         for (Iterator<? extends AeMessageExchangeDef> iter = getValues(); iter.hasNext(); )
         {
            AeMessageExchangeDef msgExchangeDef = iter.next();
            set.add(msgExchangeDef.getName());
         }
         mValues = Collections.unmodifiableSet(set);
      }
      return mValues;
   }

   /**
    * Increase visibility and clear message exchange def name cache.
    * @see org.activebpel.rt.bpel.def.AeBaseContainer#add(java.lang.Object)
    */
   public void add(AeMessageExchangeDef aValue)
   {
      super.add(aValue);
      clearValues();
   }

   /**
    * Clears the cached set of values
    */
   protected void clearValues()
   {
      mValues = null;
   }

   /**
    * @return Returns the defaultDeclared.
    */
   public boolean isDefaultDeclared()
   {
      return mDefaultDeclared;
   }

   /**
    * @param aDefaultDeclared The defaultDeclared to set.
    */
   public void setDefaultDeclared(boolean aDefaultDeclared)
   {
      mDefaultDeclared = aDefaultDeclared;
   }


   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
