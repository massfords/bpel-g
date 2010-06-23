//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/from/AeFromPropertyBase.java,v 1.3 2006/07/14 15:46:50 mford Exp $
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
import org.activebpel.rt.bpel.impl.activity.assign.AePropertyAliasBasedSelector;
import org.activebpel.rt.bpel.impl.activity.assign.IAePropertyAliasCopyOperation;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;

/**
 * Base class for impls that read variable data using a property alias 
 */
public abstract class AeFromPropertyBase extends AeFromBase implements IAePropertyAliasCopyOperation
{
   /** name of the property */
   private QName mProperty;

   /**
    * Ctor takes def
    * 
    * @param aFromDef
    */
   public AeFromPropertyBase(AeFromDef aFromDef)
   {
      super(aFromDef);
      setProperty(aFromDef.getProperty());
   }
   
   /**
    * Ctor accepts variable name and property
    * @param aVariableName
    * @param aProperty
    */
   protected AeFromPropertyBase(String aVariableName, QName aProperty)
   {
      setVariableName(aVariableName);
      setProperty(aProperty);
   }

   /**
    * Template method that allows subclasses to override getting of property alias and data for query
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeFrom#getFromData()
    */
   public Object getFromData() throws AeBusinessProcessException
   {
      IAePropertyAlias propAlias = getPropertyAlias();
      return AePropertyAliasBasedSelector.selectValue(propAlias, getDataForQueryContext(propAlias), getCopyOperation().getContext());
   }

   /**
    * @return Returns the property.
    */
   public QName getProperty()
   {
      return mProperty;
   }

   /**
    * @param aProperty The property to set.
    */
   public void setProperty(QName aProperty)
   {
      mProperty = aProperty;
   }
}
 