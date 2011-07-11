//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityContinueImpl.java,v 1.3 2007/11/21 03:22:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.activity.AeActivityContinueDef;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;

/**
 * Implementation of the continue activity. This activity will signal the enclosing
 * loop construct to immediately terminate all executing activities and then reevaluate
 * its condition for looping.
 */
public class AeActivityContinueImpl extends AeLoopControl
{
   /** default constructor for activity */
   public AeActivityContinueImpl(AeActivityContinueDef aActivityDef, IAeActivityParent aParent)
   {
      super(aActivityDef, aParent);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept(IAeImplVisitor aVisitor)
         throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();
      getEnclosedLoopContainer().onContinue(this);
   }
}
 