// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeMessageReceiverFilterManager.java,v 1.5 2006/06/26 16:50:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.util.AeUtil;

/**
 * Provides filtering capability for the in-memory queue manager 
 * queued receivers listing.
 */
public class AeMessageReceiverFilterManager
{

   /** Default comparator for sorting the list of message receivers. */
   private static final AeMessageReceiverComparator SORTER = 
      new AeMessageReceiverComparator();

   /**
    * Returns a filtered list of message receivers.
    * @param aFilter Determines the selection criteria.
    * @param aMessageReceivers All of the available receivers on the queue.
    * @return The filtered results.
    */
   public static AeMessageReceiverListResult filter( 
                       AeMessageReceiverFilter aFilter, Collection aMessageReceivers )
   {
      List matches = new ArrayList();
      int totalRows = 0;
      
      if( aMessageReceivers != null && !aMessageReceivers.isEmpty() )
      {
         AeMessageReceiver[] recs = (AeMessageReceiver[]) aMessageReceivers.toArray( 
            new AeMessageReceiver[aMessageReceivers.size()]);
            
         for( int i = 0; i < recs.length; i++ )
         {
            AeMessageReceiver receiver = recs[i];
            
            if( accepts(aFilter, receiver) ) 
            {
               totalRows++;
               if( aFilter.isWithinRange(totalRows) )
               {
                  matches.add( receiver );                     
               }
            }
         }
      }
      
      if( !matches.isEmpty() )
      {
         sort( matches );
      }
      
      return new AeMessageReceiverListResult(totalRows,matches);
   }
   
   /**
    * Returns true if the message receiver meets the filter criteria.
    * @param aFilter The selection criteria.
    * @param aReceiver A queued message receiver.
    */
   protected static boolean accepts( AeMessageReceiverFilter aFilter, AeMessageReceiver aReceiver )
   {
      return isPIDMatch( aFilter, aReceiver ) &&
             isPartnerLinkNameMatch( aFilter, aReceiver ) &&
             isPortTypeMatch( aFilter, aReceiver ) &&
             isOperationMatch( aFilter, aReceiver );
   }
   
   /**
    * Returns true if there is no process id specified in the filter or
    * if the process id in the filter mathes the receive's process id.
    * @param aFilter The selection criteria.
    * @param aReceiver A queued message receiver.
    */
   static boolean isPIDMatch( AeMessageReceiverFilter aFilter, AeMessageReceiver aReceiver )
   {
      if( !aFilter.isNullProcessId() )
      {
         return aFilter.getProcessId() == aReceiver.getProcessId();
      }
      else
      {
         return true;
      }
   }
   
   /**
    * Returns true if there is no partner link type name specified in the filter or
    * if the partner link type name in the filter mathes the receive's 
    * partner link type name.
    * @param aFilter The selection criteria.
    * @param aReceiver A queued message receiver.
    */
   protected static boolean isPartnerLinkNameMatch( AeMessageReceiverFilter aFilter, AeMessageReceiver aReceiver )
   {
      // TODO expand the matching to allow the user to specify a full path to the partner link
      if( !AeUtil.isNullOrEmpty( aFilter.getPartnerLinkName() ) )
      {
         return aFilter.getPartnerLinkName().equals( aReceiver.getPartnerLinkOperationKey().getPartnerLinkName() );
      }
      else
      {
         return true;
      }
   }
   
   /**
    * Returns true if there is no port type qname specified in the filter or
    * if the port type qname in the filter mathes the receive's port type qname.
    * @param aFilter The selection criteria.
    * @param aReceiver A queued message receiver.
    */
   protected static boolean isPortTypeMatch( AeMessageReceiverFilter aFilter, AeMessageReceiver aReceiver )
   {
      if( aFilter.getPortType() != null )
      {
         return isNamespaceUriMatch( aFilter.getPortType(), aReceiver ) &&
                isLocalPartMatch( aFilter.getPortType(), aReceiver );
      }
      else
      {
         return true;
      }
   }
   
   /**
    * Returns true if there is no namespace specified in the qname or
    * if the qname namespace uri in the filter mathes the receive's 
    * port type qname namespace uri.
    * @param aPortType The port type qname.
    * @param aReceiver A queued message receiver.
    */
   static boolean isNamespaceUriMatch( QName aPortType, AeMessageReceiver aReceiver )
   {
      if( !AeUtil.isNullOrEmpty( aPortType.getNamespaceURI() ) )
      {
         return aPortType.getNamespaceURI().equals( aReceiver.getPortType().getNamespaceURI() );
      }
      else
      {
         return true;
      }
   }
   
   /**
    * Returns true if the local part of the qname matches 
    * the receive's port type qname local part.
    * @param aQName The port type qname.
    * @param aReceiver A queued message receiver.
    */
   static boolean isLocalPartMatch( QName aQName, AeMessageReceiver aReceiver )
   {
      return aQName.getLocalPart().equals( aReceiver.getPortType().getLocalPart() );
   }
   
   /**
    * Returns true if there is no operation name specified in the filter or
    * if the operation in the filter mathes the receive's operation. 
    * @param aFilter The selection criteria.
    * @param aReceiver A queued message receiver.
    */
   static boolean isOperationMatch( AeMessageReceiverFilter aFilter, AeMessageReceiver aReceiver )
   {
      if( !AeUtil.isNullOrEmpty( aFilter.getOperation() ) )
      {
         return aFilter.getOperation().equals( aReceiver.getOperation() );
      }
      else
      {
         return true;
      }
   }
   
   /**
    * Sorts the matching queued receiver.
    * @param aMatches
    */
   protected static void sort( List aMatches )
   {
      Collections.sort( aMatches, SORTER );
   }
   
   /**
    * Comparator impl.  Does string compares on partnerlink type name,
    * portType, operation and int compares on process id.
    */
   protected static class AeMessageReceiverComparator implements Comparator
   {
      /**
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      public int compare(Object a1, Object a2)
      {
         AeMessageReceiver receiverOne = (AeMessageReceiver)a1;
         AeMessageReceiver receiverTwo = (AeMessageReceiver)a2;
         
         int match = receiverOne.getPartnerLinkOperationKey().getPartnerLinkName().compareTo( receiverTwo.getPartnerLinkOperationKey().getPartnerLinkName() );
         
         if( match == 0 )
         {
            match = receiverOne.getPortType().toString().compareTo( receiverTwo.getPortType().toString() );

            if( match == 0 )
            {
               match = receiverOne.getOperation().compareTo( receiverTwo.getOperation() );

               if( match == 0 )
               {
                  match = (int) (receiverOne.getProcessId() - receiverTwo.getProcessId());
               }
            }
         }

         return match;
      }
   }
}
