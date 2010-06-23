//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/AeAttachmentUtil.java,v 1.2 2008/02/13 06:57:42 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeader;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.wsio.AeWebServiceAttachment;
import org.activebpel.wsio.IAeWebServiceAttachment;
import org.apache.axis.Message;

/**
 * Attachment utilities for common attachment related functions
 */
public class AeAttachmentUtil
{

   public static List soap2wsioAttachments(Message aMessage) throws Exception
   {
      return soap2wsioAttachments(aMessage, null);
   }
   /**
    * Extracts inbound SOAP Attachments and converts them to WSIO attachments
    * @param aMessage axis soap message
    * @param aPrincipalName principal name -may be null.
    * @return List of attachments
    * @see IAeWebServiceAttachment
    */
   public static List soap2wsioAttachments(Message aMessage, String aPrincipalName) throws Exception
   {
      //todo: only AeBpelHandler passes in the principalname from the message context. Check
      // if the following needs to pass in principal name as well:
      // AeSOAPInvoker::receiveAttachments(), AeActiveBpelAdminImpl::addAttachment() and AeActiveBPELAdminEndpointImpl::addAttachment()
      
      Iterator aAttachmentItr = aMessage.getAttachments();
      List attachments = null;

      // A soap message can have 0..n attachment parts
      while (aAttachmentItr.hasNext())
      {
         // Convert the Mime headers of the attachment part to a Map, add the map to the attachment
         AttachmentPart attachPart = (AttachmentPart)aAttachmentItr.next();
         Map mimeHeaderPairs = new HashMap();
         for (Iterator mimeItr = attachPart.getAllMimeHeaders(); mimeItr.hasNext();)
         {
            MimeHeader pair = (MimeHeader)mimeItr.next();
            mimeHeaderPairs.put(pair.getName(), pair.getValue());
         }
         // add principal if available
         if (AeUtil.notNullOrEmpty(aPrincipalName))
         {
            mimeHeaderPairs.put(IAeWebServiceAttachment.AE_ATTACHED_BY, aPrincipalName);
         }

         // create an attachment with headers and content
         AeWebServiceAttachment attachment = new AeWebServiceAttachment(attachPart.getDataHandler()
               .getInputStream(), mimeHeaderPairs);

         // Add the attachment to the attachment list of the message
         if ( attachments == null )
         {
            attachments = new LinkedList();
         }
         attachments.add(attachment);
      }
      return attachments;
   }
}
