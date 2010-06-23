// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeDataConverter.java,v 1.5 2007/06/28 22:00:45 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.AeException;
import org.activebpel.rt.attachment.IAeAttachmentContainer;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessageDataFactory;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.wsio.AeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceMessageData;

/**
 * Utility class for converting between wsio data and our standard data container. The wsio packaging provides
 * a standalone set of interfaces and support classes for dispatching messages to the engine and for invoking
 * external web services. In order to simplify the packaging, the wsio project does not have ties to the rest
 * of the activebpel codebase. One side effect is that we're left with two different interfaces
 */
public class AeDataConverter
{
   private static IAeAttachmentManager sAttachmentManager = null;

   /**
    * Sets the attachment manager to use to convert attachments that may be
    * associated with incoming or outgoing message data.
    *
    * @param aAttachmentManager
    */
   public static void setAttachmentManager(IAeAttachmentManager aAttachmentManager)
   {
      sAttachmentManager = aAttachmentManager;
   }

   /**
    * Converts a message data object to a WSIO message data. Returns <code>null</code> if input is
    * <code>null</code>. Does not convert attachments.
    * @param aData
    */
   public static IAeWebServiceMessageData convert(IAeMessageData aData) throws AeBusinessProcessException
   {
      AeWebServiceMessageData wsMsg;

      if ( aData == null )
      {
         wsMsg = null;
      }
      else
      {
         wsMsg = new AeWebServiceMessageData(aData.getMessageType());

         for (Iterator iter = aData.getPartNames(); iter.hasNext();)
         {
            String partName = (String)iter.next();
            wsMsg.setData(partName, aData.getData(partName));
         }

         if (aData.hasAttachments())
            wsMsg.setAttachments(sAttachmentManager.bpel2wsio(aData.getAttachmentContainer()));
      }

      return wsMsg;
   }

   /**
    * Converts the message data for the given fault, silently consuming any
    * exception that may arise.
    *
    * @param aFault
    */
   public static IAeWebServiceMessageData convertFaultDataNoException(IAeFault aFault)
   {
      try
      {
         return convert(aFault.getMessageData());
      }
      catch (AeBusinessProcessException e)
      {
         // TODO (JB) Try AeDataConvert.convert() again without the attachments.
         AeException.logError(e);
         return null;
      }
   }

   /**
    * Converts a WSIO message to a message data. Returns <code>null</code> if input is <code>null</code>.
    * @param aData
    */
   public static IAeMessageData convert(IAeWebServiceMessageData aData)  throws AeBusinessProcessException
   {
      IAeMessageData bpelMsg = null;

      if ( aData != null )
      {
         bpelMsg = AeMessageDataFactory.instance().createMessageData(aData.getMessageType(),
               aData.getMessageData());

         List attachments = aData.getAttachments();
         if ((attachments != null) && !attachments.isEmpty())
         {
            IAeAttachmentContainer container = sAttachmentManager.wsio2bpel(attachments);
            bpelMsg.setAttachmentContainer(container);
         }
      }
      return bpelMsg;
   }
}
