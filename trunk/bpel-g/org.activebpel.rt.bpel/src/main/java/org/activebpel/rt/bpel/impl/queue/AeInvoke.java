// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/queue/AeInvoke.java,v 1.26 2008/02/17 21:37:11 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.queue;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeInvokeActivity;
import org.activebpel.rt.bpel.IAePartnerLink;
import org.activebpel.rt.bpel.impl.AeDataConverter;
import org.activebpel.rt.bpel.impl.AePartnerLinkOpImplKey;
import org.activebpel.rt.bpel.impl.IAeInvokeInternal;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.wsio.IAeWebServiceMessageData;
import org.w3c.dom.Document;

/**
 * Represents an in memory message queue object.
 */
public class AeInvoke extends AeAbstractQueuedObject implements IAeInvokeInternal
{
   /**
    * Flag indicates if this invoke is a one-way invoke and not expecting a
    * response
    */
   private boolean mOneWay;
   /** Input message data for invoke. */
   private IAeWebServiceMessageData mInputMessageData;
   /** Output message data for invoke. */
   private IAeMessageData mOutputMessageData;
   /** Fault for invoke. */
   private IAeFault mFault;
   /** Message receiver location path */
   private String mMessageReceiverPath;
   /**
    * location path for the activity that is waiting for the invoke's response
    */
   private String mLocationPath;
   /** The partner endpoint reference */
   private IAeEndpointReference mPartnerReference;
   /** The my endpoint reference */
   private IAeEndpointReference mMyReference;
   /** Receives the response for the invoke */
   private IAeInvokeActivity mInvokeActivity;
   /** process name */
   private QName mProcessName;
   /** Process id is needed to differentiate between queued objects */
   private long mProcessId;
   /** Location id for the activity that is waiting for the response. */
   private int mLocationId;
   /** The custom invoker uri. */
   private String mInvokeHandler;
   /** Business process properties. */
   private Map mProcessProperties;
   /**
    * The partner link location (convenient access so that the plink id doesn't
    * always need to be de-referenced).
    */
   private String mPartnerLinkLocation;
   /** Transmission id */
   private long mTransmissionId;
   /** The port type */
   private QName mPortType;
   /** The process initiator */
   private String mProcessInitiator;

   /**
    * Creates an invoke.
    * @param aProcessId
    * @param aProcessName
    * @param aPartnerLink
    * @param aPartnerLinkOpImplKey
    * @param aInputMessageData
    * @param aInvokeActivity
    * @param aProcessProperties
    * @throws AeBusinessProcessException
    */
   public AeInvoke(long aProcessId, QName aProcessName, IAePartnerLink aPartnerLink,
         AePartnerLinkOpImplKey aPartnerLinkOpImplKey, IAeMessageData aInputMessageData,
         IAeInvokeActivity aInvokeActivity, Map aProcessProperties) throws AeBusinessProcessException
   {
      super(aPartnerLinkOpImplKey);
      setProcessId(aProcessId);
      setPartnerLinkLocation(aPartnerLink.getLocationPath());
      mProcessName = aProcessName;
      // Note: clone the partner ref, my ref, and process properties.  We do
      // this in case any of those things change in the process while the invoke
      // is executing/queued (parallel foreach, flows, etc).
      mPartnerReference = (IAeEndpointReference) aPartnerLink.getPartnerReference().clone();
      mMyReference = (IAeEndpointReference) aPartnerLink.getMyReference().clone();
      // no clone required for the message data since it was already cloned by
      // the message data producer.
      setInputMessageData(aInputMessageData);
      mMessageReceiverPath = aInvokeActivity.getLocationPath();
      mLocationPath = aInvokeActivity.getLocationPath();
      mProcessProperties = new HashMap(aProcessProperties);
      mLocationId = aInvokeActivity.getLocationId();
      mTransmissionId = aInvokeActivity.getTransmissionId();
      setInvokeActivity(aInvokeActivity);
      setPortType(aPartnerLink.getDefinition().getPartnerRolePortType());
   }

   /**
    * Getter for the process name for this invoke.
    */
   public QName getProcessName()
   {
      return mProcessName;
   }

   /**
    * Overrides method to location id of invoke activity.
    * @see org.activebpel.wsio.invoke.IAeInvoke#getLocationId()
    */
   public int getLocationId()
   {
      return mLocationId;
   }

   /**
    * Returns true if the object is of the same type and has the same process id
    * and location path.
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aObject)
   {
      if (aObject instanceof AeInvoke)
      {
         AeInvoke other = (AeInvoke) aObject;
         return other.getProcessId() == getProcessId() && AeUtil.compareObjects(getLocationPath(), other.getLocationPath());
      }
      return false;
   }

   /**
    * Gets the location path for the message receiver.
    */
   public String getLocationPath()
   {
      return mLocationPath;
   }

   /**
    * @see org.activebpel.wsio.invoke.IAeInvoke#getInputMessageData()
    */
   public IAeWebServiceMessageData getInputMessageData()
   {
      return mInputMessageData;
   }

   /**
    * @return String The message receiver location path (may be null if no
    *                message yet).
    */
   public String getMessageReceiverPath()
   {
      return mMessageReceiverPath;
   }

   /**
    * Sets the message data.
    */
   public void setInputMessageData(IAeMessageData aInputMessageData) throws AeBusinessProcessException
   {
      mInputMessageData = AeDataConverter.convert(aInputMessageData);
   }

   /**
    * @return The fault associated with the invokes response, null if none.
    */
   public IAeFault getFault()
   {
      return mFault;
   }

   /**
    * @return The output message data associated with invokes response,
    *         null if none.
    */
   public IAeMessageData getOutputMessageData()
   {
      return mOutputMessageData;
   }

   /**
    * @param aFault The fault to be associated with the invokes response.
    */
   public void setFault(IAeFault aFault)
   {
      mFault = aFault;
   }

   /**
    * @param aData The output message to be associated with the invokes
    *        response.
    */
   public void setOutputMessageData(IAeMessageData aData)
   {
      mOutputMessageData = aData;
   }

   /**
    * Accessor for the partner reference endpoint.
    */
   public IAeEndpointReference getPartnerReference()
   {
      return mPartnerReference;
   }

   /**
    * @see org.activebpel.wsio.invoke.IAeInvoke#getPartnerEndpointReferenceString()
    */
   public String getPartnerEndpointReferenceString()
   {
      Document eprDoc = getPartnerReference().toDocument();
      return AeXMLParserBase.documentToString(eprDoc, true);
   }

   /**
    * Accessor for my reference endpoint.
    */
   public IAeEndpointReference getMyReference()
   {
      return mMyReference;
   }

   /**
    * @see org.activebpel.wsio.invoke.IAeInvoke#getPartnerEndpointReferenceString()
    */
   public String getMyEndpointReferenceString()
   {
      Document eprDoc = getMyReference().toDocument();
      return AeXMLParserBase.documentToString(eprDoc, true);
   }

   /**
    * Returns true if this invoke is a one way invoke and not expecting a
    * response
    */
   public boolean isOneWay()
   {
      return mOneWay;
   }

   /**
    * Setter for the one way flag
    * @param aB
    */
   public void setOneWay(boolean aB)
   {
      mOneWay = aB;
   }

   /**
    * Setter for the response receiver. The invocation layer is responsible for
    * installing its own response receiver which will dispatch the response from
    * the invocation to the engine's appropriate queue methods.
    * @param aInvokeActivity
    */
   public void setInvokeActivity(IAeInvokeActivity aInvokeActivity)
   {
      mInvokeActivity = aInvokeActivity;
   }

   /**
    * Overrides method to set invoke activity to <code>null</code>.
    * @see org.activebpel.rt.bpel.impl.IAeInvokeInternal#dereferenceInvokeActivity()
    */
   public void dereferenceInvokeActivity()
   {
      setInvokeActivity(null);
   }

   /**
    * Getter for the invoke activity. This will be null if the invocation
    * layer hasn't installed it yet.
    */
   public IAeInvokeActivity getInvokeActivity()
   {
      return mInvokeActivity;
   }

   /**
    * Setter for the process id
    *
    * @param processId
    */
   public void setProcessId(long processId)
   {
      mProcessId = processId;
   }

   /**
    * @see org.activebpel.wsio.invoke.IAeInvoke#getProcessId()
    */
   public long getProcessId()
   {
      return mProcessId;
   }

   /**
    * @see org.activebpel.wsio.invoke.IAeInvoke#getCustomInvokerUri()
    * @deprecated
    */
   public String getCustomInvokerUri()
   {
      return getInvokeHandler();
   }

   /**
    * @see org.activebpel.wsio.invoke.IAeInvoke#getInvokeHandler()
    */
   public String getInvokeHandler()
   {
      return mInvokeHandler;
   }

   /**
    * @see org.activebpel.wsio.invoke.IAeInvoke#setInvokeHandler(java.lang.String)
    */
   public void setInvokeHandler(String aCustomInvokerUri)
   {
      mInvokeHandler = aCustomInvokerUri;
   }

   /**
    * @return Returns the processProperties.
    */
   public Map getBusinessProcessProperties()
   {
      return mProcessProperties;
   }

   /**
    * @see org.activebpel.wsio.invoke.IAeInvoke#getTransmissionId()
    */
   public long getTransmissionId()
   {
      return mTransmissionId;
      }

   /**
    * Overrides method to set the transmission id in the response receiver.
    * @see org.activebpel.wsio.invoke.IAeInvoke#setTransmissionId(long)
    */
   public void setTransmissionId(long aTransmissionId)
   {
      // First check if local value of tx id is same as invoke Activity's tx id.
      // if so, we are using the same instance/iteration, hence pass thru the
      // txId and set it on the invokeActivity.
      //
      // If there is a mismatch, then there is an overlap of tx id (i.e Invoke
      // activity has moved onto the next iteration).
      // In this case, do not set tx id - just no-op and return.

      if ( isCurrent() )
      {
         // update the local copy
         mTransmissionId = aTransmissionId;
         // update the value in the AeActivityInvoke
         getInvokeActivity().setTransmissionId(aTransmissionId);
      }
      else if (getInvokeActivity() != null)
      {
         AeException.logWarning( AeMessages.format("AeInvoke.NOT_CURRENT_INVOKE_ID_INSTANCE",  //$NON-NLS-1$
                new Object[] {
                   String.valueOf(getTransmissionId()),
                   String.valueOf(getInvokeActivity().getTransmissionId())
                })
           );
      }
      else
      {
         AeException.logWarning (AeMessages.getString("AeInvoke.NULL_RESPONSE_RECEIVER")); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.wsio.invoke.IAeInvoke#getPartnerLink()
    */
   public String getPartnerLink()
   {
      return getPartnerLinkLocation();
   }

   /**
    * @return Returns the partnerLinkLocation.
    */
   protected String getPartnerLinkLocation()
   {
      return mPartnerLinkLocation;
   }

   /**
    * @param aPartnerLinkLocation The partnerLinkLocation to set.
    */
   protected void setPartnerLinkLocation(String aPartnerLinkLocation)
   {
      mPartnerLinkLocation = aPartnerLinkLocation;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeInvokeInternal#isCurrent()
    */
   public boolean isCurrent()
   {
      boolean rVal = getInvokeActivity() != null && getTransmissionId() == getInvokeActivity().getTransmissionId();
      return rVal;
   }

   /**
    * @see org.activebpel.wsio.invoke.IAeInvoke#getPortType()
    */
   public QName getPortType()
   {
      return mPortType;
   }

   /**
    * Setter for the port type
    * @param aPortType
    */
   protected void setPortType(QName aPortType)
   {
      mPortType = aPortType;
   }

   /**
    * @return the process Initiator
    */
   public String getProcessInitiator()
   {
      return mProcessInitiator;
   }

   /**
    * @param aProcessInitiator the process initiator to set
    */
   public void setProcessInitiator(String aProcessInitiator)
   {
      mProcessInitiator = aProcessInitiator;
   }
}
