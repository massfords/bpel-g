//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeCorrelationsPatternImpl.java,v 1.2 2006/09/22 19:52:38 mford Exp $
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

import org.activebpel.rt.bpel.def.AeCorrelationsDef;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.util.AeFilteredIterator;

/**
 * Uses the pattern attribute when initiating or validating correlations sets 
 */
public class AeCorrelationsPatternImpl extends AeCorrelationsImpl
{
   /** if true, uses the request pattern correlations, otherwise uses the response pattern correlations */
   private boolean mRequest;
   
   /**
    * Ctor accepts the def, parent, and flag
    * @param aDef
    * @param aParent
    */
   public AeCorrelationsPatternImpl(AeCorrelationsDef aDef, IAeBpelObject aParent, boolean aRequest)
   {
      super(aDef, aParent);
      setRequest(aRequest);
   }
   
   /**
    * returns true if its a response
    */
   protected boolean isResponse()
   {
      return !isRequest();
   }

   /**
    * @return Returns the request.
    */
   protected boolean isRequest()
   {
      return mRequest;
   }

   /**
    * @param aRequest The request to set.
    */
   protected void setRequest(boolean aRequest)
   {
      mRequest = aRequest;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeCorrelations#getCorrelationDefs()
    */
   public Iterator getCorrelationDefs()
   {
      return new AePatternFilteredIterator(super.getCorrelationDefs());
   }
   
   /**
    * filters the corrDefs to only include those that match our pattern
    */
   class AePatternFilteredIterator extends AeFilteredIterator
   {
      public AePatternFilteredIterator(Iterator aIterator)
      {
         super(aIterator);
      }

      /**
       * @see org.activebpel.rt.util.AeFilteredIterator#accept(java.lang.Object)
       */
      protected boolean accept(Object aObject)
      {
         AeCorrelationDef corrDef = (AeCorrelationDef) aObject;
         return ((isRequest() && corrDef.isRequestDataUsedForCorrelation()) 
               || isResponse() && corrDef.isResponseDataUsedForCorrelation());
      }
   }
}
 