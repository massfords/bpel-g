// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeActivityParent.java,v 1.6 2006/10/26 14:01:36 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support;

import org.activebpel.rt.bpel.IAeActivity;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeBpelObject;

import java.util.Collections;
import java.util.Iterator;

/**
 * Base class for implementation objects that are parents for a single activity.
 */
abstract public class AeActivityParent
   extends AeAbstractBpelObject
   implements IAeActivityParent
{
   /** Child activity */
   protected IAeActivity mChild;

   /**
    * Takes the base def and parent object.
    * @param aDef
    * @param aParent
    */
   public AeActivityParent(AeBaseDef aDef, IAeBpelObject aParent)
   {
      super(aDef, aParent);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeActivityParent#addActivity(org.activebpel.rt.bpel.IAeActivity)
    */
   public void addActivity(IAeActivity aActivity)
   {
      setActivity(aActivity);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getChildrenForStateChange()
    */
   public Iterator getChildrenForStateChange()
   {
      return Collections.singleton(getActivity()).iterator();
   }

   /**
    * Getter for the activity.
    */
   public IAeActivity getActivity()
   {
      return mChild;
   }

   /**
    * Setter for the activity
    * @param aActivity
    */
   protected void setActivity(IAeActivity aActivity)
   {
      mChild = aActivity;
   }
}
