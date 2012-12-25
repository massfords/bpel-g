//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/subprocess/IAeSpProtocolMessage.java,v 1.3 2008/03/28 01:45:25 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord.subprocess;

import org.activebpel.rt.bpel.coord.IAeProtocolMessage;

/**
 * Interface for AE Subprocess coordination type protcol messages.
 * This protocol is loosely based on Business Agreement protocol described in
 * the BPEL-4WS 1.1, appendix C.
 */
public interface IAeSpProtocolMessage extends IAeProtocolMessage
{
   /**
    * Returns the process id of the target.
    */
   public long getProcessId();
   
   /**
    * Returns the process id of the source process.
    */
   public long getSourceProcessId();
   
   /**
    * Returns the location path of the target.
    */
   public String getLocationPath();
   
   /**
    * Getter for the journal id
    */
   public long getJournalId();
   
   /**
    * Setter for the journal id
    * @param aJournalId
    */
   public void setJournalId(long aJournalId);
}
