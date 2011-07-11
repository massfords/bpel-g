//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeToPropertyElement.java,v 1.4 2006/12/14 22:59:39 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.to; 

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.impl.activity.assign.IAeVariableDataWrapper;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;

/**
 * Gets the target for the copy using a propertyAlias 
 */
public class AeToPropertyElement extends AeToPropertyBase
{
   /**
    * Ctor accepts def
    * 
    * @param aToDef
    */
   public AeToPropertyElement(AeToDef aToDef)
   {
      super(aToDef);
   }
   
   /**
    * Ctor accepts variable and property
    * @param aVariableName
    * @param aProperty
    */
   public AeToPropertyElement(String aVariableName, QName aProperty)
   {
      super(aVariableName, aProperty);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.to.AeToPropertyBase#getPropertyAlias()
    */
   public IAePropertyAlias getPropertyAlias() throws AeBusinessProcessException
   {
      return getCopyOperation().getContext().getPropertyAlias(IAePropertyAlias.ELEMENT_TYPE, getVariable().getElement(), getProperty());
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.to.AeToPropertyBase#getDataForQueryContext(org.activebpel.rt.wsdl.def.IAePropertyAlias)
    */
   public Object getDataForQueryContext(IAePropertyAlias aPropAlias) throws AeBusinessProcessException
   {
      return getVariable().getElementData();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.to.AeToPropertyBase#getVariableDataWrapper()
    */
   protected IAeVariableDataWrapper getVariableDataWrapper() throws AeBusinessProcessException
   {
      return new AeVariableElementDataWrapper(getVariable());
   }
} 