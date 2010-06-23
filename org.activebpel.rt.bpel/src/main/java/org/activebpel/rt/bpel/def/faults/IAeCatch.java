//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/faults/IAeCatch.java,v 1.2 2006/10/06 21:32:45 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.faults; 

import javax.xml.namespace.QName;

/**
 * Provides interface for faultHandlers. This interface is used to expose 
 * the info necessary to see if a fault matches to a catch element.
 */
// fixme (MF) repackage to rt.bpel.faults 
public interface IAeCatch
{
   /**
    * Returns true if the catch defines a variable
    */
   public boolean hasFaultVariable();
   
   /**
    * Returns the QName of the fault element or null if no variable is defined or if its catching a message
    */
   public QName getFaultElementName();
   
   /**
    * Returns the QName of the fault message or null if no variable is defined or if its catching an element
    */
   public QName getFaultMessageType();
   
   /**
    * Returns the QName of the fault being caught or null if not provided
    */
   public QName getFaultName();
}
 