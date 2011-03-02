//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/preprocess/strategies/wsio/IAeMessageDataStrategyNames.java,v 1.1 2006/08/18 22:20:34 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors.preprocess.strategies.wsio; 

/**
 * Provides the names for the valid strategies for producing and consuming message data 
 */
public interface IAeMessageDataStrategyNames
{
   /** name of the strategy where the variable is not present because it's an empty message */
   public static final String EMPTY_MESSAGE = "empty-message"; //$NON-NLS-1$
   /** name of the strategy where a message variable is used to produce/consume the message data */
   public static final String MESSAGE_VARIABLE = "message-variable";  //$NON-NLS-1$
   /** name of the strategy where an element variable is used to produce/consume the message data (single part element message only) */
   public static final String ELEMENT_VARIABLE = "element-variable"; //$NON-NLS-1$
   /** name of the strategy where the invoke/reply contains the toParts construct which 
    * copies other variable data into an anonymous temporary WSDL message variable */
   public static final String TO_PARTS = "to-parts"; //$NON-NLS-1$
   /** name of the strategy where the receive/onEvent/onMessage contains the fromParts construct which 
    * copies the message data to other variables within scope*/
   public static final String FROM_PARTS = "from-parts"; //$NON-NLS-1$
}
 