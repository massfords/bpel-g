// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityThrowImpl.java,v 1.15 2007/11/21 03:22:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.AeActivityThrowDef;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.activity.support.AeFault;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Element;

/**
 * Implementation of the bpel throw activity.
 */
public class AeActivityThrowImpl extends AeActivityImpl
{
   /** default constructor for activity */
   public AeActivityThrowImpl(AeActivityThrowDef aActivityDef, IAeActivityParent aParent)
   {
      super(aActivityDef, aParent);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept( IAeImplVisitor aVisitor ) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();
      AeActivityThrowDef def = (AeActivityThrowDef) getDefinition();
      AeFault fault = null;
      if(getFaultVariable() != null)
      {
         if (getFaultVariable().isMessageType())
         {
            fault = new AeFault(def.getFaultName(), getFaultVariable().getMessageData()); 
         }
         else
         {
            fault = new AeFault(def.getFaultName(), getFaultVariable().getElementData());
         }
      }
      else
      {
         fault = new AeFault(def.getFaultName(), (Element)null);
      }
      objectCompletedWithFault(fault);
   }
   
   /**
    * Returns the variable for use in the throw or null if one wasn't part of the
    * definition.
    */
   private IAeVariable getFaultVariable()
   {
      AeActivityThrowDef def = (AeActivityThrowDef) getDefinition();
      String variableName = def.getFaultVariable();
      if ( ! AeUtil.isNullOrEmpty(variableName))
      {
         return findVariable(variableName);
      }
      return null;
   }

}
