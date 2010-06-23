//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeCreateInstanceVisitor.java,v 1.3 2007/09/26 02:21:04 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import org.activebpel.rt.bpel.def.activity.IAeReceiveActivityDef;
import org.activebpel.rt.xml.def.AeBaseXmlDef;

public class AeCreateInstanceVisitor extends AeAbstractEntryPointVisitor
{
   /** tracks if we've seen a create instance visitor */
   private boolean mCreateInstanceFound = false;
   
   /**
    * Keep traversing until we find a create instance 
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#traverse(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   protected void traverse(AeBaseXmlDef aDef)
   {
      if (!isCreateInstanceFound())
      {
         super.traverse(aDef);
      }
   }

   /**
    * Sets a flag that we've found a create instance
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractEntryPointVisitor#processEntryPoint(org.activebpel.rt.bpel.def.activity.IAeReceiveActivityDef)
    */
   protected void processEntryPoint(IAeReceiveActivityDef aDef)
   {
      mCreateInstanceFound = true;
   }

   /**
    * Getter for the create instance found field
    */
   public boolean isCreateInstanceFound()
   {
      return mCreateInstanceFound;
   }
}
 