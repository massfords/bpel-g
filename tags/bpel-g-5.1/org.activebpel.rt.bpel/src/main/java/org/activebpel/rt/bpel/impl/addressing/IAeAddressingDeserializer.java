// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/addressing/IAeAddressingDeserializer.java,v 1.3 2007/01/17 17:48:06 KPease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.addressing;

import javax.xml.soap.SOAPHeader;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.wsio.IAeWsAddressingConstants;
import org.w3c.dom.Element;

/**
 * Defines the interface for a class capable of parsing WS-Addressing headers from
 * into endpoint references from a SOAP Header element. 
 */
public interface IAeAddressingDeserializer extends IAeWsAddressingConstants
{
   /**
    * Parses a set of WS-Addressing Headers from a dom element.
    * @param aElement the element to parse
    */
   public IAeAddressingHeaders deserializeHeaders(Element aElement) throws AeBusinessProcessException;
   
   /**
    * Parses a set of WS-Addressing Headers from a SOAP Header.
    * @param aHeader the SOAPHeader to parse
    */
   public IAeAddressingHeaders deserializeHeaders(SOAPHeader aHeader) throws AeBusinessProcessException;

   /**
    * Populates an existing WS-Addressing Header object with information contained within
    * the element
    * @param aElement the Element to parse
    * @param aRef - gets populated and returned if not null, otherwise a new ref is created, populated, and returned
    */
   public IAeAddressingHeaders deserializeHeaders(Element aElement, IAeAddressingHeaders aRef) throws AeBusinessProcessException;
   
   /**
    * Populates an existing WS-Addressing Header object with information contained within
    * the SOAP header
    * @param aHeader the SOAPHeader to parse
    * @param aRef - gets populated and returned if not null, otherwise a new ref is created, populated, and returned
    */
   public IAeAddressingHeaders deserializeHeaders(SOAPHeader aHeader, IAeAddressingHeaders aRef) throws AeBusinessProcessException;
   
   /**
    * Returns true if the element is one that contains a wsa header
    * @param aElement
    */
   public boolean isEndpointHeader(Element aElement);
}
