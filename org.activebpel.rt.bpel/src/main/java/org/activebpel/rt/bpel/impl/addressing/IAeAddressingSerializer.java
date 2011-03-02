// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/addressing/IAeAddressingSerializer.java,v 1.4 2007/12/03 20:42:29 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.addressing;

import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.wsio.IAeWsAddressingConstants;
import org.activebpel.wsio.IAeWsAddressingHeaders;
import org.w3c.dom.Document;

/**
 * Responsible for serializing a set of WS-Addressing headers to a SOAPHeader element. 
 * This is used when creating SOAP messages. 
 */
public interface IAeAddressingSerializer extends IAeWsAddressingConstants
{
   
   /** Header element names with preferred prefix*/ 
   public static final String WSA_MESSAGE_ID_NAME = WSA_NS_PREFIX + ":" + IAeWsAddressingHeaders.WSA_MESSAGE_ID; //$NON-NLS-1$
   public static final String WSA_ACTION_NAME = WSA_NS_PREFIX + ":" + IAeWsAddressingHeaders.WSA_ACTION; //$NON-NLS-1$
   public static final String WSA_FAULT_TO_NAME = WSA_NS_PREFIX + ":" + IAeWsAddressingHeaders.WSA_FAULT_TO; //$NON-NLS-1$
   public static final String WSA_REPLY_TO_NAME = WSA_NS_PREFIX + ":" + IAeWsAddressingHeaders.WSA_REPLY_TO; //$NON-NLS-1$
   public static final String WSA_FROM_NAME = WSA_NS_PREFIX + ":" + IAeWsAddressingHeaders.WSA_FROM; //$NON-NLS-1$
   public static final String WSA_TO_NAME = WSA_NS_PREFIX + ":" + IAeWsAddressingHeaders.WSA_TO; //$NON-NLS-1$
   public static final String WSA_RECIPIENT_NAME = WSA_NS_PREFIX + ":" + IAeWsAddressingHeaders.WSA_RECIPIENT; //$NON-NLS-1$   
   public static final String WSA_ADDRESS_NAME = WSA_NS_PREFIX + ":" + IAeWsAddressingHeaders.WSA_ADDRESS; //$NON-NLS-1$
   public static final String ABX_CONVERSATION_ID_NAME = "abx:" + IAeWsAddressingHeaders.ABX_CONVERSATION_ID; //$NON-NLS-1$
   public static final String WSA_RELATES_TO_NAME = WSA_NS_PREFIX + ":" + IAeWsAddressingHeaders.WSA_RELATES_TO; //$NON-NLS-1$
   public static final String WSA_RELATIONSHIP_TYPE_NAME = WSA_NS_PREFIX + ":" + WSA_RELATIONSHIP_TYPE; //$NON-NLS-1$
   
   /**
    * Serializes the WS-Addressing information to a SOAPHeader.
    * @param aReference contains addressing information
    * @param aEnv SOAPEnvelope responsible for SOAP Name creation 
    */
   public SOAPHeader serializeHeaders(IAeAddressingHeaders aReference, SOAPEnvelope aEnv) throws AeBusinessProcessException;

   /**
    * Serializes the WS-Addressing information to a Document.
    * @param aReference contains addressing information
    */
   public Document serializeToDocument(IAeAddressingHeaders aReference) throws AeBusinessProcessException;
   
}
