//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeWSBPELMessageExchangeVisitor.java,v 1.1 2006/09/22 19:52:37 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import org.activebpel.rt.bpel.def.AeMessageExchangesDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;

/**
 * Adds support for the onEvent's child scope as a root scope
 */
public class AeWSBPELMessageExchangeVisitor extends AeMessageExchangeVisitor
{
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      // this visitor is run prior to the static analysis code so it's possible the onEvent is invalid and doesn't have a child scope.
      if (aDef.getChildScope() != null)
      {
         AeMessageExchangesDef msgExsDef = getOrCreateMessageExchangesDef(aDef.getChildScope().getScopeDef());
         msgExsDef.setDefaultDeclared(true);
      }

      super.visit(aDef);
   }
}
 