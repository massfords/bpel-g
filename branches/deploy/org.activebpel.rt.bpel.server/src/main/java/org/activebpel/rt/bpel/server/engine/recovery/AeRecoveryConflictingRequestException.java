//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/AeRecoveryConflictingRequestException.java,v 1.1 2006/10/05 21:17:18 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery; 

import org.activebpel.rt.AeException;

/**
 * Exception thrown during the recovery process to signal a conflicting request.
 * This exception will get converted to an AeConflictingRequestException and
 * rethrown by the recovery engine/manager. 
 */
public class AeRecoveryConflictingRequestException extends AeException
{
}
 