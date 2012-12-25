//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeBpelTerminationHandlerObject.java,v 1.1 2006/10/30 23:00:27 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import org.activebpel.rt.bpel.def.AeTerminationHandlerDef;

/**
 * BPEL model for the root level terminationHandler.
 */
public class AeBpelTerminationHandlerObject extends AeBpelObjectContainer
{

   /**
    * Ctor
    * @param aDef activity definition.
    */
   public AeBpelTerminationHandlerObject(AeTerminationHandlerDef aDef)
   {
      super(AeTerminationHandlerDef.TAG_TERMINATION_HANDLER, aDef);
   }   
}
