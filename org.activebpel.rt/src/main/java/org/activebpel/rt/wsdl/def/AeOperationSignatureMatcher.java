//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/AeOperationSignatureMatcher.java,v 1.1 2005/08/24 20:42:46 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def; 

import java.util.Iterator;
import java.util.List;

import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Document;

/**
 * Utility class that finds an operation given a WSDL for the port type and the
 * Document[]. This routine is designed for doc literal operations only. It will
 * match the data against the available operations to figure out what operation
 * is being invoked. 
 */
public class AeOperationSignatureMatcher extends AeMessageMatcher
{
   /** def that contains the port type - will have access to the messages either directly or through imports */
   private AeBPELExtendedWSDLDef mDef;
   /** name of the operation or null if not found */
   private Operation mOperation;
   /** qname of the port type */
   private QName mPortTypeName;
   /** optionally provided in the ctor */
   private String mOperationName;
   
   /**
    * Ctor
    * 
    * @param aPortTypeDef
    * @param aPortTypeName
    * @param aOperationName
    * @param aDataArray
    */
   public AeOperationSignatureMatcher(AeBPELExtendedWSDLDef aPortTypeDef, QName aPortTypeName, String aOperationName, Document[] aDataArray)
   {
      super(aDataArray);
      mDef = aPortTypeDef;
      mPortTypeName = aPortTypeName;
      mOperationName = aOperationName;
      
      matchSignature();
   }
   
   /**
    * walks the available operations to find a match by comparing the input message
    * parts with the data passed in the constructor
    */
   protected void matchSignature()
   {
      PortType portType = getDef().getPortType(getPortTypeQName());
      List operations = portType.getOperations();
      
      for (Iterator iter = operations.iterator(); getOperation() == null && iter.hasNext();)
      {
         Operation operation = (Operation) iter.next();
         if(isMatch(operation))
         {
            setOperation(operation);
         }
      }
   }
   
   /**
    * Determines if the operation is a match against our data
    * 
    * @param aOperation
    */
   protected boolean isMatch(Operation aOperation)
   {
      boolean match = false;

      // only check if the op has input
      if (aOperation.getInput() != null && isMatchForOperationName(aOperation))
      {
         Message inputMessage = aOperation.getInput().getMessage();
         
         match = isMatch(inputMessage);
      }
      return match;
   }

   /**
    * Convenience method that checks if the operation name matches the operation
    * name we're looking for. If the operation name wasn't set on the matcher then
    * we'll match against any operation provided that the data matches the message's
    * parts.
    * @param aOperation
    */
   protected boolean isMatchForOperationName(Operation aOperation)
   {
      return AeUtil.isNullOrEmpty(getOperationName()) || getOperationName().equals(aOperation.getName());
   }

   /**
    * Returns true if we were able to find a match
    */
   public boolean foundMatch()
   {
      return getOperation() != null;
   }
   
   /**
    * Getter for the operation name
    */
   public String getOperationName()
   {
      return mOperationName;
   }
   
   /**
    * Getter for the operation
    */
   public Operation getOperation()
   {
      return mOperation;
   }
   
   /**
    * Getter for the message qname
    */
   public QName getMessageName()
   {
      if (foundMatch())
      {
         return getMessage().getQName();
      }
      return null;
   }
   
   /**
    * Setter for the operation name
    * @param aOperation
    */
   protected void setOperation(Operation aOperation)
   {
      mOperation = aOperation;
      mOperationName = aOperation.getName();
   }
   
   /**
    * Getter for the def
    */
   protected AeBPELExtendedWSDLDef getDef()
   {
      return mDef;
   }
   
   /**
    * Getter for the port type qname
    */
   protected QName getPortTypeQName()
   {
      return mPortTypeName;
   }
}