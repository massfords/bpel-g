//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/IAeVariableDataWrapper.java,v 1.2 2006/06/26 16:50:36 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign; 

import org.activebpel.rt.bpel.impl.AeBpelException;
import org.exolab.castor.xml.schema.XMLType;

/**
 * Interface used to wrap a variable that is being assigned to. 
 */
public interface IAeVariableDataWrapper
{
   /**
    * Sets the new value for the message part
    * 
    * @param aValue
    */
   public void setValue(Object aValue) throws AeBpelException;
   
   /**
    * Gets the value of the variable, initializing the variable to an acceptable
    * default if it was null. Simple types will default to an empty string. Complex
    * types and Elements will default to an empty element with the appropriate name
    * and namespace. 
    * @return Element, java primitive object, or one of our schema type objects (duration/deadline)
    */
   public Object getValue() throws AeBpelException;
   
   /**
    * Getter for the XMLType
    * 
    * @throws AeBpelException
    */
   public XMLType getXMLType() throws AeBpelException;
}
 