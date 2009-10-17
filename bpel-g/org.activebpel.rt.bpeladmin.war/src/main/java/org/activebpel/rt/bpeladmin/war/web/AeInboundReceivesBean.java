// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeInboundReceivesBean.java,v 1.3 2005/01/14 16:30:35 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.bpel.server.admin.AeQueuedReceiveDetail;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 * Top level listing of unmatched inbound queued receives. 
 */
public class AeInboundReceivesBean
{
   /** Unmatched inbound queued receives */
   protected List mDetails;
   /** Current row index. */
   protected int mCurrentIndex;
      
   /**
    * Default constructor.
    * Intializes the unmatched inbound queued receives list.
    */
   public AeInboundReceivesBean()
   {
      AeQueuedReceiveDetail[] details = AeEngineFactory.getEngineAdministration().getUnmatchedQueuedReceives();
      Map detailsMap = parse(details);
      mDetails = new ArrayList( detailsMap.values() );
   }
   
   /**
    * Returns true if the row details are empty.
    */
   public boolean isEmpty()
   {
      return mDetails == null || mDetails.size() == 0;
   }
   
   /**
    * Maps the partner link, port type, operation key to one or more
    * AeQueuedReceiveDetail objects.
    * @param aDetails
    */
   protected static Map parse( AeQueuedReceiveDetail[] aDetails )
   {
      Map recs = new HashMap();
      for( int i = 0; i < aDetails.length; i++ )
      {
         AeQueuedReceiveDetail detail = aDetails[i];
         addToMap( recs, detail );
      }
      return recs;
   }
   
   /**
    * Convenience method for adding details to the map.
    * @param aHashMap
    * @param aDetail
    */
   protected static void addToMap( Map aHashMap, AeQueuedReceiveDetail aDetail )
   {
      String key = makeKey( aDetail );
      List matches = (List)aHashMap.get( key );
      if( matches == null )
      {
         matches = new ArrayList();
         aHashMap.put( key, matches );
      }
      matches.add( aDetail );
   }

   /**
    * Create a key based on the partner link, port type and operation.
    * @param aDetail
    */
   public static String makeKey( AeQueuedReceiveDetail aDetail )
   {
      return aDetail.getPartnerLinkName()+":"+aDetail.getPortTypeAsString()+ //$NON-NLS-1$
            ":"+aDetail.getOperation(); //$NON-NLS-1$
   }
   
   
   /**
    * Indexed accessor for the queued receive detail.
    * @param aIndex
    */
   public AeQueuedReceiveDetail getDetail( int aIndex )
   {
      mCurrentIndex = aIndex;
      List detailList = (List)mDetails.get(aIndex);
      return (AeQueuedReceiveDetail)detailList.get(0);
   }
   
   /**
    * Returns the number of queued receives for the current row.
    */
   public int getQueuedReceiveCount()
   {
      return ((List)mDetails.get(mCurrentIndex)).size();
   }
   
   /**
    * Creates a unique key to identify this row.
    */
   public String getIdentifier()
   {
      List detailList = (List)mDetails.get(mCurrentIndex);
      return makeKey((AeQueuedReceiveDetail)detailList.get(0));
   }
   
   /**
    * Returns the number of details rows available.
    */
   public int getDetailSize()
   {
      if( mDetails == null )
      {
         return 0;
      }
      else
      {
         return mDetails.size();
      }
   }
}
