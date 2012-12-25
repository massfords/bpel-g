//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeOnEventCorrelations.java,v 1.2 2006/10/26 14:01:36 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support; 

import java.util.Iterator;

import org.activebpel.rt.bpel.def.AeCorrelationSetsDef;
import org.activebpel.rt.bpel.def.AeCorrelationsDef;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.util.AeFilteredIterator;

/**
 * Impl for the correlations used for onEvent. There is special handling here to filter
 * out any references to correlation sets that resolve to the child scope. The reason
 * being that the child scope is not instantiated until a message arrives.    
 */
public class AeOnEventCorrelations extends AeIMACorrelations
{
   public AeOnEventCorrelations(AeCorrelationsDef aDef, AeOnEvent aParent)
   {
      super(aDef, aParent);
   }

   /**
    * Overrides to filter out any correlation sets that are defined within the onEvent's associated scope.
    * The onEvent's concurrency implementation results in the scope's creation being deferred until the message arrives.
    * As such, the scope and its correlation set info won't be available here - nor is it needed since the 
    * correlation information we're looking for is for initiated correlation sets only.
    * @see org.activebpel.rt.bpel.impl.activity.support.AeIMACorrelations#getInitiatedCorrelationDefs()
    */
   protected Iterator getInitiatedCorrelationDefs()
   {
      return new AeFilteredIterator(super.getInitiatedCorrelationDefs())
      {
         /**
          * @see org.activebpel.rt.util.AeFilteredIterator#accept(java.lang.Object)
          */
         protected boolean accept(Object aObject)
         {
            AeCorrelationDef corrDef = (AeCorrelationDef)aObject;
            String corrSetName = corrDef.getCorrelationSetName();
            AeOnEvent onEvent = (AeOnEvent) getParent();
            AeOnEventDef def = (AeOnEventDef) onEvent.getDefinition();
            AeCorrelationSetsDef scopeCorrelations = def.getChildScope().getCorrelationSetsDef();
            if (scopeCorrelations != null && scopeCorrelations.getCorrelationSetDef(corrSetName) != null)
            {
               return false;
            }
            return true;
         }
      };
   }
}
 