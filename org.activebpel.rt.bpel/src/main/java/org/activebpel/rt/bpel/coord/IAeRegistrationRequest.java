//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/coord/IAeRegistrationRequest.java,v 1.3 2006/05/24 23:07:01 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.coord;

/**
 * Interface for a WS-Coordination registation request. 
 * 
 * Note: This is an internal implementation tailored to be used with requirement 111
 * i.e. the interface provides a simple property getter and setter. 
 * 
 * The final implementation should follow something close to the Register definition
 * as per http://schemas.xmlsoap.org/ws/2004/10/wsoor.
 * Maybe refactor this interface to wsio.IWebServiceCoordRegistrationRequest. 
 */
public interface IAeRegistrationRequest
{   
   /**
    * Returns the coordination context.
    * @return coordination context. 
    */
   public IAeCoordinationContext getCoordinationContext();
   
   /**
    * Returns the protocol identifier string.
    * @return protocol id.
    */
   public String getProtocolIdentifier();
   
   /**
    * Sets the protocol identifier string.
    * @param aProtocolId protocol type to be used for coordination.
    */
   public void setProtocolIdentifier(String aProtocolId);
   
   /**
    * Sets a property. 
    * @param aName
    * @param aValue
    */
   public void setProperty(String aName, String aValue);
   
   /**
    * Returns a named property. 
    * @param aName
    * @return property value.
    */
   public String getProperty(String aName);
   
}
