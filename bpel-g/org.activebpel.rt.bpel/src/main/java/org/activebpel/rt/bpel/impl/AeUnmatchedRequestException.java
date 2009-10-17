//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeUnmatchedRequestException.java,v 1.2 2006/06/26 16:50:30 mford Exp $
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
 * A correlation violation resulting from an unmatched request. 
 */
public class AeUnmatchedRequestException extends AeCorrelationViolationException
{
   /**
    * Ctor takes namespace for the bpel fault
    * @param aNamespace
    */
   public AeUnmatchedRequestException(String aNamespace)
   {
      super(AeMessages.getString("AeUnmatchedRequestException.Message"), AeFaultFactory.getFactory(aNamespace).getUnmatchedRequest()); //$NON-NLS-1$
   }
}
 