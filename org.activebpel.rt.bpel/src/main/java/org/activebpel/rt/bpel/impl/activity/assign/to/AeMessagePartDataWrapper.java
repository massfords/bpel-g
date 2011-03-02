//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeMessagePartDataWrapper.java,v 1.2 2006/12/14 22:59:39 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.to; 

import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.message.AeMessagePartTypeInfo;
import org.exolab.castor.xml.schema.XMLType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Encapsulates setting data on a complex message part (either complexType or element) 
 */
public class AeMessagePartDataWrapper extends AeVariableBaseDataWrapper
{
   /** wsdl message part info */
   private AeMessagePartTypeInfo mPart;

   /**
    * Ctor accepts the message data and part
    * 
    * @param aVariable
    * @param aPart
    */
   public AeMessagePartDataWrapper(IAeVariable aVariable, AeMessagePartTypeInfo aPart)
   {
      super(aVariable);
      setPart(aPart);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.to.AeVariableBaseDataWrapper#getValue()
    */
   public Object getValue() throws AeBpelException
   {
      Object data = getVariable().getMessageData().getData(getPart().getName());
      if (data instanceof Document)
      {
         return ((Document)data).getDocumentElement();
      }
      else
      {
         return data;
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeVariableDataWrapper#setValue(java.lang.Object)
    */
   public void setValue(Object aValue) throws AeBpelException
   {
      Object value = cloneValue(aValue);
         
      if (value instanceof Element)
      {
         value = ((Element)value).getOwnerDocument();
      }
      
      getVariable().getMessageData().setData(getPart().getName(), value);
   }

   /**
    * @param aPart The part info to set.
    */
   protected void setPart(AeMessagePartTypeInfo aPart)
   {
      mPart = aPart;
   }
   
   /**
    * Getter for the part info
    */
   protected AeMessagePartTypeInfo getPart()
   {
      return mPart;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeVariableDataWrapper#getXMLType()
    */
   public XMLType getXMLType() throws AeBpelException
   {
      return getVariable().getDefinition().getPartType(getPart().getName());
   }
}