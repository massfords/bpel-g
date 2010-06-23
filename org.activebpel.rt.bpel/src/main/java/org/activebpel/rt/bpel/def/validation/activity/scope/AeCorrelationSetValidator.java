//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AeCorrelationSetValidator.java,v 1.3 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope; 

import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;

/**
 * moddel provides validation for a correlation set def
 */
public class AeCorrelationSetValidator extends AeBaseValidator
{
   /** used to record that the correlation set is used by a web service interaction */
   private boolean mReferenced = false;

   /**
    * ctor
    * @param aDef
    */
   public AeCorrelationSetValidator(AeCorrelationSetDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Getter for the name
    */
   public String getName()
   {
      return getDef().getName();
   }
   
   /**
    * Getter for the correlationSet def
    */
   public AeCorrelationSetDef getDef()
   {
      return (AeCorrelationSetDef) getDefinition();
   }

   /**
    * Validates:
    * 1. warning if the correlationSet isn't referenced
    * 2. all of the properties in the set are resolved
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      if (!isReferenced())
      {
         getReporter().reportProblem( BPEL_CORR_SET_NOT_USED_CODE,  
                                 WARNING_CORR_SET_NOT_USED,  
                                 new String[] { getDef().getName() }, getDefinition());
      }
      
      validateNCName(true);
      
      // Ensure that the Correlation Set has an assigned properties
      //  list and the properties' QNames can be resolved in WSDL.
      //
      Collection props = getDef().getProperties();
      if ( props != null && !props.isEmpty() )
      {
         for ( Iterator iterProps = props.iterator() ; iterProps.hasNext() ; )
         {
            QName propName = (QName)iterProps.next();
            AeBPELExtendedWSDLDef wsdl = AeWSDLDefHelper.getWSDLDefinitionForProp( getValidationContext().getContextWSDLProvider(), propName );
            if ( wsdl == null )
            {
               addTypeNotFoundError(ERROR_PROP_NOT_FOUND, propName);
            }
         }
      }
      else
      {
         // No properties defined - error.  Added as fix for Defect #341.
         //
         getReporter().reportProblem( BPEL_CORR_SET_PROPS_NOT_FOUND_CODE,
                                 ERROR_CORR_SET_PROPS_NOT_FOUND,
                                 new String[] { getDef().getName() },
                                 getDef() );
      }
   }

   /**
    * records the reference to the correlationSet
    */
   public void addReference()
   {
      mReferenced = true;
   }
   
   /**
    * Returns true if the correlationSet is referenced
    */
   public boolean isReferenced()
   {
      return mReferenced;
   }
}
 