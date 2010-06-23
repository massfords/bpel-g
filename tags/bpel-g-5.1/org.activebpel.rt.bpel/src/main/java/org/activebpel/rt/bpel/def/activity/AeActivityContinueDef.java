//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/AeActivityContinueDef.java,v 1.2 2006/09/25 01:34:39 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity; 

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Definition object for a continue activity. These activities must be nested
 * within a looping construct like a while or a for-each.
 */
public class AeActivityContinueDef extends AeActivityDef implements IAeExtensionActivityDef
{
   /**
    * Ctor 
    */
   public AeActivityContinueDef()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeExtensionActivityDef#isUnderstood()
    */
   public boolean isUnderstood()
   {
      return true;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeExtensionActivityDef#getNamespace()
    */
   public String getNamespace()
   {
      return IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
 