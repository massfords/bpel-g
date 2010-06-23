//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeServiceNotFoundException.java,v 1.1 2007/09/02 17:17:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy; 

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;

/**
 * Exception for a service that wasn't found 
 */
public class AeServiceNotFoundException extends AeException
{
   /**
    * Ctor
    * @param aServiceName
    * @param aRoot
    */
   public AeServiceNotFoundException(String aServiceName, Throwable aRoot)
   {
      super(AeMessages.format("AeServiceNotFoundException.Message", aServiceName), aRoot); //$NON-NLS-1$
   }
}
 