//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeNoOpAssignExtObjVisitor.java,v 1.1 2007/10/12 16:09:48 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.def.AeProcessDef;
/**
 * A NoOp visitor for BPEL 1.1 since we do not expect AeChildExtensionActivityDef for BPEL 1.1
 */
public class AeNoOpAssignExtObjVisitor extends AeAbstractDefVisitor
{

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      // do nothing
   }
  
}
