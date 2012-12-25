//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeVariableComplexTypeDataWrapper.java,v 1.4 2006/12/14 22:59:39 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.to; 

import org.activebpel.rt.bpel.IAeVariable;

/**
 * Wraps a complex type variable being assigned to. If the variable is null then an empty element
 * is automatically created to receive the data. 
 */
public class AeVariableComplexTypeDataWrapper extends AeVariableBaseTypeDataWrapper
{
   /**
    * Ctor accepts variable
    * 
    * @param aVariable
    */
   public AeVariableComplexTypeDataWrapper(IAeVariable aVariable)
   {
      super(aVariable);
   }
}
 