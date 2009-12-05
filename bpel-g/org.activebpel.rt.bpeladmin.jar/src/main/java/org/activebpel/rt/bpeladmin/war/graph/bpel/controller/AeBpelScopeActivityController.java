//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/controller/AeBpelScopeActivityController.java,v 1.2 2005/06/28 17:19:03 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelActivityObject;
import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelObjectBase;

/**
 * The AeBpelScopeActivityController reorders the child models so that the
 * Activity type objects appear at the top of the model's child list. This
 * renders BPEL Activity types first, followed by the remainder.
 */
public class AeBpelScopeActivityController extends AeBpelBandedContainerController
{

   /**
    * Default constructor.
    */
   public AeBpelScopeActivityController()
   {
      super();
   }
  
   /** 
    * Overrides method to set the model and reorder the model child so that the activity type
    * child appear at the top of the list. 
    * @see org.activebpel.rt.bpeladmin.war.graph.ui.controller.IAeGraphController#setModel(java.lang.Object)
    */
   public void setModel(Object aModel)
   {      
      super.setModel(aModel);
      // re-order the bpel model child such that Activity type objects appear first,
      // followed by others such as fault handlers.
      List children = getModelChildren();
      List tempList = new ArrayList();
      Iterator it = children.listIterator();
      // remove non-activity models
      while (it.hasNext())
      {
         AeBpelObjectBase child = (AeBpelObjectBase) it.next();
         if (!(child instanceof AeBpelActivityObject))
         {
            it.remove();
            tempList.add(child);           
         }
      }
      // add non-activity type models to the end of the list.
      it = tempList.listIterator();
      while (it.hasNext())
      {
         getModelChildren().add(it.next());
      }
   }   
}
