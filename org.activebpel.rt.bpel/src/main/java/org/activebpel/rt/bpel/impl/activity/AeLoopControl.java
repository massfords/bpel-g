//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeLoopControl.java,v 1.2 2005/08/18 21:35:46 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.AeStaticAnalysisException;
import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.activity.support.IAeLoopActivity;

/**
 * A base class for the loop control activities "break" and "continue" 
 */
public abstract class AeLoopControl extends AeActivityImpl implements IAeLoopControl
{
   /**
    * Constructor for subclasses to pass their specific activity def and parent.
    * @param aActivityDef
    * @param aParent
    */
   public AeLoopControl(AeActivityDef aActivityDef, IAeActivityParent aParent)
   {
      super(aActivityDef, aParent);
   }
   
   /**
    * Walks up the parent axis looking for the enclosing loop container.
    * @throws AeStaticAnalysisException
    */
   protected IAeLoopActivity getEnclosedLoopContainer() throws AeStaticAnalysisException
   {
      IAeBpelObject parent = getParent();
      while(parent != null && !(parent instanceof IAeLoopActivity))
      {
         parent = parent.getParent();
      }
      
      if (parent == null)
      {
         staticAnalysisFailure(AeMessages.format("AeActivityContinueImpl.NO_LOOP_CONTAINER", getLocationPath())); //$NON-NLS-1$
      }
      return (IAeLoopActivity)parent;
   }
   

}
 