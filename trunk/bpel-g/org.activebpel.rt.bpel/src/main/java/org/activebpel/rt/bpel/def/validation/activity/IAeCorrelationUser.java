//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/IAeCorrelationUser.java,v 1.3 2006/09/27 00:36:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import org.activebpel.rt.bpel.def.validation.IAeValidator;
import org.activebpel.rt.message.AeMessagePartsMap;

/**
 * interface for models that use correlation data
 */
public interface IAeCorrelationUser extends IAeValidator
{
   /**
    * Gets the message parts map for the message being consumed
    */
   public AeMessagePartsMap getConsumerMessagePartsMap();

   /**
    * Gets the message parts map for the message being produced
    */
   public AeMessagePartsMap getProducerMessagePartsMap();

   /**
    * Returns true if the pattern attribute is required to be used with this model (only supported for invokes)
    */
   public boolean isPatternRequired();
}
 