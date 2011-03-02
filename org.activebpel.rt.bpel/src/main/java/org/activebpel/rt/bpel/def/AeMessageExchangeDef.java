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
 * Models a WS-BPEL 2.0 messageExchange construct (as well as the BPEL4WS 1.1 Active Endpoints
 * extension construct aex:messageExchange).
 */
public class AeMessageExchangeDef extends AeBaseDef
{
   /** The message exchange's 'name' attribute. */
   private String mName;

   /**
    * Default c'tor.
    */
   public AeMessageExchangeDef()
   {
      super();
   }

   /**
    * @return Returns the name.
    */
   public String getName()
   {
      return mName;
   }

   /**
    * @param aName The name to set.
    */
   public void setName(String aName)
   {
      mName = aName;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
