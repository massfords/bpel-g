// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDefOneWayVisitor.java,v 1.9 2008/01/11 19:31:16 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.IAeReceiveActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;

/**
 * This visitor traverses the tree looking for receives and on-message object.  For each receive 
 * or on-message object it finds, it determines whether the object is one-way or not.
 */
public class AeDefOneWayVisitor extends AeAbstractDefVisitor
{
   /** The WSDL provider. */
   private IAeContextWSDLProvider mProvider;
   /** The process def. */
   private AeProcessDef mProcessDef;

   /**
    * Constructs the visitor with the given WSDL provider.
    * 
    * @param aProvider The WSDL provider.
    */
   public AeDefOneWayVisitor(IAeContextWSDLProvider aProvider)
   {
      mProvider = aProvider;

      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      mProcessDef = aDef;
      super.visit(aDef);
   }

   /**
    * This method determines if a port type and operation for a given WSDL def is a one-way operation.
    * 
    * @param aWsdlDef The WSDL def.
    * @param aPortType The port type qname.
    * @param aOperation The operation name.
    * @return True if the operation is one-way.
    */
   protected boolean defIsOneWay(AeBPELExtendedWSDLDef aWsdlDef, QName aPortType, String aOperation)
   {
      boolean oneWay = false;
      if (aWsdlDef != null)
      {
         PortType portType = aWsdlDef.getPortType(aPortType);
         if (portType != null)
         {
            Operation op = portType.getOperation(aOperation, null, null);
            if (op != null && op.getOutput() == null)
            {
               oneWay = true;
            }
         }
      }
      return oneWay;
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef aDef)
   {
      examineForOneWay(aDef);
      super.visit(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef aDef)
   {
      examineForOneWay(aDef);
      super.visit(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      visit((AeOnMessageDef) aDef);
   }

   /**
    * Sets the one way flag on the def if it's a one-way receive. 
    * 
    * @param aDef
    */
   protected void examineForOneWay(IAeReceiveActivityDef aDef)
   {
      // TODO (EPW) Is it possible that the def won't extend AeBaseDef?  Maybe for extensions...
      AePartnerLinkDef pDef = AeDefUtil.getProcessDef((AeBaseDef) aDef).findPartnerLink(aDef.getPartnerLinkOperationKey());
      if (pDef != null)
      {
         AeBPELExtendedWSDLDef wsdlDef = AeWSDLDefHelper.getWSDLDefinitionForPortType(mProvider, pDef.getMyRolePortType());
         if (defIsOneWay(wsdlDef, pDef.getMyRolePortType(), aDef.getOperation()))
         {
            markAsOneWay(pDef, aDef);
         }
      }
   }

   /**
    * Marks the def as being a one-way def. This is broken out as a separate method
    * for the unit test.
    *
    * @param aPartnerLinkDef 
    * @param aDef
    */
   protected void markAsOneWay(AePartnerLinkDef aPartnerLinkDef, IAeReceiveActivityDef aDef)
   {
      mProcessDef.addOneWayReceive(aPartnerLinkDef.getPartnerLinkTypeName(), aDef.getOperation());
      aDef.setOneWay(true);
   }
}
