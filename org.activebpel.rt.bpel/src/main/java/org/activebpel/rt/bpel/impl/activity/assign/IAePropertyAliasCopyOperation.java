//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/IAePropertyAliasCopyOperation.java,v 1.2 2006/06/26 16:50:36 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;

/**
 * Interface for copy operation components that use property aliases to select the data for the
 * source or target. 
 */
public interface IAePropertyAliasCopyOperation
{
   /**
    * Gets the property alias for use in selecting the property
    * @throws AeBusinessProcessException
    */
   public IAePropertyAlias getPropertyAlias() throws AeBusinessProcessException;
   
   /**
    * Gets the data to use for the property alias query.
    * @param aPropAlias
    * @throws AeBusinessProcessException 
    */
   public Object getDataForQueryContext(IAePropertyAlias aPropAlias) throws AeBusinessProcessException;
}
 