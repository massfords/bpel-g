// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/scanner/IAeScannerListener.java,v 1.1 2004/08/25 20:30:21 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.scanner;


/**
 * Listener interface for event callbacks fired when
 * the <code>AeDirectoryScanner</code> detects a change
 * in its watch file list. 
 */
public interface IAeScannerListener
{
   
   /**
    * Notification that a watch file has changed.
    * @param aEvent
    */
   public void onChange( AeScanEvent aEvent );

}
