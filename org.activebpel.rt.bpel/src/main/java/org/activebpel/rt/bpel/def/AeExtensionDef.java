// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def;

import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Models the 'extension' bpel construct introduced in WS-BPEL 2.0.
 */
public class AeExtensionDef extends AeBaseDef
{
   /** The extension's 'namespace' attribute. */
   private String mNamespace;
   /** The extension's 'mustUnderstand' attribute. */
   private boolean mMustUnderstand;
   
   /**
    * Default c'tor.
    */
   public AeExtensionDef()
   {
      super();
   }

   /**
    * @return Returns the mustUnderstand.
    */
   public boolean isMustUnderstand()
   {
      return mMustUnderstand;
   }

   /**
    * @param aMustUnderstand The mustUnderstand to set.
    */
   public void setMustUnderstand(boolean aMustUnderstand)
   {
      mMustUnderstand = aMustUnderstand;
   }

   /**
    * @return Returns the namespace.
    */
   public String getNamespace()
   {
      return mNamespace;
   }

   /**
    * @param aNamespace The namespace to set.
    */
   public void setNamespace(String aNamespace)
   {
      mNamespace = aNamespace;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
