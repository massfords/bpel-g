//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/visitors/AeRegisterLocationVisitor.java,v 1.5 2007/11/27 02:49:47 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.visitors;

import java.util.Iterator;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.AeBusinessProcess;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.AeVariable;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;

/**
 * Visit the objects creating unique ids from process ids and their paths.  This
 * is neccesary during restore state when the state has not included a path in the 
 * document and the path itself has custom locations.
 */
public class AeRegisterLocationVisitor extends AeImplTraversingVisitor
{
   private AeBusinessProcess mProcess;

   /**
    * Construct the register location visitr with the associated business process.
    */
   public AeRegisterLocationVisitor(AeBusinessProcess aProcess)
   {
      super();
      mProcess = aProcess;
   }
   
   /**
    * Extends the method to assign a unique id and register the path with the process.
    *  
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visitBase(org.activebpel.rt.bpel.impl.AeAbstractBpelObject)
    */
   protected void visitBase(AeAbstractBpelObject aImpl) throws AeBusinessProcessException
   {
      // assign a unique id
      int locationId = getProcess().getMaxLocationId() + 1;
      aImpl.setLocationId(locationId);
      getProcess().setMaxLocationId(locationId);
      
      // add the bpel object to the process
      getProcess().addBpelObject(aImpl.getLocationPath(), aImpl);
      
      // continue traverse
      super.visitBase(aImpl);
   }
   
   /**
    * Extends the method to assign a unique id to variables and partner links and register the variable 
    * paths with the process. 
    * @see org.activebpel.rt.bpel.impl.visitors.AeAbstractImplVisitor#visitScope(org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl)
    */
   protected void visitScope(AeActivityScopeImpl aImpl) throws AeBusinessProcessException
   {
      if (aImpl.getVariableContainer() != null)
      {
         for(Iterator iter = aImpl.getVariableContainer().iterator(); iter.hasNext(); )
         {
            AeVariable variable = (AeVariable)iter.next();
            getProcess().addVariableMapping(variable);
         }
      }
      for (Iterator iter = aImpl.getPartnerLinks().values().iterator(); iter.hasNext(); )
      {
         AePartnerLink plink = (AePartnerLink) iter.next();
         getProcess().addPartnerLinkMapping(plink);
      }
      super.visitScope(aImpl);
   }
   
   /**
    * @return Returns the process.
    */
   public AeBusinessProcess getProcess()
   {
      return mProcess;
   }
}
