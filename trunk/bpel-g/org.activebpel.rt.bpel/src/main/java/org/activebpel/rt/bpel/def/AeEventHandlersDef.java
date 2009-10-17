// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/AeEventHandlersDef.java,v 1.6 2007/03/03 02:45:33 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.def.activity.IAeEventContainerDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * This is the container for event handlers for a scope.  Event handlers
 * include onMessage and onAlarm activity containers.
 */
public class AeEventHandlersDef extends AeBaseDef 
      implements IAeEventContainerDef, IAeUncrossableLinkBoundary
{
   /** The list of on message definitions, may be null */
   private List mOnEventList = new ArrayList();
   /** The list of on alaram definitions, may be null */
   private List mOnAlarmList = new ArrayList();

   /**
    * Default constructor
    */
   public AeEventHandlersDef()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeEventContainerDef#addOnEventDef(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void addOnEventDef(AeOnEventDef aEvent)
   {
      mOnEventList.add(aEvent);
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeEventContainerDef#getOnEventDefs()
    */
   public Iterator getOnEventDefs()
   {
      return mOnEventList.iterator();
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeAlarmParentDef#addAlarmDef(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void addAlarmDef(AeOnAlarmDef aAlarm)
   {
      mOnAlarmList.add(aAlarm);
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeAlarmParentDef#getAlarmDefs()
    */
   public Iterator getAlarmDefs()
   {
      return mOnAlarmList.iterator();
   }

   /**
    * Returns true if has any onAlarm or onEvent defined.
    */
   public boolean hasEventHandler()
   {
      return getOnEventDefs().hasNext() || getAlarmDefs().hasNext();
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeUncrossableLinkBoundary#canCrossInbound()
    */
   public boolean canCrossInbound()
   {
      return false;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeUncrossableLinkBoundary#canCrossOutbound()
    */
   public boolean canCrossOutbound()
   {
      return false;
   }
}
