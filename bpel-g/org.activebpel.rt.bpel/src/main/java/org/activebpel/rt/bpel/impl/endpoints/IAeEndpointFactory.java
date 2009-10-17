// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/endpoints/IAeEndpointFactory.java,v 1.2 2004/07/08 13:10:00 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.endpoints;


/**
 * Factory provides getters for serializers and deserializers for the endpoint
 * references based on the provided namespace.  
 */
public interface IAeEndpointFactory
{
   /**
    * Gets a serializer for the specified namespace
    * @param aNamespace
    */
   public IAeEndpointSerializer getSerializer(String aNamespace);

   /**
    * Gets a deserializer for the specified namespace
    * @param aNamespace
    */
   public IAeEndpointDeserializer getDeserializer(String aNamespace);
}
