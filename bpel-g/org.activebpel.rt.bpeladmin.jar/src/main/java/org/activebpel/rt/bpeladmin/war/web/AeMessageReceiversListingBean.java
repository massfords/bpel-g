// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeMessageReceiversListingBean.java,v 1.6 2006/01/11 16:22:00 RNaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.server.admin.jmx.AeMessageReceiverBean;

/**
 *  Wraps the details for the Message Receivers Queue listing.
 */
public class AeMessageReceiversListingBean extends AeAbstractListingBean
{
   /** AeMessageReceives to display. */   
   protected List<AeMessageReceiverDetailWrapper> mMessageReceivers;
   /** Process id. */
   protected long mProcessId = -1;
   /** The current row being processed. */
   protected int mCurrentIndex;
   /** Partner link type name. */
   protected String mPartnerLinkTypeName = ""; //$NON-NLS-1$
   /** PortType QName. */
   protected QName mPortType;
   /** Operation name. */
   protected String mOperation  = ""; //$NON-NLS-1$

   /**
    * Constructor.  
    */
   public AeMessageReceiversListingBean()
   {
      super();
   }
   
   /**
    * Setting this flag to true signals that the 
    * bean should update its state based on the 
    * filter parameters.
    * @param aUpdateFlag
    */
   public void setFinished( boolean aUpdateFlag )
   {
      if( aUpdateFlag )
      {
         QName qname = AeWebUtil.toQName( getPortType() );
         
         String namespace = qname != null? qname.getNamespaceURI() : null;
         String name = qname != null? qname.getLocalPart() : null;
         
         List<AeMessageReceiverBean> results = getAdmin().getMessageReceivers(mProcessId, getPartnerLinkTypeName(), namespace, name, getOperation(), getRowCount(), getRowStart() );
            
         if( !results.isEmpty() )
         { 
            setTotalRowCount( results.size() );
            updateNextPageStatus();
            mMessageReceivers = new ArrayList<>();

            for(AeMessageReceiverBean bean : results)
               mMessageReceivers.add( new AeMessageReceiverDetailWrapper(bean));                 

            setRowsDisplayed( mMessageReceivers.size() );
         }
         else
         {
            setNextPage( false );
         }
      }
   }
   
   /**
    * Indexed accessor for a message receiver.
    * @param aIndex
    * @return The AeMessageReceiverDetailWrapper mapped to the index.
    */
   public AeMessageReceiverDetailWrapper getMessageReceiver( int aIndex )
   {
      return mMessageReceivers.get(aIndex);          
   }
   
   /**
    * Accessor for the number of the receivers.
    */
   public int getMessageReceiverSize()
   {
      if( mMessageReceivers == null )
      {
         return 0;
      }
      else
      {
         return mMessageReceivers.size();
      }
   }

   /**
    * Setter for the process id.
    * @param aId A process id.
    */
   public void setProcessId( String aId )
   {
      try
      {
         mProcessId = Long.parseLong(aId);
      }
      catch( NumberFormatException e )
      {
         // @todo - error handling
      }
   }
   
   /**
    * Getter for the process id.
    */
   public String getProcessId()
   {
      if( mProcessId < 0 )
      {
         return ""; //$NON-NLS-1$
      }
      else
      {
         return String.valueOf(mProcessId);
      }
   }
   
   /**
    * Setter for the partner link type name.
    * @param aName
    */
   public void setPartnerLinkTypeName( String aName )
   {
      mPartnerLinkTypeName = aName;
   }
   
   /**
    * Getter for the partner link type name.
    */
   public String getPartnerLinkTypeName()
   {
      return mPartnerLinkTypeName;
   }
   
   /**
    * Setter for the operation.
    * @param aOperation
    */
   public void setOperation( String aOperation )
   {
      mOperation = aOperation;
   }
   
   /**
    * Getter for the operation.
    */
   public String getOperation()
   {
      return mOperation;
   }

   /**
    * Setter for the port type.
    * @param aQName
    */
   public void setPortType( String aQName )
   {
      mPortType = AeWebUtil.toQName( aQName );
   }
   
   /**
    * Getter for the port type qname.
    */
   public QName getPortTypeQName()
   {
      return mPortType;
   }
   
   /**
    * Getter for the port type.
    */
   public String getPortType()
   {
      return AeWebUtil.toString(mPortType);
   }
   
   /**
    * @see org.activebpel.rt.bpeladmin.war.web.AeAbstractListingBean#isEmpty()
    */
   public boolean isEmpty()
   {
      return mMessageReceivers == null || mMessageReceivers.size() == 0;
   }
}
