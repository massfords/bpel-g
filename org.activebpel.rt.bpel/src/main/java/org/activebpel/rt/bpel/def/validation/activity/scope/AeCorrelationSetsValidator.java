//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AeCorrelationSetsValidator.java,v 1.1 2006/08/16 22:07:27 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope; 

import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.def.AeCorrelationSetsDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * model provides validation for the correlationSets def
 */
public class AeCorrelationSetsValidator extends AeBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeCorrelationSetsValidator(AeCorrelationSetsDef aDef)
   {
      super(aDef);
   }

   /**
    * Returns the correlationSet with the given name or null if not defined here
    * @param aName
    */
   public AeCorrelationSetValidator getCorrelationSetModel(String aName)
   {
      List correlationSets = getChildren(AeCorrelationSetValidator.class);
      for (Iterator iter = correlationSets.iterator(); iter.hasNext();)
      {
         AeCorrelationSetValidator corrSetModel = (AeCorrelationSetValidator) iter.next();
         if (corrSetModel.getName().equals(aName))
            return corrSetModel;
      }
      return null;
   }
}
 