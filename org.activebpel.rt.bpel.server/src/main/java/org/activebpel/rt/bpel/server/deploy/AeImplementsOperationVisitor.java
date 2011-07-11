//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeImplementsOperationVisitor.java,v 1.1 2007/09/02 17:17:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy; 

import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
import org.activebpel.rt.bpel.def.visitors.AeAbstractSearchVisitor;

/**
 * Visitor that checks to see if the process provides an IMA for the given
 * plink and operation. 
 */
public class AeImplementsOperationVisitor extends AeAbstractSearchVisitor
{
   /** operation we're looking for */
   private String mOperation;
   /** plink def we're looking for */
   private AePartnerLinkDef mPartnerLinkDef;
   /** set to true if found */
   private boolean mFound;
   
   /**
    * Ctor
    * @param aOperation
    * @param aPlinkDef
    */
   public AeImplementsOperationVisitor(String aOperation, AePartnerLinkDef aPlinkDef)
   {
      setOperation(aOperation);
      setPartnerLinkDef(aPlinkDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef aDef)
   {
      checkFound(aDef.getPartnerLinkDef(), aDef.getOperation());
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      checkFound(aDef.getPartnerLinkDef(), aDef.getOperation());
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef aDef)
   {
      checkFound(aDef.getPartnerLinkDef(), aDef.getOperation());
      super.visit(aDef);
   }

   /**
    * @param aPartnerLinkDef
    * @param aOperation
    */
   private void checkFound(AePartnerLinkDef aPartnerLinkDef, String aOperation)
   {
      setFound(getPartnerLinkDef().equals(aPartnerLinkDef) && getOperation().equals(aOperation));
   }

   /**
    * @return the operation
    */
   protected String getOperation()
   {
      return mOperation;
   }
   /**
    * @param aOperation the operation to set
    */
   protected void setOperation(String aOperation)
   {
      mOperation = aOperation;
   }
   /**
    * @return the partnerLinkDef
    */
   protected AePartnerLinkDef getPartnerLinkDef()
   {
      return mPartnerLinkDef;
   }

   /**
    * @param aPartnerLinkDef the partnerLinkDef to set
    */
   protected void setPartnerLinkDef(AePartnerLinkDef aPartnerLinkDef)
   {
      mPartnerLinkDef = aPartnerLinkDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractSearchVisitor#isFound()
    */
   public boolean isFound()
   {
      return mFound;
   }
   
   /**
    * Setter for found
    * @param aFound
    */
   protected void setFound(boolean aFound)
   {
      mFound = aFound;
   }
}
 