//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeInvalidVariableException.java,v 1.1 2008/02/21 17:03:27 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl; 

import org.activebpel.rt.bpel.AeMessages;

/**
 * Thrown when data is read from a variable that has not been initialized
 */
public class AeInvalidVariableException extends AeBpelException
{
   /**
    * Ctor
    * @param aNamespace
    * @param aVariableName
    * @param aMessage
    * @param aCause
    */
   public static AeInvalidVariableException createException(String aNamespace, String aVariableName, String aMessage, Throwable aCause)
   {
      Object[] args = {aVariableName, aMessage};
      String message = AeMessages.format("AeInvalidVariableException.Error", args); //$NON-NLS-1$
      return new AeInvalidVariableException(aNamespace, message, aCause);
   }
   
   /**
    * Creates the exception with the proper namespace
    * @param aNamespace
    * @param aMessage
    * @param aCause
    */
   public AeInvalidVariableException(String aNamespace, String aMessage, Throwable aCause)
   {
      super(aMessage, AeFaultFactory.getFactory(aNamespace).getInvalidVariables(aMessage), aCause);
   }
}
 