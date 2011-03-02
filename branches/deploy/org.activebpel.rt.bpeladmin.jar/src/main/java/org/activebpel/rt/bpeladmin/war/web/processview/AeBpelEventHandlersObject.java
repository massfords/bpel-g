//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeBpelEventHandlersObject.java,v 1.2 2005/12/03 01:10:56 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import org.activebpel.rt.bpel.def.AeEventHandlersDef;

/**
 * BPEL model for the root level event handlers.
 */
public class AeBpelEventHandlersObject extends AeBpelObjectContainer
{

   /**
    * Ctor
    * @param aDef activity definition.
    */
   public AeBpelEventHandlersObject(AeEventHandlersDef aDef)
   {
      super(AeEventHandlersDef.TAG_EVENT_HANDLERS, aDef);
   }    
}
