//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeVariableView.java,v 1.1 2007/11/01 18:23:52 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.impl.AeUninitializedVariableException;
import org.activebpel.rt.message.IAeMessageData;
import org.w3c.dom.Element;

/**
 * Describes a read-only interface used for reading business process variable data.
 * These Business Process variables are associated with either messages or
 * standard schema described xml data.
 */
public interface IAeVariableView
{
   /**
    * Get the name of the variable.
    */
   public String getName();
   
   /**
    * Get the message data associated with the variable or throws an exception if
    * the variable has not been initialized.
    */
   public IAeMessageData getMessageData() throws AeUninitializedVariableException;
   
   /**
    * Returns true if the variable has data associated with it.
    */
   public boolean hasMessageData();

   /**
    * Get the data if the variable is specified as type. Returns null if not type.
    * @throws AeUninitializedVariableException 
    */
   public Object getTypeData() throws AeUninitializedVariableException;

   /**
    * Get the data if the variable is specified as element. Returns null if not element.
    */
   public Element getElementData() throws AeUninitializedVariableException;;

   /**
    * Accessor method to obtain messageType of this object. Returns
    * null if not a message.
    */
   public QName getMessageType();
   
   /**
    * Returns true if the variable is an Element variable.
    */
   public boolean isElement();

   /**
    * Returns true if the variable is a Message variable.
    */
   public boolean isMessageType();

   /**
    * Returns true if the variable is a Type variable (complex or simple type).
    */
   public boolean isType();

   /**
    * Returns the element decaration associated with this variable, null if
    * not an element.
    */
   public QName getElement();

   /**
    * Returns the type decaration associated with this variable, null if
    * not an type.
    */
   public QName getType();

   /**
    * Returns <code>true</code> if and only if the variable has data.
    */
   public boolean hasData();   
   
}
