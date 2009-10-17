//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/invoke/AeInvokeContext.java,v 1.2 2008/01/14 21:27:46 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.invoke;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.wsdl.BindingOperation;
import javax.wsdl.Operation;
import javax.wsdl.Service;

import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.wsio.invoke.AeInvokeResponse;
import org.activebpel.wsio.invoke.IAeInvoke;

/**
 * Context information necessary to execute and invoke.
 */
public class AeInvokeContext
{
   /** IAeInvoke object */
   private IAeInvoke mInvoke;
   /** Invoke response object. */
   private AeInvokeResponse mResponse;
   /** The invoke service. */
   private Service mService;
   /** The invoke operation. */
   private Operation mOperation;
   /** Message parts that are destined for the header, they should not be added to the body */
   private Collection mInputHeaderParts;
   /** Message parts that should be extracted from the response soap headers */
   private Collection mOutputHeaderParts;
   /** Contains binding info for the operation being invoked */
   private BindingOperation mBindingOperation;
   /** Target Endpoint for the invoke */
   private IAeEndpointReference mEndpoint;
   /** ReplyTo Endpoint for the invoke */
   private IAeEndpointReference mReplyTo;
   /** resolved policies */
   private List mPolicyList;
   /** policy driven call properties */
   private Map mCallProperties;
   /** the address handling type */
   private AeAddressHandlingType mAddressType;
   /** the soapAction from the binding op */
   private String mSoapAction;
   
   /**
    * @return the soapAction
    */
   public String getSoapAction()
   {
      return mSoapAction;
   }

   /**
    * @param aSoapAction the soapAction to set
    */
   public void setSoapAction(String aSoapAction)
   {
      mSoapAction = aSoapAction;
   }

   /**
    * Default Constructor.
    */
   public AeInvokeContext()
   {
   }

   /**
    * @return Returns the invoke.
    */
   public IAeInvoke getInvoke()
   {
      return mInvoke;
   }
   
   /**
    * @param aInvoke The invoke to set.
    */
   public void setInvoke(IAeInvoke aInvoke)
   {
      mInvoke = aInvoke;
   }
   
   /**
    * @return Returns the operation.
    */
   public Operation getOperation()
   {
      return mOperation;
   }

   /**
    * @param aOperation The operation to set.
    */
   public void setOperation(Operation aOperation)
   {
      mOperation = aOperation;
   }
   
   /**
    * @return Returns the response.
    */
   public AeInvokeResponse getResponse()
   {
      return mResponse;
   }

   /**
    * @param aResponse The response to set.
    */
   public void setResponse(AeInvokeResponse aResponse)
   {
      mResponse = aResponse;
   }

   /**
    * @return Returns the inputHeaderParts.
    */
   public Collection getInputHeaderParts()
   {
      if (mInputHeaderParts == null)
      {
         mInputHeaderParts = Collections.EMPTY_SET;
      }
      return mInputHeaderParts;
   }

   /**
    * @param aInputHeaderParts The inputHeaderParts to set.
    */
   public void setInputHeaderParts(Collection aInputHeaderParts)
   {
      mInputHeaderParts = aInputHeaderParts.isEmpty() ? null : new HashSet(aInputHeaderParts);
   }

   /**
    * @return Returns the outputHeaderParts.
    */
   public Collection getOutputHeaderParts()
   {
      if (mOutputHeaderParts == null)
      {
         mOutputHeaderParts = Collections.EMPTY_SET;
      }
      return mOutputHeaderParts;
   }

   /**
    * @param aOutputHeaderParts The outputHeaderParts to set.
    */
   public void setOutputHeaderParts(Collection aOutputHeaderParts)
   {
      mOutputHeaderParts = aOutputHeaderParts.isEmpty() ? null : new HashSet(aOutputHeaderParts);
   }

   /**
    * @return Returns the bindingOperation.
    */
   public BindingOperation getBindingOperation()
   {
      return mBindingOperation;
   }

   /**
    * @param aBindingOperation The bindingOperation to set.
    */
   public void setBindingOperation(BindingOperation aBindingOperation)
   {
      mBindingOperation = aBindingOperation;
   }
   
   /**
    * Returns true if the part name is supposed to go in the soap header of the request.
    * @param aPartName
    */
   public boolean isInputHeader(String aPartName)
   {
      return getInputHeaderParts().contains(aPartName);
   }
   
   /**
    * Returns true if the part name is supposed to be in the soap header of the response
    * @param aPartName
    */
   public boolean isOutputHeader(String aPartName)
   {
      return getOutputHeaderParts().contains(aPartName);
   }

   /**
    * @return the service
    */
   public Service getService()
   {
      return mService;
   }

   /**
    * @param aService the service to set
    */
   public void setService(Service aService)
   {
      mService = aService;
   }

   /**
    * @return the endpoint
    */
   public IAeEndpointReference getEndpoint()
   {
      return mEndpoint;
   }

   /**
    * @param aEndpoint the endpoint to set
    */
   public void setEndpoint(IAeEndpointReference aEndpoint)
   {
      mEndpoint = aEndpoint;
   }

   /**
    * @return the policyList
    */
   public List getPolicyList()
   {
      return mPolicyList;
   }

   /**
    * @param aPolicyList the policyList to set
    */
   public void setPolicyList(List aPolicyList)
   {
      mPolicyList = aPolicyList;
   }

   /**
    * @return the replyTo
    */
   public IAeEndpointReference getReplyTo()
   {
      return mReplyTo;
   }

   /**
    * @param aReplyTo the replyTo to set
    */
   public void setReplyTo(IAeEndpointReference aReplyTo)
   {
      mReplyTo = aReplyTo;
   }

   /**
    * @return the addressType
    */
   public AeAddressHandlingType getAddressType()
   {
      return mAddressType;
   }

   /**
    * @param aAddressType the addressType to set
    */
   public void setAddressType(AeAddressHandlingType aAddressType)
   {
      mAddressType = aAddressType;
   }

   /**
    * @return the callProperties
    */
   public Map getCallProperties()
   {
      return mCallProperties;
   }

   /**
    * @param aCallProperties the callProperties to set
    */
   public void setCallProperties(Map aCallProperties)
   {
      mCallProperties = aCallProperties;
   }
}
