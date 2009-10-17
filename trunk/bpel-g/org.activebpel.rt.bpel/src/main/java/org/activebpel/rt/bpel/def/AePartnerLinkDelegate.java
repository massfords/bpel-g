//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/AePartnerLinkDelegate.java,v 1.4 2006/09/11 23:06:28 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import java.util.Collections;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.message.AeMessagePartsMap;

/**
 * Class to hold partner link related information.
 */
public class AePartnerLinkDelegate implements IAeCorrelationsParentDef
{
   /** partner link */
   private String mPartnerLink;
   /** port type.*/
   private QName mPortType;
   /** operation */
   private String mOperation;
   /** correlation container */
   private AeCorrelationsDef mCorrelationsDef;
   /** message parts map for the message being consumed */
   private AeMessagePartsMap mConsumerMessagePartsMap;
   /** message parts map for the message being produced */
   private AeMessagePartsMap mProducerMessagePartsMap;

   /**
    * Default ctor.
    */
   public AePartnerLinkDelegate()
   {
      super();
   }

   /**
    * Provides the ability to add a correlation element to the correlation list.
    * 
    * @param aCorrelation the correlation to be added
    */
   public void addCorrelation(AeCorrelationDef aCorrelation)
   {
      AeCorrelationsDef correlationsDef = getCorrelationsDef();
      if (correlationsDef == null)
      {
         correlationsDef = new AeCorrelationsDef();
         setCorrelationsDef(correlationsDef);
      }
      correlationsDef.addCorrelationDef(aCorrelation);
   }

   /**
    * Provide a list of the Correlation objects for the user to iterate .
    * 
    * @return iterator of AeCorrelationDef object
    */
   public Iterator getCorrelationDefs()
   {
      if (mCorrelationsDef == null)
         return Collections.EMPTY_LIST.iterator();
      else
         return mCorrelationsDef.getValues();
   }
   
   /**
    * Utility method to determine if a message has a correlation list 
    * @return true if there are elements in the correlation list.
    */
   public boolean hasCorrelationList()
   {
      if (mCorrelationsDef == null)
         return false;
      else
         return getCorrelationsDef().getSize() > 0;
   }
   
   /**
    * Returns the name of the partner link associated with this activity.
    */
   public String getPartnerLink()
   {
      return mPartnerLink;
   }

   /**
    * Set the name of the partner link associated with this activity.
    */
   public void setPartnerLink(String aPartnerLink)
   {
      mPartnerLink = aPartnerLink;
   }

   /**
    * Accessor method to obtain the port type for the object.
    * 
    * @return QName of the port type for the object
    */
   public QName getPortType()
   {
      return mPortType;
   }

   /**
    * Mutator method to set the port type for the object.
    * 
    * @param aPortType the port type value to be set
    */
   public void setPortType(QName aPortType)
   {
      mPortType = aPortType;
   }

   /**
    * Accessor method to obtain the operation for the object.
    * 
    * @return name of the operation for the object
    */
   public String getOperation()
   {
      return mOperation;
   }

   /**
    * Mutator method to set the operation for the object.
    * 
    * @param aOperation the operation value to be set
    */
   public void setOperation(String aOperation)
   {
      mOperation = aOperation;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCorrelationsParentDef#getCorrelationsDef()
    */
   public AeCorrelationsDef getCorrelationsDef()
   {
      return mCorrelationsDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCorrelationsParentDef#setCorrelationsDef(org.activebpel.rt.bpel.def.AeCorrelationsDef)
    */
   public void setCorrelationsDef(AeCorrelationsDef aCorrelations)
   {
      mCorrelationsDef = aCorrelations;
   }

   /**
    * Sets the message parts map for the input message.
    */
   public void setConsumerMessagePartsMap(AeMessagePartsMap aInputMessagePartsMap)
   {
      mConsumerMessagePartsMap = aInputMessagePartsMap;
   }

   /**
    * Returns the message parts map for the input message.
    */
   public AeMessagePartsMap getConsumerMessagePartsMap()
   {
      return mConsumerMessagePartsMap;
   }

   /**
    * Sets the message parts map for the output message.
    */
   public void setProducerMessagePartsMap(AeMessagePartsMap aOutputMessagePartsMap)
   {
      mProducerMessagePartsMap = aOutputMessagePartsMap;
   }

   /**
    * Returns the message parts map for the output message.
    */
   public AeMessagePartsMap getProducerMessagePartsMap()
   {
      return mProducerMessagePartsMap;
   }
}
