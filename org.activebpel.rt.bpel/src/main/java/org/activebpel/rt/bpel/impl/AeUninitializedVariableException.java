//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeUninitializedVariableException.java,v 1.1 2006/08/02 14:40:29 mford Exp $
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
public class AeUninitializedVariableException extends AeBpelException
{
   /**
    * Creates the exception with the proper namespace
    * @param aNamespace
    */
   public AeUninitializedVariableException(String aNamespace)
   {
      super(AeMessages.getString("AeUninitializedVariableException.Error"), AeFaultFactory.getFactory(aNamespace).getUninitializedVariable()); //$NON-NLS-1$
   }

}
 