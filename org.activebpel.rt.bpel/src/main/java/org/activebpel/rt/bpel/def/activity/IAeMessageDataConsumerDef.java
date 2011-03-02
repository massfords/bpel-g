//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/IAeMessageDataConsumerDef.java,v 1.4 2007/12/08 12:02:57 mford Exp $
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
import org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.xml.def.IAeAdapter;

/**
 * interface for wsio activity defs that consume message data  
 */
public interface IAeMessageDataConsumerDef extends IAeAdapter
{
   /**
    * Gets the variable def that is being received into or null if not present
    */
   public AeVariableDef getMessageDataConsumerVariable();

   /**
    * Gets the from parts def or null if not present
    */
   public AeFromPartsDef getFromPartsDef();

   /**
    * Setter for the strategy name
    * @param aStrategy
    */
   public void setMessageDataConsumerStrategy(String aStrategy);

   /**
    * Getter for the strategy name
    */
   public String getMessageDataConsumerStrategy();
   
   /**
    * Returns the message parts map for the request
    */
   public AeMessagePartsMap getConsumerMessagePartsMap();
   
   /**
    * Setter for the message parts being consumed.
    * @param aMap
    */
   public void setConsumerMessagePartsMap(AeMessagePartsMap aMap);
   
   /**
    * Getter for the Port Type
    */
   public QName getConsumerPortType();
   
   /**
    * Getter for the operation
    */
   public String getConsumerOperation();
}
 