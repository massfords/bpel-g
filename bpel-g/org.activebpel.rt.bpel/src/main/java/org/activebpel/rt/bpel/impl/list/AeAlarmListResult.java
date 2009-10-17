// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeAlarmListResult.java,v 1.3 2006/06/26 16:50:48 mford Exp $
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wraps a listing of alarm objects.
 */
public class AeAlarmListResult extends AeListResult implements Serializable
{
   /** Mapping of process ids to location paths. */
   protected Map mLocationIdtoLocationPathMap;
   
   /**
    * Constructor.
    * @param aTotalRows Total rows that matched selection criteria.  This number may be greater than the number of results in this listing.
    * @param aAlarms The matching alarms.
    */
   public AeAlarmListResult( int aTotalRows, List aAlarms )
   {
      super( aTotalRows, aAlarms, true );
      mLocationIdtoLocationPathMap = new HashMap();
   }
   
   /**
    * Accessor for alarms.
    */
   public AeAlarmExt[] getResults()
   {
      return (AeAlarmExt[])getResultsInternal().toArray( new AeAlarmExt[getResultsInternal().size()]);
   }
   
   /**
    * Add a location id to location path mapping.
    * @param aLocationId The location path id.
    * @param aLocation The location xpath.
    */
   public void addPathMapping( int aLocationId, String aLocation )
   {
      mLocationIdtoLocationPathMap.put( new Integer( aLocationId ), aLocation );
   }
   
   /**
    * Returns the matching location path for this process id.
    * @param aLocationId
    */
   public String getLocationPath( int aLocationId )
   {
      return (String)mLocationIdtoLocationPathMap.get( new Integer(aLocationId) );
   }
}
