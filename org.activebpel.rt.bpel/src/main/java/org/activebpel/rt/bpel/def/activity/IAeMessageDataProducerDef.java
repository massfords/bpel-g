//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/IAeMessageDataProducerDef.java,v 1.4 2007/12/08 12:02:57 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity; 

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartsDef;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.xml.def.IAeAdapter;

/**
 * interface for the wsio activity defs that produce message data 
 */
public interface IAeMessageDataProducerDef extends IAeAdapter
{
   /**
    * Getter for the variable def if the activity uses a variable to produce message data
    */
   public AeVariableDef getMessageDataProducerVariable();

   /**
    * Getter for the to parts def if the variable is using the toParts construct
    */
   public AeToPartsDef getToPartsDef();

   /**
    * Setter for the strategy name
    * @param aStrategy
    */
   public void setMessageDataProducerStrategy(String aStrategy);
   
   /**
    * Getter for the strategy name
    */
   public String getMessageDataProducerStrategy();

   /**
    * Returns the message parts map for the message being produced
    */
   public AeMessagePartsMap getProducerMessagePartsMap();
   
   /**
    * Setter for the message parts
    * @param aMap
    */
   public void setProducerMessagePartsMap(AeMessagePartsMap aMap);
   
   /**
    * Gets the port type
    */
   public QName getProducerPortType();
   
   /**
    * Gets the operation
    */
   public String getProducerOperation();
}
 