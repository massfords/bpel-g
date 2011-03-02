// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeMessageReceiverFilter.java,v 1.3 2005/07/12 00:17:04 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.list;

import java.io.Serializable;

import javax.xml.namespace.QName;

/**
 * Wraps selection criteria for selecting message receivers
 * off of the queue.
 */
public class AeMessageReceiverFilter extends AeListingFilter implements Serializable
{
   /** No selection criteria specified. */
   public static final AeMessageReceiverFilter NULL_FILTER = new AeMessageReceiverFilter();
   /** Default 'null' value for process id. */
   public static final long NULL_ID = -1;
   
   /** Process id. */
   protected long mProcessId = NULL_ID;
   /** PartnerLink name. */
   protected String mPartnerLinkName;
   /** Port type qname. */
   protected QName mPortType;
   /** Operation name. */
   protected String mOperation;
   
   /**
    * Constructor.
    */
   public AeMessageReceiverFilter()
   {
      super();
   }
   
   /**
    * Accessor for operation.
    */
   public String getOperation()
   {
      return mOperation;
   }

   /**
    * Accessor for partner link name.
    */
   public String getPartnerLinkName()
   {
      return mPartnerLinkName;
   }

   /**
    * Accessor for port type.
    */
   public QName getPortType()
   {
      return mPortType;
   }

   /**
    * Setter for operation.
    * @param aString
    */
   public void setOperation(String aString)
   {
      mOperation = aString;
   }

   /**
    * Setter for the partner link name.
    * @param aString
    */
   public void setPartnerLinkName(String aString)
   {
      mPartnerLinkName = aString;
   }

   /**
    * Setter for the port type qname.
    * @param aName
    */
   public void setPortType(QName aName)
   {
      mPortType = aName;
   }
   
   /**
    * Accessor for process id.
    */
   public long getProcessId()
   {
      return mProcessId;
   }
   
   /**
    * Returns true if the process id criteria has not been set.
    */
   public boolean isNullProcessId()
   {
      return mProcessId == NULL_ID;
   }

   /**
    * Setter for the process id.
    * @param aId
    */
   public void setProcessId(long aId)
   {
      mProcessId = aId;
   }
}
