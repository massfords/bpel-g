// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/input/IAeInputMessageWork.java,v 1.1 2007/07/31 20:53:47 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work.input;

import commonj.work.Work;

import java.io.Serializable;

/**
 * Defines the interface for work to process an input message. Extends
 * <code>java.io.Serializable</code>, so that input message work can potentially
 * be moved to a remote work manager for execution.
 */
public interface IAeInputMessageWork extends Work, Serializable
{
}
