//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeCorrelationsImpl.java,v 1.4.16.1 2008/04/21 16:09:43 ppatruni Exp $
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

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.AeCorrelationsDef;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.message.IAeMessageData;

/**
 * impl for the &lt;correlations&gt; used by a wsio activity
 */
public class AeCorrelationsImpl implements IAeCorrelations
{
   /** correlations def */
   private AeCorrelationsDef mDef;
   
   /** reference to our parent activity */
   private IAeBpelObject mParent;
   
   /**
    * Ctor takes the ref to the parent wsio activity
    * @param aParent
    */
   public AeCorrelationsImpl(AeCorrelationsDef aDef, IAeBpelObject aParent)
   {
      mParent = aParent;
      mDef = aDef;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeCorrelations#initiateOrValidate(org.activebpel.rt.message.IAeMessageData, org.activebpel.rt.message.AeMessagePartsMap)
    */
   public void initiateOrValidate(IAeMessageData aData, AeMessagePartsMap aMessagePartsMap) throws AeBusinessProcessException
   {
      for(Iterator iter = getCorrelationDefs(); iter.hasNext(); )
      {
         AeCorrelationDef corrDef = (AeCorrelationDef)iter.next();
         String csName = corrDef.getCorrelationSetName();
         AeCorrelationSet corrSet = findCorrelationSet(csName);
         corrSet.initiateOrValidate(aData, aMessagePartsMap, corrDef.getInitiate());
      }
   }

   /**
    * Finds the correlation set through the parent
    * @param csName
    */
   protected AeCorrelationSet findCorrelationSet(String csName)
   {
      return getParent().findCorrelationSet(csName);
   }
   
   /**
    * Getter for the parent
    */
   protected IAeBpelObject getParent()
   {
      return mParent;
   }
   
   /**
    * Getter for the def
    */
   protected AeCorrelationsDef getDef()
   {
      return mDef;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeCorrelations#getCorrelationDefs()
    */
   public Iterator getCorrelationDefs()
   {
      return getDef().getValues();
   }
}
