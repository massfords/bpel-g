// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/produce/IAeMessageDataProducerContext.java,v 1.2 2007/11/15 02:24:40 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.wsio.produce;

import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;

/**
 * Defines the interface for a message data producer to interact with a BPEL
 * implementation object.
 */
public interface IAeMessageDataProducerContext
{
   /**
    * Returns the BPEL implementation object.
    */
   public AeAbstractBpelObject getBpelObject();
   
   /**
    * Returns the variable that contains the data if this is a variable
    * interaction.
    */
   public IAeVariable getVariable();
   
   /**
    * Getter for the def
    */
   public IAeMessageDataProducerDef getMessageDataProducerDef();
}
