// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import java.util.Iterator;

import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;


/**
 * Models the <code>variables</code> element from BPEL.  
 */
public class AeVariablesDef extends AeBaseContainer
{
   /**
    * Default c'tor.
    */
   public AeVariablesDef()
   {
      super();
   }

   /**
    * Add a variable
    * @param aVariableDef
    */
   public void addVariableDef(AeVariableDef aVariableDef)
   {
      add(aVariableDef.getName(), aVariableDef);
   }
   
   /**
    * Gets a variable by name.
    * @param aName
    */
   public AeVariableDef getVariableDef(String aName)
   {
      return (AeVariableDef) get(aName);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.AeBaseContainer#isEmpty()
    */
   public boolean isEmpty()
   {
      boolean empty = super.isEmpty();
      if (!empty)
      {
         boolean foundExplicit = false;
         // we might still be empty if the container only has dynamic variables
         for (Iterator it = getValues(); it.hasNext(); )
         {
            AeVariableDef def = (AeVariableDef) it.next();
            if (!def.isImplicit())
            {
               foundExplicit = true;
               break;
            }
         }
         
         // TODO (MF) should use a different means of testing for explicit only

         // if the variable container only had implicit variables then we consider
         // it empty in terms of writing xml since these defs will get recreated
         // during the read.
         empty = !foundExplicit;
      }
      return empty;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);      
   }
}
