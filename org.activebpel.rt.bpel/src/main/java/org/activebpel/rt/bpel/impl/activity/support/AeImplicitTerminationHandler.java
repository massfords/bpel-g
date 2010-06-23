//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeImplicitTerminationHandler.java,v 1.3 2007/12/31 20:31:36 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.activity.AeActivityCompensateImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;

/**
 * Implicit version of termination handler that contains the default compensate activity
 */
public class AeImplicitTerminationHandler extends AeTerminationHandler
{
   /**
    * ctor accepts the def
    * @param aParent
    */
   public AeImplicitTerminationHandler(AeActivityScopeImpl aParent)
   {
      super(null, aParent);
      addActivity(AeActivityCompensateImpl.createImplicitCompensation(this, aParent.getDefinition()));
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeTerminationHandler#isEnableCoordinatedActivityCompensation()
    */
   protected boolean isEnableCoordinatedActivityCompensation()
   {
      return false;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeImplicitCompensationHandler#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept(IAeImplVisitor aVisitor) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeImplicitCompensationHandler#getLocationPath()
    */
   public String getLocationPath()
   {
      IAeBpelObject parent = getParent();
      StringBuffer buffer = new StringBuffer(parent.getLocationPath());
      return buffer.append(AeSupportActivityLocationPathSuffix.IMPLICIT_TERMINATION_HANDLER).toString(); 
   }

   /**
    * This object gets created because there was no compensation handler defined for the scope.
    * As such, there is no definition object and calling this method results in an exception.
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#getDefinition()
    */
   public AeBaseDef getDefinition()
   {
      throw new UnsupportedOperationException(AeMessages.getString("AeImplicitTerminationHandler.ERROR_1")); //$NON-NLS-1$
   }

   /**
    * Overrides method to return <code>false</code>, since {@link
    * #getDefinition()} will fail. 
    * 
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#hasLocationId()
    */
   public boolean hasLocationId()
   {
      return false;
   }
}
 