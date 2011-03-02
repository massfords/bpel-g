//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AeFaultHandlersValidator.java,v 1.8 2008/02/17 21:37:11 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope; 

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.activebpel.rt.bpel.IAeFaultTypeInfo;
import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.AeFaultHandlersDef;
import org.activebpel.rt.bpel.def.faults.AeFaultMatchingStrategyFactory;
import org.activebpel.rt.bpel.def.faults.IAeFaultMatchingStrategy;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeBaseCatchValidator.AeCatchSpec;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;

/**
 * model provides validation for the faultHandlers def
 */
public abstract class AeFaultHandlersValidator extends AeBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeFaultHandlersValidator(AeFaultHandlersDef aDef)
   {
      super(aDef);
   }
      
   /**
    * Validate fault handler to make sure no identical catch elements exsits in one fault handler. 
    * The catch constructs are considered identical in this context, when they have identical 
    * values in their falutName, faultElement, and faultMessageType attribute.
    * @see org.activebpel.rt.bpel.def.validation.IAeValidator#validate()
    */
   public void validate()
   {
      super.validate();

      Set noDupes = new HashSet();
      List catchChildren = getChildren(AeBaseCatchValidator.class);
      for (Iterator iter = catchChildren.iterator(); iter.hasNext();)
      {
         AeBaseCatchValidator baseCatch = (AeBaseCatchValidator)iter.next();
         IAeCatchValidatorSpec spec = createSpecValidator(baseCatch);
         if ( !noDupes.add(spec) )
         {
            reportDuplicateCatch(baseCatch);
         }
      }
   }

   /**
    * @param baseCatch
    */
   protected abstract void reportDuplicateCatch(AeBaseCatchValidator baseCatch);

   /**
    * @param baseCatch
    */
   protected IAeCatchValidatorSpec createSpecValidator(AeBaseCatchValidator baseCatch)
   {
      return new AeBaseCatchValidatorSpec(baseCatch.getDef());
   }

   /**
    * Inner interface for AeCatchValidatorSpec definition
    *
    */
   protected interface IAeCatchValidatorSpec
   {
      /**
       * @return Returns the catchDef.
       */
      public AeCatchDef getCatchDef();

      /**
       * @param aCatchDef The catchDef to set.
       */
      public void setCatchDef(AeCatchDef aCatchDef);

      /**
       * @return Returns the catchSpec.
       */
      public AeCatchSpec getCatchSpec();

      /**
       * @param aCatchSpec The catchSpec to set.
       */
      public void setCatchSpec(AeCatchSpec aCatchSpec);
   }
         
   /**
    * Nested base class to create a object of AeCatchValidatorSpec for comparing the two 
    * catch elements. 
    */
   protected static class AeBaseCatchValidatorSpec implements IAeCatchValidatorSpec
   {
      private AeCatchSpec mCatchSpec;
      private AeCatchDef mCatchDef;
      
      /**
       * ctor
       * @param aDef
       */
      public AeBaseCatchValidatorSpec(AeCatchDef aDef)
      {
         mCatchDef = aDef;
         mCatchSpec = AeCatchSpec.create(aDef);
      }
      
      /**
       * Equality is determined by comparing the AeCatchSpec of the fault handler and if the 
       * spec is equal then compare the faultName, faultVariable, falutElementName and faultMessageType.
       * @see java.lang.Object#equals(java.lang.Object)
       */    
      public boolean equals(Object aObject)
      {
         if ( aObject == null || !(aObject instanceof AeBaseCatchValidatorSpec))
            return false;

         IAeCatchValidatorSpec otherSpec = (IAeCatchValidatorSpec)aObject;
         boolean isEqual = AeUtil.compareObjects(mCatchSpec, otherSpec.getCatchSpec());
         if ( isEqual )
         {
            isEqual = AeUtil.compareObjects(mCatchDef.getFaultName(), otherSpec.getCatchDef().getFaultName())
                  && AeUtil.compareObjects(mCatchDef.getFaultElementName(), otherSpec.getCatchDef().getFaultElementName())
                  && AeUtil.compareObjects(mCatchDef.getFaultMessageType(), otherSpec.getCatchDef().getFaultMessageType());
         }
         return isEqual;
      }

      /**
       * @see java.lang.Object#hashCode()
       */
      public int hashCode()
      {
         StringBuffer sb = new StringBuffer();
         sb.append(mCatchDef.getFaultName());
         sb.append(mCatchDef.getFaultElementName());
         sb.append(mCatchDef.getFaultMessageType());
         return sb.toString().hashCode();
      }

      /**
       * @return Returns the catchDef.
       */
      public AeCatchDef getCatchDef()
      {
         return mCatchDef;
      }

      /**
       * @param aCatchDef The catchDef to set.
       */
      public void setCatchDef(AeCatchDef aCatchDef)
      {
         mCatchDef = aCatchDef;
      }

      /**
       * @return Returns the catchSpec.
       */
      public AeCatchSpec getCatchSpec()
      {
         return mCatchSpec;
      }

      /**
       * @param aCatchSpec The catchSpec to set.
       */
      public void setCatchSpec(AeCatchSpec aCatchSpec)
      {
         mCatchSpec = aCatchSpec;
      }
   }

   /**
    * Getter for the faultHandlers def
    */
   protected AeFaultHandlersDef getDef()
   {
      return (AeFaultHandlersDef) getDefinition();
   }
   
   /**
    * Returns true if the fault is handled by a catch or catchAll in this faultHandlers model
    * 
    * @param aFaultTypeInfo
    */
   public boolean isFaultHandled(IAeFaultTypeInfo aFaultTypeInfo)
   {
      if (getDef().getCatchAllDef() != null)
      {
         return true;
      }
      IAeFaultMatchingStrategy strategy = AeFaultMatchingStrategyFactory.getInstance(getBpelNamespace());
      IAeContextWSDLProvider wsdlProvider = getProcessValidator().getValidationContext().getContextWSDLProvider();
      List catches = getChildren(AeBaseCatchValidator.class);
      return strategy.selectMatchingCatch(wsdlProvider, catches.iterator(), aFaultTypeInfo) != null;
   }
}
 