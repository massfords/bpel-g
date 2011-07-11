//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeWSBPELImplementationTraverser.java,v 1.9 2007/11/15 22:31:11 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor;

/**
 * WS-BPEL 2.0 traverser
 */
public class AeWSBPELImplementationTraverser extends AeImplementationTraverser
{
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeOnAlarmDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      if (aDef.getRepeatEveryDef() == null)
      {
         super.traverse(aDef, aVisitor);
      }
      else
      {
         traverseDocumentationDefs(aDef, aVisitor);
         traverseForAndUntilDefs(aDef, aVisitor);
         callAccept(aDef.getRepeatEveryDef(), aVisitor);
         // avoid traversing the scope def since we defer the creation of the
         // scope impl until the alarm fires.
         // callAccept(aDef.getActivityDef(), aVisitor);
         traverseExtensionDefs(aDef, aVisitor);
      }
   }
}
 