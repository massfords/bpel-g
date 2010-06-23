//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeReceiveHandler.java,v 1.2 2008/02/17 21:37:07 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.wsio.IAeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.receive.IAeMessageContext;
import org.w3c.dom.Document;

/**
 * Interface for pluggable receive handlers to process receives from a partner.
 * The methods represent the public API for the BPEL engine to interact with the handlers  
 */
public interface IAeReceiveHandler
{
   /**
    * Convert raw service message data into our internal message data format.
    * Queue the inbound receive with the BPEL engine or appropriate 
    * protocol manager (i.e. WSRM manager for WSRM requests) 
    * 
    * @param aData
    * @param aContext
    * @throws AeBusinessProcessException
    */
   public IAeWebServiceResponse handleReceiveData( IAeWebServiceMessageData aData, IAeMessageContext aContext ) throws AeBusinessProcessException;

   /**
    * Convert raw service message data into our internal message data format.
    * Queue the inbound receive with the BPEL engine or appropriate 
    * protocol manager (i.e. WSRM manager for WSRM requests) 
    * @param aDocArray
    * @param aContext
    * 
    * @return service response from engine
    * @throws AeBusinessProcessException 
    */
   public IAeWebServiceResponse handleReceiveData( Document[] aDocArray, IAeMessageContext aContext ) throws AeBusinessProcessException;   
}
