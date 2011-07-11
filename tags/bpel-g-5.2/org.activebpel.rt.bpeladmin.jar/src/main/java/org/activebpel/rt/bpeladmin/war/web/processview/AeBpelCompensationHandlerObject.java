//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeBpelCompensationHandlerObject.java,v 1.3 2006/06/26 18:38:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;

/**
 * BPEL model for the root level compensationHandler.
 */
public class AeBpelCompensationHandlerObject extends AeBpelObjectContainer
{

   /**
    * Ctor
    * @param aDef activity definition.
    */
   public AeBpelCompensationHandlerObject(AeCompensationHandlerDef aDef)
   {
      super(AeCompensationHandlerDef.TAG_COMPENSATION_HANDLER, aDef);
   }   
}
