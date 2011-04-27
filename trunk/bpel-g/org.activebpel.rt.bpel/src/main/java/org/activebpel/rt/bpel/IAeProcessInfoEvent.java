// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeProcessInfoEvent.java,v 1.13 2008/03/11 03:05:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

/**
 * Interface for process information events.
 *
 * Events are reported to listeners via handleProcessInfoEvent().
 */
public interface IAeProcessInfoEvent extends IAeBaseProcessEvent
{
	ProcessInfoEventType getProcessInfoEventType();
}
