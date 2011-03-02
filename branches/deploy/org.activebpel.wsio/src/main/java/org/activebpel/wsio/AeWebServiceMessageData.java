// $Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/AeWebServiceMessageData.java,v 1.5 2007/05/01 17:00:40 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Standard impl of <code>IAeWebServiceMessageData</code>.
 */
public class AeWebServiceMessageData implements IAeWebServiceMessageData
{
   /** Message qname. */
   protected QName mMessageQName;
   /** Message part data. */
   protected Map mParts = new HashMap();
   /** Optional list of attachments */
   protected List mAttachments;
   
   /**
    * no arg ctor.
    */
   public AeWebServiceMessageData()
   {
   }

   /**
    * Constructor.
    * @param aQName
    */
   public AeWebServiceMessageData( QName aQName )
   {
      mMessageQName = aQName;
   }
   
   /**
    * Constructor.
    * @param aQName
    * @param aData
    */
   public AeWebServiceMessageData( QName aQName, Map aData )
   {
      this( aQName );
      mParts.putAll( aData );
   }
   
    /**
    * @see org.activebpel.wsio.IAeWebServiceMessageData#getMessageData()
    */
   public Map getMessageData()
   {
      return mParts;
   }
   
   /**
    * Setter for message data.
    * @param aPartName
    * @param aMessageData
    */
   public void setData( String aPartName, Object aMessageData )
   {
      mParts.put( aPartName, aMessageData );
   }
   
   /**
    * Setter for name
    * @param aName
    */
   public void setName(QName aName)
   {
      mMessageQName = aName;
   }
   
   /**
    * Setter for attachments.
    * @param aAttachments
    */
   public void setAttachments( List aAttachments )
   {
      mAttachments = aAttachments;
   }

   /**
    * @see org.activebpel.wsio.IAeWebServiceMessageData#getMessageType()
    */
   public QName getMessageType()
   {
      return mMessageQName;
   }

   /**
    * @see org.activebpel.wsio.IAeWebServiceMessageData#getAttachments()
    */
   public List getAttachments()
   {
      return mAttachments;
   }
}
