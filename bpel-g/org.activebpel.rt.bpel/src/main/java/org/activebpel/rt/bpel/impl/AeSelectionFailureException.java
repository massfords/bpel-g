//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeSelectionFailureException.java,v 1.4 2007/05/23 15:25:00 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl; 

import org.activebpel.rt.bpel.AeMessages;

/**
 * Standard BPEL fault thrown when there is an error selecting data for a copy operation
 * or using a propertyAlias in selecting correlation data. 
 */
public class AeSelectionFailureException extends AeBpelException
{
   // fixme (MF) create real error messages for the errors below

   /** error message when the expression failed to select a single EII or TII */
   private static final String SELECTION_COUNT_ERROR = "AeSelectionFailureException.SELECTION_COUNT_ERROR"; //$NON-NLS-1$
   /** signals a selection failure due to having the copy op's keepSrcElementName option enabled but they failed to select an element */
   public static final String KEEP_SRC_ELEMENT_ERROR = "AeSelectionFailureException.KEEP_SRC_ELEMENT_ERROR"; //$NON-NLS-1$
   
   /**
    * Accepts the reason code uses it for the message.
    * 
    * @param aReason
    */
   public AeSelectionFailureException(String aNamespace, String aReason)
   {
      super(aReason, AeFaultFactory.getFactory(aNamespace).getSelectionFailure());
   }
   
   /**
    * Accepts the number of items selected by the expression.
    * @param aSelectionCount
    */
   public AeSelectionFailureException(String aNamespace, int aSelectionCount)
   {
      super(AeMessages.format(SELECTION_COUNT_ERROR, new Integer(aSelectionCount)), AeFaultFactory.getFactory(aNamespace).getSelectionFailure());
   }
}
 