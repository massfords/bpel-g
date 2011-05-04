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

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.List;

/**
 * Wraps a listing of alarm objects.
 */
public class AeAlarmListResult extends AeListResult implements Serializable
{
   /**
     * 
     */
    private static final long serialVersionUID = -249832311166148492L;

/**
    * Constructor.
    * @param aTotalRows Total rows that matched selection criteria.  This number may be greater than the number of results in this listing.
    * @param aAlarms The matching alarms.
    */
   @ConstructorProperties({"totalRowCount", "results"})
   public AeAlarmListResult( int aTotalRows, List<AeAlarmExt> aAlarms )
   {
      super( aTotalRows, aAlarms, true );
   }
}
