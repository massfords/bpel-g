//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeMessageExchangeVisitor.java,v 1.7 2006/11/03 22:48:00 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.def.AeMessageExchangesDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.IAeMessageExchangesParentDef;
import org.activebpel.rt.bpel.def.activity.AeActivityForEachDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;

/**
 * Records the messageExchange attribute for all of the createInstance activities.
 * This value is used to populate the messageExchange value on the reply object
 * that we queue with the create message. This is required to select the correct
 * reply receiver when the reply activity executes.
 *
 * Also marks the root scopes (currently limited to AeProcessDef and child scopes
 * of parallel forEach) as implicitly declaring default message exchange values.
 */
public class AeMessageExchangeVisitor extends AeAbstractDefVisitor
{
   /**
    * Ctor
    */
   public AeMessageExchangeVisitor()
   {
      setTraversalVisitor( new AeTraversalVisitor( new AeDefTraverser(), this ) );
   }

   /**
    * Marks the child scope as implicitly declaring a default message exchange value.
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef)
    */
   public void visit(AeActivityForEachDef aDef)
   {
      if (aDef.isParallel() && aDef.getActivityDef() instanceof AeActivityScopeDef)
      {
         AeMessageExchangesDef msgExsDef = getOrCreateMessageExchangesDef(aDef.getChildScope().getScopeDef());
         msgExsDef.setDefaultDeclared(true);
      }
      super.visit(aDef);
   }

   /**
    * Marks the process def as implicitly declaring a default message exchange value
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      AeMessageExchangesDef msgExsDef = getOrCreateMessageExchangesDef(aDef);
      msgExsDef.setDefaultDeclared(true);
      super.visit(aDef);
   }

   /**
    * Gets or creates the messageExchanges def from the given def.
    *
    * @param aDef
    */
   protected AeMessageExchangesDef getOrCreateMessageExchangesDef(IAeMessageExchangesParentDef aDef)
   {
      AeMessageExchangesDef msgExsDef = aDef.getMessageExchangesDef();
      if (msgExsDef == null)
      {
         msgExsDef = new AeMessageExchangesDef();
         msgExsDef.setImplict(true);
         aDef.setMessageExchangesDef(msgExsDef);
      }
      return msgExsDef;
   }
}
