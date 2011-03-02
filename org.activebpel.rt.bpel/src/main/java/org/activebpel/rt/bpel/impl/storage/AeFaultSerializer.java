// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/storage/AeFaultSerializer.java,v 1.5 2006/11/21 22:56:28 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.storage;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.AeMessageDataSerializer;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeImplStateNames;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.impl.fastdom.AeForeignNode;
import org.activebpel.rt.xml.schema.AeTypeMapping;

/**
 * Serializes a fault to an instance of {@link
 * org.activebpel.rt.bpel.impl.fastdom.AeFastElement} or {@link
 * org.activebpel.rt.bpel.impl.fastdom.AeFastDocument}.
 */
public class AeFaultSerializer implements IAeImplStateNames
{
   /** The fault to serialize. */
   private IAeFault mFault;

   /** The resulting serialization. */
   private AeFastElement mFaultElement;

   /** Serializer for message data. */
   private AeMessageDataSerializer mMessageDataSerializer;

   /** Type mapping to use to serialize simple types. */
   private AeTypeMapping mTypeMapping;

   /**
    * Serializes the specified fault to an instance of {@link
    * org.activebpel.rt.bpel.impl.fastdom.AeFastElement}.
    *
    * @param aFault
    */
   protected AeFastElement createFaultElement(IAeFault aFault) throws AeBusinessProcessException
   {
      AeFastElement result = new AeFastElement(STATE_FAULT);
      QName faultName = aFault.getFaultName();
      boolean hasMessageData = aFault.hasMessageData();
      boolean hasElementData = aFault.hasElementData();

      result.setAttribute(STATE_NAME          , faultName.getLocalPart());
      result.setAttribute(STATE_NAMESPACEURI  , faultName.getNamespaceURI());
      result.setAttribute(STATE_HASMESSAGEDATA, String.valueOf(hasMessageData));
      result.setAttribute(STATE_HASELEMENTDATA, String.valueOf(hasElementData));
      result.setAttribute(STATE_SUSPENDABLE, String.valueOf(aFault.isSuspendable()) );
      result.setAttribute(STATE_RETHROWABLE, String.valueOf(aFault.isRethrowable()) );

      IAeBpelObject source = aFault.getSource();
      if (source != null)
      {
         result.setAttribute(STATE_SOURCE, source.getLocationPath());
      }

      if (hasMessageData)
      {
         AeMessageDataSerializer serializer = getMessageDataSerializer();
         serializer.setMessageData(aFault.getMessageData());

         AeFastElement messageDataElement = serializer.getMessageDataElement();
         result.appendChild(messageDataElement);
      }
      
      if (hasElementData)
      {
         result.appendChild(new AeForeignNode(aFault.getElementData()));
      }

      return result;
   }

   /**
    * Returns the fault to serialize.
    */
   protected IAeFault getFault()
   {
      return mFault;
   }

   /**
    * Returns an instance of {@link
    * org.activebpel.rt.bpel.impl.fastdom.AeFastDocument} representing the
    * fault.
    */
   public AeFastDocument getFaultDocument() throws AeBusinessProcessException
   {
      return new AeFastDocument(getFaultElement());
   }

   /**
    * Returns an instance of {@link
    * org.activebpel.rt.bpel.impl.fastdom.AeFastElement} representing the fault.
    */
   public AeFastElement getFaultElement() throws AeBusinessProcessException
   {
      if (mFaultElement == null)
      {
         if (getFault() == null)
         {
            throw new IllegalStateException(AeMessages.getString("AeFaultSerializer.ERROR_0")); //$NON-NLS-1$
         }

         mFaultElement = createFaultElement(getFault());
      }

      return mFaultElement;
   }

   /**
    * Returns serializer for message data.
    */
   protected AeMessageDataSerializer getMessageDataSerializer()
   {
      if (mMessageDataSerializer == null)
      {
         if (getTypeMapping() == null)
         {
            throw new IllegalStateException(AeMessages.getString("AeFaultSerializer.ERROR_1")); //$NON-NLS-1$
         }

         mMessageDataSerializer = new AeMessageDataSerializer(getTypeMapping());
      }

      return mMessageDataSerializer;
   }

   /**
    * Returns the type mapping to use to serialize simple types.
    */
   protected AeTypeMapping getTypeMapping()
   {
      return mTypeMapping;
   }

   /**
    * Resets all output variables.
    */
   protected void reset()
   {
      mFaultElement = null;
      mMessageDataSerializer = null;
   }

   /**
    * Sets the fault to serialize.
    *
    * @param aFault
    */
   public void setFault(IAeFault aFault)
   {
      reset();

      mFault = aFault;
   }

   /**
    * Sets the type mapping to use to serialize simple types.
    *
    * @param aTypeMapping
    */
   public void setTypeMapping(AeTypeMapping aTypeMapping)
   {
      reset();

      mTypeMapping = aTypeMapping;
   }
}
