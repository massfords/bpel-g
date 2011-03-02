//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeFaultTypeInfo.java,v 1.2 2006/10/17 21:23:08 tzhang Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel; 

import javax.xml.namespace.QName;

/**
 * Provides the type info for the fault
 */
public interface IAeFaultTypeInfo
{
   /**
    * Getter for the fault name
    * @return QName
    */
   public QName getFaultName();
   
   /**
    * returns the QName of the message if the data is a message type
    */
   public QName getMessageType();
   
   /**
    * returns the QName of the element data if the data is an element type
    */
   public QName getElementType();
   
   /**
    * returns the QName of the single part element if the data is a message
    * and the message has a single element part.
    */
   public QName getSinglePartElementType();

   /**
    * Returns true if the fault contains message data.
    */
   public boolean hasMessageData();

   /**
    * Returns true if the fault has element data.
    */
   public boolean hasElementData();
   
   /**
    * Returns true if the fault has element or message data 
    */
   public boolean hasData();
}
 