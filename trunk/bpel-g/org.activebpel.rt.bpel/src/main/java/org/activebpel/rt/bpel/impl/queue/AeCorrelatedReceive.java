// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/queue/AeCorrelatedReceive.java,v 1.7 2006/09/07 15:06:27 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.queue;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AePartnerLinkOpKey;

/**
 * Base class for correlated queue objects. No behavior here, just providing
 * the common fields and getters and setters.
 */
public class AeCorrelatedReceive extends AeAbstractQueuedObject
{
   /** Correlation data of message queue entry */
   protected Map mCorrelation;
   /** Identifies the process QName that this queued object belongs to */
   protected QName mProcessName;

   /**
    * Accepts the common data required for a correlated exchange.
    *
    * @param aPartnerLinkOpKey
    * @param aProcessQName
    * @param aCorrelation
    */
   public AeCorrelatedReceive(AePartnerLinkOpKey aPartnerLinkOpKey, QName aProcessQName, Map aCorrelation)
   {
      super(aPartnerLinkOpKey);
      setCorrelation(aCorrelation);
      setProcessName(aProcessQName);
   }

   /**
    * Setter for the process name
    *
    * @param aProcessName
    */
   public void setProcessName(QName aProcessName)
   {
      mProcessName = aProcessName;
   }

   /**
    * Getter for the process name
    */
   public QName getProcessName()
   {
      return mProcessName;
   }

   /**
    * @return Map The correlation map for this entry.
    */
   public Map getCorrelation()
   {
      if (mCorrelation == null)
         setCorrelation(new HashMap());
      return mCorrelation;
   }

   /**
    * Setter for the correlation map
    * @param aMap
    */
   public void setCorrelation(Map aMap)
   {
      mCorrelation = aMap;
   }

   /**
    * A queue object that is correlated has its correlation data participate in
    * the evaluation of equality. Queue objects that are not correlated will
    * only perform the comparison against the plink, port type, and operation.
    */
   public boolean isCorrelated()
   {
      return mCorrelation != null && !getCorrelation().isEmpty();
   }
}
