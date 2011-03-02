//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/controller/AeBpelProcessRootController.java,v 1.3 2008/02/17 21:43:06 mford Exp $
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

import org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelProcessFigure;
import org.activebpel.rt.bpeladmin.war.graph.ui.AeXyLayoutManager;
import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelLinkObject;
import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelObjectBase;
import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelProcessObject;

/**
 * Controller which is the root controller for the BPEL process.
 * This controller also obtains a list of all Link models and adds them
 * to its list of model children.  The root controller is also used to
 * boot strap the creation of MVC hierarchy in the graph container.
 */
public class AeBpelProcessRootController extends AeBpelControllerBase
{
   /** Use the Flow or Sequence BPEL part as the root */
   public static final int    PROCESS_ACTIVITY     = 0;
   /** Use the FaultHandlers BPEL part as the root */
   public static final int    FAULT_HANDLERS       = 1;
   /** Use the EventHandlers BPEL part as the root */
   public static final int    EVENT_HANDLERS       = 2;
   /** Use the CompensationHandler BPEL part as the root */
   public static final int    COMPENSATION_HANDLER = 3;
   /** Use the TerminationHandler BPEL part as the root */
   public static final int    TERMINATION_HANDLER = 4;
   
   /** Indicates which part should be used as the root. */
   private int mProcessMode = PROCESS_ACTIVITY;
   /** BPEL process model */
   private AeBpelProcessObject mBpelProcessModel = null;
   
   /**
    * Constructs the root controller.
    * @param aProcess BPEL process model.
    * @param aProcessMode the part of the process to use.
    */
   public AeBpelProcessRootController(AeBpelProcessObject aProcess, int aProcessMode)
   {      
      super();
      mProcessMode = aProcessMode;
      mBpelProcessModel = aProcess;
      setModel(aProcess);      
      AeBpelProcessFigure fig = new AeBpelProcessFigure(aProcess != null ? aProcess.getName() : "");  //$NON-NLS-1$
      fig.setLayout(new AeXyLayoutManager());
      setFigure(fig);      
   }
   
   /**
    * @return Returns the processMode.
    */
   public int getProcessMode()
   {
      return mProcessMode;
   }
      
   /**
    * Returns the process model.
    * @return process model.
    */
   public AeBpelProcessObject getBpelProcessModel()
   {
      return mBpelProcessModel;
   }
   
   /** 
    * Overrides method to set the model as well as obtain all the links and add the links
    * to the model's list of children.
    * @see org.activebpel.rt.bpeladmin.war.graph.ui.controller.IAeGraphController#setModel(java.lang.Object)
    * @param aModel BPEL model.
    */
   public void setModel(Object aModel)
   {      
      
      if (aModel == null)
      {
         return;
      }
      
      AeBpelObjectBase rootModel = null;
      if (aModel instanceof AeBpelProcessObject)
      {
         AeBpelProcessObject process = (AeBpelProcessObject) aModel;
         int mode = getProcessMode();
         switch(mode)
         {
            case FAULT_HANDLERS:
            {
               rootModel = process.getFaultHandlers();
               break;
            }
            case EVENT_HANDLERS:
            {
               rootModel = process.getEventHandlers();
               break;
            }
            case COMPENSATION_HANDLER:
            {
               rootModel = process.getCompensationHandler();
               break;
            }
            case TERMINATION_HANDLER:
            {
               rootModel = process.getTerminationHandler();
               break;
            }
            case PROCESS_ACTIVITY:
            {
               rootModel = process.getProcessActivity();
               break;
            }                           
         }// switch
      }  
      
      if (rootModel != null)
      {
         // set the model.
         super.setModel(aModel);
         // remove all but the root model so that only the selected root activity, fault handler,
         // event handler or compensation handler is shown.
         Iterator it = getModelChildren().iterator();
         while (it.hasNext())
         {
            AeBpelObjectBase childModel = (AeBpelObjectBase) it.next();
            if (childModel != rootModel)
            {
               it.remove();
            }
         }
         
         // create set of links
         // make sure that the links are added last so that the z-order is
         // such that the links are painted last.
         addLinks();               
         
      }
   }
   
   /**
    * Returns the BPEL model.
    */
   public AeBpelObjectBase getBpelModel()
   {
      return (AeBpelObjectBase) getModel();
   }

   /** 
    * Adds all of the links found in this model.
    */
   private void addLinks()
   {      
      List links = mBpelProcessModel.getLinks();
      List tempList = new ArrayList();
      for (int i =0; i < links.size(); i++)
      {
         AeBpelLinkObject link = (AeBpelLinkObject) links.get(i);
         if (link.isEvaluated())
         {
            // append evaluated links to the end of the list to increase its z-order.
            // (this have, if we need to highlight the link, it can be drawn last, over the other links). 
            tempList.add(link);
         }
         else 
         {
            // insert other links to the start of the list.
            tempList.add(0,link);
         }
      }
      getModelChildren().addAll(tempList);
   }
   
}
