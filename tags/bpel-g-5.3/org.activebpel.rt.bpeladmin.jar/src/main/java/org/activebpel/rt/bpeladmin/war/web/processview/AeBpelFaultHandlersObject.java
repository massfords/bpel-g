//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeBpelFaultHandlersObject.java,v 1.3 2006/06/26 18:38:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import org.activebpel.rt.bpel.def.AeFaultHandlersDef;

/**
 * BPEL model for the root level fault handlers.
 */
public class AeBpelFaultHandlersObject extends AeBpelObjectContainer
{

   /**
    * Ctor.
    * @param aDef fault handler definition.
    */
   public AeBpelFaultHandlersObject(AeFaultHandlersDef aDef)
   {
      super(AeFaultHandlersDef.TAG_FAULT_HANDLERS, aDef);
   }      

}
