//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeVariableBaseTypeDataWrapper.java,v 1.4 2006/12/14 22:59:40 mford Exp $
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
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeUninitializedVariableException;
import org.exolab.castor.xml.schema.XMLType;

/**
 * Base class used for both simple and complex type data wrappers. 
 */
public abstract class AeVariableBaseTypeDataWrapper extends AeVariableBaseDataWrapper
{
   /**
    * Ctor accepts variable
    * 
    * @param aVariable
    */
   public AeVariableBaseTypeDataWrapper(IAeVariable aVariable)
   {
      super(aVariable);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.to.AeVariableBaseDataWrapper#getValue()
    */
   public Object getValue() throws AeUninitializedVariableException
   {
      return getVariable().getTypeData();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeVariableDataWrapper#setValue(java.lang.Object)
    */
   public void setValue(Object aValue)
   {
      getVariable().setTypeData(cloneValue(aValue));
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeVariableDataWrapper#getXMLType()
    */
   public XMLType getXMLType() throws AeBpelException
   {
      return getVariable().getDefinition().getXMLType();
   }
}
 