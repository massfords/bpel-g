// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/AeQueuedReceiveMessageData.java,v 1.4 2005/02/01 19:56:36 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 *  Wraps the message data for web ui.
 */
public class AeQueuedReceiveMessageData
{

   /** message data qname */
   private QName mQName;
   /** map of message data */
   private Map mPartData;
   
   /**
    * Constructor.
    * @param aQName
    */
   public AeQueuedReceiveMessageData( QName aQName )
   {
      mQName = aQName;
      mPartData = new HashMap();
   }
   
   /**
    * Add message parts.
    * @param aName
    * @param aData
    */
   public void addPartData( String aName, Object aData )
   {
      getPartData().put( aName, aData );
   }
   
   /**
    * Return part data map.
    */
   public Map getPartData()
   {
      return mPartData;
   }
   
   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return mQName + "-" + getPartData(); //$NON-NLS-1$
   }
}
