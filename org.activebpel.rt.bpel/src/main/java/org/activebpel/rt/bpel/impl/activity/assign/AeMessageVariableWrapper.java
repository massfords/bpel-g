//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/AeMessageVariableWrapper.java,v 1.2 2006/06/26 16:50:36 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign; 

import org.activebpel.rt.bpel.IAeVariable;

/**
 * Wrapper object for the message variable. 
 */
public class AeMessageVariableWrapper implements IAeMessageVariableWrapper
{
   /** our variable */
   private IAeVariable mVariable;
   
   /**
    * Ctor accepts variable
    *  
    * @param aVariable
    */
   public AeMessageVariableWrapper(IAeVariable aVariable)
   {
      mVariable = aVariable;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeMessageVariableWrapper#getVariable()
    */
   public IAeVariable getVariable()
   {
      return mVariable;
   }
}
 