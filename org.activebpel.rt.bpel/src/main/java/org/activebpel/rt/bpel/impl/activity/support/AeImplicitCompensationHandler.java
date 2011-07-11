// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeImplicitCompensationHandler.java,v 1.8 2007/12/31 20:31:36 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
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
 * Implements the logic for the implicit compensation handler. This handler
 * executes a single instance of the default compensate activity. 
 */
public class AeImplicitCompensationHandler extends AeCompensationHandler
{
   /**
    * Ctor that takes the parent scope and adds the default compensation
    * activity as its only child activity. 
    * @param aParent
    */
   public AeImplicitCompensationHandler(AeActivityScopeImpl aParent)
   {
      super(null, aParent);
      addActivity(AeActivityCompensateImpl.createImplicitCompensation(this, aParent.getDefinition()));
   }

   /**
    * Implicit compensation handler always executes all of the (enclosed scope's)
    * comp info objects, including the coordinated comp info objects. In this case,
    * an implicit compensate activity for the coordinated activities does not need to
    * be run. This method returns false.
    *   
    * @return false for implicit compensation handler.
    */
   protected boolean isEnableCoordinatedActivityCompensation()
   {
      // Implicit compensation handler always executes all of the (enclosed scope's)
      // comp info objects, including the coordinated comp info objects.
      // So, this class does not have to re-run the comp. for the coordinated activities.
      return false;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept(IAeImplVisitor aVisitor) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * This is overridden because we don't have a def object. Our location path
    * is equal to the parent's path plus _ImplicitCompensationHandler
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getLocationPath()
    */
   public String getLocationPath()
   {
      IAeBpelObject parent = getParent();
      StringBuffer buffer = new StringBuffer(parent.getLocationPath());
      return buffer.append(AeSupportActivityLocationPathSuffix.IMPLICIT_COMPENSATION_HANDLER).toString(); 
   }

   /**
    * This object gets created because there was no compensation handler defined for the scope.
    * As such, there is no definition object and calling this method results in an exception.
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#getDefinition()
    */
   public AeBaseDef getDefinition()
   {
      throw new UnsupportedOperationException(AeMessages.getString("AeImplicitCompensationHandler.ERROR_1")); //$NON-NLS-1$
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
