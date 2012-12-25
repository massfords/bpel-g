// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/endpoints/IAeEndpointDeserializer.java,v 1.2 2004/07/08 13:10:00 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.endpoints;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeEndpointReference;

import org.w3c.dom.Element;

/**
 * Defines the interface for a class capable of parsing an IAeEndpointReference from
 * an DOM element. This provides a layer of abstraction between endpoint references
 * and their xml format, primarily since WS-Addressing is not yet an open standard
 * and it's possible that there may arise a competing standard for serializing 
 * endpoints as xml. 
 */
public interface IAeEndpointDeserializer
{
   /**
    * Parses an IAeEndpointReference from a DOM Element.
    * @param aElement
    */
   public IAeEndpointReference deserializeEndpoint(Element aElement) throws AeBusinessProcessException;

   /**
    * Populates an existing reference with the endpoint information contained within
    * the DOM Element.
    * @param aElement
    * @param aRef - gets populated and returned if not null, otherwise a new ref is created, populated, and returned
    */
   public IAeEndpointReference deserializeEndpoint(Element aElement, IAeEndpointReference aRef) throws AeBusinessProcessException;
}
