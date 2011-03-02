//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeOperationNotImplementedException.java,v 1.1 2007/09/02 17:17:15 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy; 

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;

/**
 * Exception that indicates that the process exists but it doesn't provide
 * an IMA for the requested operation. 
 */
public class AeOperationNotImplementedException extends AeException
{
   /**
    * Ctor
    * @param aService
    * @param aPortType
    * @param aOperation
    */
   public AeOperationNotImplementedException(String aService, QName aPortType, String aOperation)
   {
      super(AeMessages.format("AeOperationNotImplementedException.Message", new String[] {aService, aPortType.getNamespaceURI(), aPortType.getLocalPart(), aOperation})); //$NON-NLS-1$
   }
}
 