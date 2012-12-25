//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeCreateInstanceOperationVisitor.java,v 1.3 2006/12/22 12:57:58 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.bpel.def.activity.AeActivityPickDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.IAeReceiveActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;

/**
 * Walks the process def looking to see if the createInstance operation is 
 * used by more than one IMA. If not, then an inbound receive routing optimization
 * is possible since we'll never route an inbound receive on this operation 
 * since it should always result in a new process instance.
 * 
 * This visitor should only be run on processes with a single start activity.
 */
public class AeCreateInstanceOperationVisitor extends AeAbstractDefVisitor
{
   /** maps the plink and operation to a count of the number of IMA's using the plink/op */
   private final Map<String, Integer> mOperationCount = new HashMap<String, Integer>();
   /** key for the create instance IMA in the above map */
   private String mCreateInstanceKey;
   
   /**
    * ctor 
    */
   public AeCreateInstanceOperationVisitor()
   {
      setTraversalVisitor( new AeTraversalVisitor( new AeDefTraverser(), this ) );
   }
   
   /**
    * Returns true if the create instance operation isn't used by any other IMA's
    */
   public boolean isCreateInstanceOnly()
   {
      Map map = getOperationCount();
      Integer i = (Integer) map.get(getCreateInstanceKey());
      return i == 1;
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef def)
   {
      super.visit(def);
      recordIMA(def, def.isCreateInstance());
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef def)
   {
      super.visit(def);
      boolean createInstance = false;
      if (def.getParent() instanceof AeActivityPickDef)
      {
         createInstance = ((AeActivityPickDef) def.getParent()).isCreateInstance();
      }
      recordIMA(def, createInstance);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef def)
   {
      super.visit(def);
      // pass false since an onEvent can never be a createInstance
      recordIMA(def, false);
   }
   
   /**
    * Records the IMA.
    * @param aDef
    * @param aCreateInstance
    */
   protected void recordIMA(IAeReceiveActivityDef aDef, boolean aCreateInstance)
   {
      String plink = aDef.getPartnerLink();
      String op = aDef.getOperation();
      String key = plink + "." + op; //$NON-NLS-1$
      
      if (aCreateInstance)
      {
         setCreateInstanceKey(key);
      }
      
      Map<String, Integer> map = getOperationCount();
      Integer i = map.get(key);
      if (i == null)
      {
         i = 1;
      }
      else
      {
         i = i.intValue() + 1;
      }
      map.put(key, i);
   }

   /**
    * @return Returns the operationCount.
    */
   protected Map<String, Integer> getOperationCount()
   {
      return mOperationCount;
   }

   /**
    * @return Returns the createInstanceKey.
    */
   protected String getCreateInstanceKey()
   {
      return mCreateInstanceKey;
   }

   /**
    * @param aCreateInstanceKey The createInstanceKey to set.
    */
   protected void setCreateInstanceKey(String aCreateInstanceKey)
   {
      mCreateInstanceKey = aCreateInstanceKey;
   }
}
 