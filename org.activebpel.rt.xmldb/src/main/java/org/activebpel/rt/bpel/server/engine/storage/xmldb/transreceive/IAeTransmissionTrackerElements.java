//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/transreceive/IAeTransmissionTrackerElements.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.transreceive;

/**
 * List of element names used by the TransmissionTracker.
 */
public interface IAeTransmissionTrackerElements
{
   public static final String TRANSMISSION_ID   = "TransmissionId"; //$NON-NLS-1$
   public static final String STATE             = "State"; //$NON-NLS-1$
   public static final String MESSAGE_ID        = "MessageId"; //$NON-NLS-1$
}
