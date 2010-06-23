//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeVariableElementDataWrapper.java,v 1.5 2006/12/14 22:59:39 mford Exp $
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
import org.activebpel.rt.bpel.impl.activity.assign.AeMismatchedAssignmentException;
import org.exolab.castor.xml.schema.XMLType;
import org.w3c.dom.Element;

/**
 * The target for assigning a value to a variable that's an element 
 */
public class AeVariableElementDataWrapper extends AeVariableBaseDataWrapper
{
   /**
    * Ctor accepts the variable
    * 
    * @param aVariable
    */
   public AeVariableElementDataWrapper(IAeVariable aVariable)
   {
      super(aVariable);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeVariableDataWrapper#getXMLType()
    */
   public XMLType getXMLType() throws AeBpelException
   {
      // This is only used for complex and simple types
      return null;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.to.AeVariableBaseDataWrapper#getValue()
    */
   public Object getValue() throws AeUninitializedVariableException
   {
      return getVariable().getElementData();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeVariableDataWrapper#setValue(java.lang.Object)
    */
   public void setValue(Object aValue) throws AeMismatchedAssignmentException
   {
      if (aValue instanceof Element)
      {
         getVariable().setElementData((Element) cloneValue(aValue));
      }
      else
      {
         // If we get here then there was likely something wrong with our strategy table since we should
         // only be here to copy string/element to an element
         throw new AeMismatchedAssignmentException(getNamespace());
      }
   }
}
 