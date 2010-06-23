// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/endpoints/IAeEndpointSerializer.java,v 1.4 2005/10/17 19:44:17 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.endpoints;

import org.activebpel.wsio.IAeWebServiceEndpointReference;
import org.w3c.dom.Document;

/**
 * Responsible for serializing an endpoint reference to a document fragment. This
 * is used when we're copying endpoint references in an assign's copy operation. 
 */
public interface IAeEndpointSerializer
{
   /**
    * Serializes the endpoint reference to a document fragment.
    * @param aReference
    */
   public Document serializeEndpoint(IAeWebServiceEndpointReference aReference);
}
