// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/storage/AeFaultDeserializer.java,v 1.6 2006/11/21 22:56:38 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.storage;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.impl.AeMessageDataDeserializer;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeImplStateNames;
import org.activebpel.rt.bpel.impl.activity.support.AeFault;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;

/**
 * Deserializes a fault from its serialization.
 */
public class AeFaultDeserializer implements IAeImplStateNames
{
   /** The fault once deserialized. */
   private IAeFault mFault;

   /** The fault's serialization. */
   private Element mFaultElement;

   /** The fault's process: used to locate the fault's source object. */
   private IAeBusinessProcess mProcess;
   
   /** needed for legacy check of deserialization of old faults */
   private static QName FORCED_TERMINATION = new QName(IAeBPELConstants.BPWS_NAMESPACE_URI, "forcedTermination"); //$NON-NLS-1$

   /**
    * Creates an instance of {@link org.activebpel.rt.bpel.IAeFault} from its
    * serialization.
    * 
    * @param aFaultElement
    * @throws AeBusinessProcessException
    */
   protected IAeFault createFault(Element aFaultElement) throws AeBusinessProcessException
   {
      String localPart = aFaultElement.getAttribute(STATE_NAME);
      String namespace = aFaultElement.getAttribute(STATE_NAMESPACEURI);
      QName faultName = new QName(namespace, localPart);
      
      AeFault result;

      // If there's message data, then create the fault object from scratch
      // with the message data.
      String hasMessageData = aFaultElement.getAttribute(STATE_HASMESSAGEDATA);
      String hasElementData = aFaultElement.getAttribute(STATE_HASELEMENTDATA);
      if ("true".equals(hasMessageData)) //$NON-NLS-1$
      {
         AeMessageDataDeserializer deserializer = new AeMessageDataDeserializer();
         deserializer.setMessageDataElement(AeXmlUtil.getFirstSubElement(aFaultElement));

         IAeMessageData messageData = deserializer.getMessageData();
         result = new AeFault(faultName, messageData);
      }
      else if ("true".equals(hasElementData)) //$NON-NLS-1$
      {
         result = new AeFault(faultName, AeXmlUtil.getFirstSubElement(aFaultElement));
      }
      // Otherwise, try getting the fault from the fault factory.
      else
      {
         result = new AeFault(faultName, (IAeMessageData)null);
      }
      
      result.setSuspendable( readBooleanAttribute(aFaultElement, STATE_SUSPENDABLE, true) );

      // FYI - this check for forcedTermination is for backwards compatability since the flag for whether a fault
      //       was rethrowable was not always stored with the fault's state.
      boolean faultIsForcedTermination = faultName.equals(FORCED_TERMINATION);
      result.setRethrowable( readBooleanAttribute(aFaultElement, STATE_RETHROWABLE, !faultIsForcedTermination) );

      String sourceLocationPath = aFaultElement.getAttribute(STATE_SOURCE);
      if (!AeUtil.isNullOrEmpty(sourceLocationPath))
      {
         if (getProcess() == null)
         {
            throw new IllegalStateException(AeMessages.getString("AeFaultDeserializer.ERROR_0")); //$NON-NLS-1$
         }

         IAeBpelObject source = getProcess().findBpelObject(sourceLocationPath);
         result.setSource(source);
      }

      return result;
   }

   /**
    * Reads the boolean attribute from the element, defaulting to the value provided if not 
    * available from the element.
    * @param aFaultElement
    * @param aAttributeName
    * @param aDefaultValue
    */
   protected boolean readBooleanAttribute(Element aFaultElement, String aAttributeName, boolean aDefaultValue)
   {
      String flag = aFaultElement.getAttribute( aAttributeName );

      // if this fault was serialized before this feature was available,
      // then use the default value
      if( AeUtil.isNullOrEmpty(flag) )
      {
         return aDefaultValue;
      }
      else
      {
         return AeUtil.toBoolean( flag );
      }
   }

   /**
    * Returns the fault deserialized from the serialization that was set with
    * {@link #setFaultElement}.
    */
   public IAeFault getFault() throws AeBusinessProcessException
   {
      if (mFault == null)
      {
         if (getFaultElement() == null)
         {
            throw new IllegalStateException(AeMessages.getString("AeFaultDeserializer.ERROR_1")); //$NON-NLS-1$
         }

         mFault = createFault(getFaultElement());
      }

      return mFault;
   }

   /**
    * Returns the fault serialization to use.
    */
   protected Element getFaultElement()
   {
      return mFaultElement;
   }

   /**
    * Returns the process to use to locate fault source objects.
    */
   protected IAeBusinessProcess getProcess()
   {
      return mProcess;
   }

   /**
    * Resets all output variables.
    */
   protected void reset()
   {
      mFault = null;
   }

   /**
    * Sets the fault serialization to use.
    *
    * @param aFaultElement
    */
   public void setFaultElement(Element aFaultElement)
   {
      reset();

      mFaultElement = aFaultElement;
   }

   /**
    * Sets the process to use to locate fault source objects.
    * 
    * @param aProcess
    */
   public void setProcess(IAeBusinessProcess aProcess)
   {
      reset();

      mProcess = aProcess;
   }
}
