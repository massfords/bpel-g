//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeVariableBaseDataWrapper.java,v 1.6 2006/12/14 22:59:40 mford Exp $
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
import org.activebpel.rt.bpel.impl.activity.assign.IAeVariableDataWrapper;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Base class for the variable data wrappers. Provides getter and setter for variable being assigned to. 
 */
public abstract class AeVariableBaseDataWrapper implements IAeVariableDataWrapper
{
   /** variable being assigned to */
   private IAeVariable mVariable;
   
   /**
    * Ctor accepts variable
    * 
    * @param aVariable
    */
   public AeVariableBaseDataWrapper(IAeVariable aVariable)
   {
      setVariable(aVariable);
   }

   /**
    * @return Returns the variable.
    */
   public IAeVariable getVariable()
   {
      return mVariable;
   }

   /**
    * @param aVariable The variable to set.
    */
   public void setVariable(IAeVariable aVariable)
   {
      mVariable = aVariable;
   }
   
   /**
    * Gets the value that is being assigned to 
    * 
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeVariableDataWrapper#getValue()
    */
   public abstract Object getValue() throws AeBpelException;
   
   /**
    * Creates a clone of a DOM Node or no-op if param isn't a Node.
    * @param aValue
    */
   protected Object cloneValue(Object aValue)
   {
      Object value = null;
      if (aValue instanceof Element)
      {
         value = AeXmlUtil.cloneElement((Element) aValue);
      }
      else if (aValue instanceof Document)
      {
         value = AeXmlUtil.cloneElement(((Document) aValue).getDocumentElement());
      }
      else
      {
         value = aValue;
      }
      return value;
   }
   
   /**
    * Getter for the namespace
    */
   protected String getNamespace()
   {
      return getVariable().getParent().getParent().getProcess().getBPELNamespace();
   }
}
 