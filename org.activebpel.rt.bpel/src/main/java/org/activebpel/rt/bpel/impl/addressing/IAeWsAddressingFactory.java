//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/addressing/IAeWsAddressingFactory.java,v 1.1 2006/08/08 16:44:26 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.addressing;

/**
 * Interface for a factory class that hands out the appropriate serializer/deserializer
 * for a given WSA namespace
 */
public interface IAeWsAddressingFactory
{
   /**
    * Returns the WS-Addressing deserializer for a given namespace.  
    * The default deserializer is returned if the namespace parameter is null.
    * @param aNamespace
    * @return the Deserializer
    */
   public IAeAddressingDeserializer getDeserializer(String aNamespace);
   
   /**
    * Returns the WS-Addressing serializer for a given namespace.  
    * The default serializer is returned if the namespace parameter is null.
    * @param aNamespace
    * @return the Serializer
    */
   public IAeAddressingSerializer getSerializer(String aNamespace);


}