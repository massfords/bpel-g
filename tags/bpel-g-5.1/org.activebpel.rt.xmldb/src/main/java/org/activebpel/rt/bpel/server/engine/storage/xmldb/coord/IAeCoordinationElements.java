//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/coord/IAeCoordinationElements.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
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
 * XMLDB instance document element names.
 */
public interface IAeCoordinationElements
{
   public static final String COORDINATION_PK   = "CoordinationPk"; //$NON-NLS-1$
   public static final String ENGINE_ID         = "EngineID"; //$NON-NLS-1$
   public static final String COORDINATION_TYPE = "CoordinationType"; //$NON-NLS-1$
   public static final String COORDINATION_ROLE = "CoordinationRole"; //$NON-NLS-1$
   public static final String COORDINATION_ID   = "CoordinationID"; //$NON-NLS-1$   
   public static final String STATE             = "State"; //$NON-NLS-1$
   public static final String PROCESS_ID        = "ProcessID"; //$NON-NLS-1$
   public static final String LOCATION_PATH     = "LocationPath"; //$NON-NLS-1$
   public static final String COORDINATION_DOC  = "CoordinationDocument"; //$NON-NLS-1$
   public static final String START_DATE        = "StartDate"; //$NON-NLS-1$   
   public static final String MODIFIED_DATE     = "ModifiedDate"; //$NON-NLS-1$
}
