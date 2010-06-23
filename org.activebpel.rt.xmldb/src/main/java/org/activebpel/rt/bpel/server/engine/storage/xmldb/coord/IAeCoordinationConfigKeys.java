//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/coord/IAeCoordinationConfigKeys.java,v 1.1 2007/08/17 00:40:56 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.coord;

/**
 * Constants for the Coordination storage XMLDB keys.
 */
public interface IAeCoordinationConfigKeys
{
   public static final String INSERT_CONTEXT             = "InsertContext"; //$NON-NLS-1$
   public static final String UPDATE_CONTEXT             = "UpdateContext"; //$NON-NLS-1$   
   public static final String UPDATE_STATE               = "UpdateState"; //$NON-NLS-1$
   public static final String LIST_BY_COORDINATION_ID    = "ListByCoordinationId"; //$NON-NLS-1$
   public static final String LIST_BY_PROCESS_ID         = "ListByProcessId"; //$NON-NLS-1$
   public static final String LOOKUP_COORDINATION        = "LookupCoordination"; //$NON-NLS-1$
   public static final String DELETE_COORDINATION        = "DeleteCoordination"; //$NON-NLS-1$
   public static final String DELETE_BY_PROCESS_ID       = "DeleteByProcessId"; //$NON-NLS-1$
   public static final String DELETE_BY_COORDINATION_ID  = "DeleteByCoordinationId"; //$NON-NLS-1$     
   public static final String LIST_COORDINATORS_FOR_PID  = "ListCoordinatorsForProcessId"; //$NON-NLS-1$
   public static final String LIST_PARTICIPANTS_FOR_PID  = "ListParticipantsForProcessId"; //$NON-NLS-1$
}
