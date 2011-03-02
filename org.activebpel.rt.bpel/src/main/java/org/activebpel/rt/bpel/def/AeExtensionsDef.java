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

import java.util.Iterator;

import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.util.AeUtil;

/**
 * Models the 'extensions' bpel construct introduced in WS-BPEL 2.0.
 */
public class AeExtensionsDef extends AeBaseContainer
{
   /**
    * Default c'tor.
    */
   public AeExtensionsDef()
   {
      super();
   }
   
   /**
    * Adds an 'extension' child element to the container's list.
    * 
    * @param aDef
    */
   public void addExtensionDef(AeExtensionDef aDef)
   {
      add(aDef);
   }

   /**
    * Gets an Iterator over the list of all the extension defs.
    */
   public Iterator getExtensionDefs()
   {
      return getValues();
   }
   
   /**
    * Returns true if there is an extension def in the list with the given namespace.
    * 
    * @param aNamespace
    */
   public boolean hasExtensionDef(String aNamespace)
   {
      for (Iterator iter = getValues(); iter.hasNext(); )
      {
         AeExtensionDef extDef = (AeExtensionDef) iter.next();
         if (AeUtil.compareObjects(aNamespace, extDef.getNamespace()))
         {
            return true;
         }
      }
      return false;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
