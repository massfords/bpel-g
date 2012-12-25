//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/coord/IAeCoordinationContext.java,v 1.4 2006/05/24 23:07:01 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.coord;

import java.io.Serializable;

/**
 * Interface for a WS-Coordination context. 
 * 
 * Note: This is an internal implementation tailored to be used with requirement 111
 * i.e. the interface provides a simple property getter and setter. 
 *  * 
 * The final implementation should follow something close to CoordinationContext
 * as per http://schemas.xmlsoap.org/ws/2004/10/wsoor.
 *  
 */
public interface IAeCoordinationContext extends Serializable
{   
   /**
    * Returns the coordination identifier assigned by the activation service.
    * @return coordination id
    */
   public String getIdentifier();
   
   /**
    * Returns the coordination type. E.g. activebpel:coord:SubProcess, wsba:AtomicOutcome, wsba:MixedOutcome.
    */
   public String getCoordinationType();   
   
   /**
    * Returns a context property.
    * @param aName name of property.
    * @return property value.
    */
   public String getProperty(String aName);
   
   /**
    * Sets a property value.
    * @param aName property name.
    * @param aValue property value.
    */
   public void setProperty(String aName, String aValue);
}
