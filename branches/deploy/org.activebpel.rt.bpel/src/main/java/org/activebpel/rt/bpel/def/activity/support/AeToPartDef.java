// $Header$
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.activity.support;

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Models the invoke|reply activity's 'toPart' bpel construct introduced in WS-BPEL 2.0.
 */
public class AeToPartDef extends AeBaseDef
{
   /** The toPart's 'part' attribute. */
   private String mPart;
   /** The toPart's 'fromVariable' attribute. */
   private String mFromVariable;

   /**
    * Default c'tor.
    */
   public AeToPartDef()
   {
      super();
   }

   /**
    * @return Returns the fromVariable.
    */
   public String getFromVariable()
   {
      return mFromVariable;
   }

   /**
    * @param aFromVariable The fromVariable to set.
    */
   public void setFromVariable(String aFromVariable)
   {
      mFromVariable = aFromVariable;
   }

   /**
    * @return Returns the part.
    */
   public String getPart()
   {
      return mPart;
   }

   /**
    * @param aPart The part to set.
    */
   public void setPart(String aPart)
   {
      mPart = aPart;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
