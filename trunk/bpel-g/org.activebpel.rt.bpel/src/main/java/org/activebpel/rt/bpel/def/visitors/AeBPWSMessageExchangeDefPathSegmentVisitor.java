//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeBPWSMessageExchangeDefPathSegmentVisitor.java,v 1.1 2006/11/14 19:47:34 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.def.AeMessageExchangeDef;
import org.activebpel.rt.bpel.def.AeMessageExchangesDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;

/**
 * A def path visitor extension for BPEL4WS 1.1 which also assigns paths for message exchanges.
 */
public class AeBPWSMessageExchangeDefPathSegmentVisitor extends AeBPWSDefPathSegmentVisitor
{

   /**
    * Overrides method to assign a segment path.
    * @see org.activebpel.rt.bpel.def.visitors.AeBPWSDefPathSegmentVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangesDef)
    */
   public void visit(AeMessageExchangesDef aDef)
   {
      setPathSegment(IAeBPELConstants.TAG_MESSAGE_EXCHANGES);
   }

   /**
    *
    * Overrides method to assign a segment path.
    * @see org.activebpel.rt.bpel.def.visitors.AeBPWSDefPathSegmentVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangeDef)
    */
   public void visit(AeMessageExchangeDef aDef)
   {
      setPathSegment(IAeBPELConstants.TAG_MESSAGE_EXCHANGE);
   }

}
