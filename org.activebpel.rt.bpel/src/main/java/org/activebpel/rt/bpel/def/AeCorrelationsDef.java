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

import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;


/**
 * Container class for <code>correlations</code> that are a part of <code>receive</code>,
 * <code>reply</code>, <code>invoke</code>, and <code>pick</code>.
 */
public class AeCorrelationsDef extends AeBaseContainer
{
   /**
    * Default c'tor.
    */
   public AeCorrelationsDef()
   {
      super();
   }

   /**
    * Adds a correlation def to the container.
    * 
    * @param aDef
    */
   public void addCorrelationDef(AeCorrelationDef aDef)
   {
      super.add(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * @return Returns the requestFlag.
    */
   public boolean isRequestPatternUsed()
   {
      for (Iterator iter = getValues(); iter.hasNext();)
      {
         AeCorrelationDef def = (AeCorrelationDef) iter.next();
         if (def.getPattern() != null && def.getPattern().isRequestDataUsed())
         {
            return true;
         }
      }
      return false;
   }

   /**
    * @return Returns the responseFlag.
    */
   public boolean isResponsePatternUsed()
   {
      for (Iterator iter = getValues(); iter.hasNext();)
      {
         AeCorrelationDef def = (AeCorrelationDef) iter.next();
         if (def.getPattern() != null && def.getPattern().isResponseDataUsed())
         {
            return true;
         }
      }
      return false;
   }
}
