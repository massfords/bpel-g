//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/IAeProcessRecovery.java,v 1.1 2004/12/23 23:06:35 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

/**
 * Defines interface for recovering processes.
 */
public interface IAeProcessRecovery
{
   /**
    * Prepares to recover pending work.
    */
   public void prepareToRecover();

   /**
    * Recovers pending work.
    */
   public void recover();
}