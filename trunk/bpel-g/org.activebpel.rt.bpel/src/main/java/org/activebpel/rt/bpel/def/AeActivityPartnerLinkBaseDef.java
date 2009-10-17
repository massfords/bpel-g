// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/AeActivityPartnerLinkBaseDef.java,v 1.12 2006/09/11 23:06:28 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.activity.IAePartnerLinkActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.message.AeMessagePartsMap;

/**
 * Definition for bpel partner link based activity
 */
public abstract class AeActivityPartnerLinkBaseDef extends AeActivityDef implements
      IAePartnerLinkActivityDef, IAeCorrelationsParentDef
{
   /** delegate which handle the partner link and correlation information. */
   private AePartnerLinkDelegate mDelegate = new AePartnerLinkDelegate();
   
   /**
    * Returns the delegate.
    */
   protected AePartnerLinkDelegate getDelegate()
   {
      return mDelegate;
   }

   /**
    * Provides the ability to add a correlation element to the correlation list.
    * 
    * @param aCorrelation the correlation to be added
    */
   public void addCorrelation(AeCorrelationDef aCorrelation)
   {
      getDelegate().addCorrelation(aCorrelation);
   }
   
   /**
    * Provide a list of the Correlation objects for the user to iterate .
    * 
    * @return iterator of AeCorrelationDef object
    */
   public Iterator getCorrelationList()
   {
      return getDelegate().getCorrelationDefs();
   }
   
   /**
    * Utility method to determine if a message has a correlation list 
    * @return true if there are elements in the correlation list.
    */
   public boolean hasCorrelationList()
   {
      return getDelegate().hasCorrelationList();
   }
   
   /**
    * Returns the name of the partner link associated with this activity.
    */
   public String getPartnerLink()
   {
      return getDelegate().getPartnerLink();
   }

   /**
    * Set the name of the partner link associated with this activity.
    */
   public void setPartnerLink(String aPartnerLink)
   {
      getDelegate().setPartnerLink(aPartnerLink);
   }

   /**
    * Accessor method to obtain the port type for the object.
    * 
    * @return QName of the port type for the object
    */
   public QName getPortType()
   {
      return getDelegate().getPortType();
   }

   /**
    * Mutator method to set the port type for the object.
    * 
    * @param aPortType the port type value to be set
    */
   public void setPortType(QName aPortType)
   {
      getDelegate().setPortType(aPortType);
   }

   /**
    * Accessor method to obtain the operation for the object.
    * 
    * @return name of the operation for the object
    */
   public String getOperation()
   {
      return getDelegate().getOperation();
   }

   /**
    * Mutator method to set the operation for the object.
    * 
    * @param aOperation the operation value to be set
    */
   public void setOperation(String aOperation)
   {
      getDelegate().setOperation(aOperation);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCorrelationsParentDef#setCorrelationsDef(org.activebpel.rt.bpel.def.AeCorrelationsDef)
    */
   public void setCorrelationsDef(AeCorrelationsDef aDef)
   {
      getDelegate().setCorrelationsDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCorrelationsParentDef#getCorrelationsDef()
    */
   public AeCorrelationsDef getCorrelationsDef()
   {
      return getDelegate().getCorrelationsDef();
   }
   
   /**
    * Gets the partner link key.
    */
   private AePartnerLinkDefKey getPartnerLinkKey()
   {
      AePartnerLinkDef plinkDef = getPartnerLinkDef();
      if (plinkDef == null)
         return null;

      return new AePartnerLinkDefKey(plinkDef);
   }
   
   /**
    * Gets the partner link def referenced by this activity
    */
   public AePartnerLinkDef getPartnerLinkDef()
   {
      return AeDefUtil.findPartnerLinkDef(this, getPartnerLink());
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAePartnerLinkActivityDef#getPartnerLinkOperationKey()
    */
   public AePartnerLinkOpKey getPartnerLinkOperationKey()
   {
      AePartnerLinkDefKey defKey = getPartnerLinkKey();
      if (defKey == null)
         return null;

      return new AePartnerLinkOpKey(defKey, getOperation());
   }

   /**
    * Sets the message parts map for the request message.
    */
   public void setConsumerMessagePartsMap(AeMessagePartsMap aInputMessagePartsMap)
   {
      getDelegate().setConsumerMessagePartsMap(aInputMessagePartsMap);
   }

   /**
    * Returns the message parts map for the request message.
    */
   public AeMessagePartsMap getConsumerMessagePartsMap()
   {
      return getDelegate().getConsumerMessagePartsMap();
   }

   /**
    * Sets the message parts map for the response message.
    */
   public void setProducerMessagePartsMap(AeMessagePartsMap aOutputMessagePartsMap)
   {
      getDelegate().setProducerMessagePartsMap(aOutputMessagePartsMap);
   }

   /**
    * Returns the message parts map for the response message.
    */
   public AeMessagePartsMap getProducerMessagePartsMap()
   {
      return getDelegate().getProducerMessagePartsMap();
   }
}
