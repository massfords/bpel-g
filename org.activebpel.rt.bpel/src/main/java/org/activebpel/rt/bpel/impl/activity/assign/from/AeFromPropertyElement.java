//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/from/AeFromPropertyElement.java,v 1.5 2006/12/14 22:58:53 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.from; 

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.impl.AeUninitializedVariableException;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;

/**
 * Handles reading a value from an element using a property alias
 */
public class AeFromPropertyElement extends AeFromPropertyBase
{
   /**
    * Ctor accepts def and context
    * @param aFromDef
    */
   public AeFromPropertyElement(AeFromDef aFromDef)
   {
      super(aFromDef);
   }
   
   /**
    * Ctor accepts variable name and property
    * @param aVariableName
    * @param aProperty
    */
   public AeFromPropertyElement(String aVariableName, QName aProperty)
   {
      super(aVariableName, aProperty);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.from.AeFromPropertyBase#getDataForQueryContext(org.activebpel.rt.wsdl.def.IAePropertyAlias)
    */
   public Object getDataForQueryContext(IAePropertyAlias aPropAlias) throws AeUninitializedVariableException
   {
      return getVariable().getElementData();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.from.AeFromPropertyBase#getPropertyAlias()
    */
   public IAePropertyAlias getPropertyAlias() throws AeBusinessProcessException
   {
      return getCopyOperation().getContext().getPropertyAlias(IAePropertyAlias.ELEMENT_TYPE, getVariable().getDefinition().getElement(), getProperty());
   }
}
 