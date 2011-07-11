//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/IAeTransmissionTrackerColumns.java,v 1.1 2006/05/24 23:16:32 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

/**
 * Column names used in SQL table for the transmission id manager.
 */
public interface IAeTransmissionTrackerColumns
{
   public static final String TRANSMISSION_ID   = "TransmissionId"; //$NON-NLS-1$
   public static final String STATE             = "State"; //$NON-NLS-1$
   public static final String MESSAGE_ID        = "MessageId"; //$NON-NLS-1$   
}
