//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/coord/IAeCreateContextRequest.java,v 1.4 2006/05/24 23:07:01 PJayanetti Exp $
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
 * Interface for a WS-Coordination CreateCoordinationContext 
 * 
 * Note: This is an internal implementation tailored to be used with requirement 111
 * ie - the interface provides a simple property getter and setter. 
 * 
 * The final implementation should follow something close to CreateCoordinationContext
 * as per http://schemas.xmlsoap.org/ws/2004/10/wsoor.
 * Maybe refactor this interface to wsio.IWebServiceCoordCreateContextRequest.  
 */
public interface IAeCreateContextRequest
{
   /**
    * Returns the coordination type.
    */
   public String getCoordinationType();
   
   /**
    * Sets the coordination type.
    * @param aCoordinationType
    */
   public void setCoordinationType(String aCoordinationType);
   
   /**
    * Returns a property value.
    * @param aName name of the property.
    * @return value if the property is found or null otherwise.
    */
   public String getProperty(String aName);
   
   /**
    * Sets a custom property.
    * @param aName name of the property.
    * @param aValue value of the property.
    */
   public void setProperty(String aName, String aValue);
}
