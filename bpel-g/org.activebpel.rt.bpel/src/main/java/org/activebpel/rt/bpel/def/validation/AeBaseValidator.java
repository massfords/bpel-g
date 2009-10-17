//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/AeBaseValidator.java,v 1.16 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation; 

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AeActivityPartnerLinkBaseDef;
import org.activebpel.rt.bpel.def.AeNamedDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeBaseScopeValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeCompensationHandlerValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeCorrelationSetValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeFaultHandlersValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AePartnerLinkValidator;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeTerminationHandlerValidator;
import org.activebpel.rt.bpel.def.validation.expressions.IAeExpressionModelValidator;
import org.activebpel.rt.bpel.def.validation.extensions.AeExtensionValidator;
import org.activebpel.rt.bpel.def.validation.extensions.AeExtensionsValidator;
import org.activebpel.rt.bpel.def.validation.process.AeProcessValidator;
import org.activebpel.rt.bpel.impl.AeFaultFactory;
import org.activebpel.rt.bpel.impl.IAeFaultFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeXmlDefUtil;
import org.activebpel.rt.xml.def.IAeExtensionObject;

/**
 * Base class for all validation models. Provides accessors to the def, child models, parent model, and
 * root process model. 
 */
public class AeBaseValidator implements IAeValidationDefs, IAeValidator, IAeValidationProblemCodes
{
   /** def that is being validated */
   private AeBaseXmlDef mDef;
   /** 
    * list of all child models for this model. This list may have mixed types of child models depending 
    * on the specific instance of this model 
    */
   private List mChildren;
   /** reference to our parent */
   private AeBaseValidator mParent;
   /** reference to the root process model */
   private AeProcessValidator mProcessModel;
   
   /**
    * Ctor takes the def being validated
    */
   protected AeBaseValidator(AeBaseXmlDef aBaseDef)
   {
      setDefinition(aBaseDef);
   }
   
   /**
    * Setter for the parent reference
    * @param aParent
    */
   public void setParent(AeBaseValidator aParent)
   {
      mParent = aParent;
      mProcessModel = aParent.getProcessValidator();
   }
   
   /**
    * Getter for the parent reference
    */
   public AeBaseValidator getParent()
   {
      return mParent;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeValidator#getDefinition()
    */
   public AeBaseXmlDef getDefinition()
   {
      return mDef;
   }
   
   /**
    * Setter for the definition.
    * @param aDef
    */
   public void setDefinition(AeBaseXmlDef aDef)
   {
      mDef = aDef;
   }
   
   /**
    * Getter for the factory
    */
   public IAeFaultFactory getFaultFactory()
   {
      return AeFaultFactory.getFactory( getBpelNamespace() );
   }   
   
   /**
    * Gets the BPEL namespace for the process.
    */
   public String getBpelNamespace()
   {
      return getProcessValidator().getProcessDef().getNamespace();
   }
   
   /**
    * Adds the model as a child, setting the parent reference
    * @param aModel
    */
   public void add(AeBaseValidator aModel)
   {
      aModel.setParent(this);
      getChildren().add(aModel);
   }
   
   /**
    * Getter for the children list, does lazy instantiation
    */
   private List getChildren()
   {
      if (mChildren == null)
         mChildren = new ArrayList();
      return mChildren;
   }
   
   /**
    * Tests to see if we have children, used to avoid lazy instantiation of list
    */
   private boolean hasChildren()
   {
      return mChildren != null;
   }
   
   /**
    * Returns all of the children that can be assigned to the class or interface passed in
    * @param aClass
    */
   public List getChildren(Class aClass)
   {
      if (hasChildren())
      {
         ArrayList list = new ArrayList();
         for (int i =0; i<getChildren().size(); i++)
         {
            Object obj = getChildren().get(i);
            if (aClass.isAssignableFrom(obj.getClass()))
            {
               list.add(obj);
            }
         }
         return list;
      }
      else
      {
         return Collections.EMPTY_LIST;
      }
   }
   
   /**
    * Gets the single child that can be assigned to the class or interface passed in.
    * If there are more than one then we report an error because we
    * were only expecting to find 1 or 0.
    * @param aClass
    */
   protected Object getChild(Class aClass)
   {
      List list = getChildren(aClass);
      if (list.size() == 1)
         return list.get(0);
      else if (list.size() > 1)
      {
         getReporter().reportProblem(BPEL_MULTIPLE_CHILDREN_FOUND_CODE, ERROR_MULTIPLE_CHILDREN_FOUND,
               new String[] { aClass.getName() },
               getDefinition() );
         return list.get(0);
      }
      else return null;
   }
   
   /**
    * No validation done here other than to walk the children and call their validate().
    * @see org.activebpel.rt.bpel.def.validation.IAeValidator#validate()
    */
   public void validate()
   {
      if (hasChildren())
      {
         for (int i =0; i<getChildren().size(); i++)
         {
            AeBaseValidator model = (AeBaseValidator) getChildren().get(i);
            model.validate();
         }
      }
   }

   /**
    * Process the extension validator.  If it is null, then we are using an extension without
    * declaring it.  Otherwise, add ourselves as a "user" of the extension.
    * 
    * @param aExtensionValidator
    * @param aIsUnderstood
    * @param aExtensionNamespaceURI
    */
   protected void processExtensionValidator(AeExtensionValidator aExtensionValidator, boolean aIsUnderstood,
         String aExtensionNamespaceURI)
   {
      if (aExtensionValidator == null)
      {
         getReporter().reportProblem(BPEL_UNDECLARED_EXTENSION_CODE, ERROR_UNDECLARED_EXTENSION, new String[] { aExtensionNamespaceURI },
               getDefinition());
      }
      else
      {
         aExtensionValidator.addUsage(this, aIsUnderstood);
      }
   }
   
   /**
    * Returns the extension validator object for the given extension namespace.
    * 
    * @param aNamespace
    */
   protected AeExtensionValidator findExtensionValidator(String aNamespace)
   {
      AeExtensionValidator extensionValidator = null;

      AeExtensionsValidator extensionsValidator = getProcessValidator().getExtensionsValidator();
      if (extensionsValidator != null)
      {
         List extensionValidators = extensionsValidator.getChildren(AeExtensionValidator.class);
         for (Iterator iter = extensionValidators.iterator(); iter.hasNext(); )
         {
            AeExtensionValidator validator = (AeExtensionValidator) iter.next();
            if (AeUtil.compareObjects(aNamespace, validator.getNamespace()))
            {
               extensionValidator = validator;
            }
         }
      }
      
      return extensionValidator;
   }

   /**
    * Convenience method for getting the validation context from the process model
    */
   public IAeValidationContext getValidationContext()
   {
      return getProcessValidator().getValidationContext();
   }
   
   /**
    * Convenience method for getting the error reporter from the validation context
    */
   public IAeValidationProblemReporter getReporter()
   {
      return getValidationContext().getErrorReporter();
   }
   
   /**
    * Returns true if this model has an anscestor that is assignable to the given
    * class or interface. This is used to test that the model is enclosed within
    * an acceptable construct. For example, a compensate must be nested within a
    * FCT handler.
    * @param aClass
    */
   protected boolean enclosedWithinDef(Class aClass)
   {
      return AeXmlDefUtil.isParentedByType(getDefinition(), aClass);
   }
   
   /**
    * Convenience getter for the process def
    */
   protected AeProcessDef getProcessDef()
   {
      return (AeProcessDef) getProcessValidator().getProcessDef();
   }
   
   /**
    * Convenience getter for the process model
    */
   public AeProcessValidator getProcessValidator()
   {
      return mProcessModel;
   }
   
   /**
    * Gets the anscestor activity model which may be our direct parent or some other
    * activity higher up our anscestor path.
    */
   protected AeActivityValidator getParentActivityModel()
   {
      return (AeActivityValidator) getAnscestor(AeActivityValidator.class);
   }
   
   /**
    * Returns the first anscestor that can be assigned to the given class or interface
    * @param aClass
    */
   public AeBaseValidator getAnscestor(Class aClass)
   {
      AeBaseValidator parent = getParent();
      while(parent != null && !aClass.isAssignableFrom(parent.getClass()))
         parent = parent.getParent();
      return parent;
   }
   
   /**
    * Gets a reference to the AeFaultHandlersModel that is currently in scope and
    * capable of catching any faults thrown from this model. This code accounts
    * for the fact that this model may itself by nested within a FCT handler and
    * therefore skips over the first scope encountered in those cases.
    */
   public AeFaultHandlersValidator getScopedFaultHandlersValidator()
   {
      AeFaultHandlersValidator faultHandlersModel = null;
      AeBaseValidator context = this;
      boolean skip = false;
      while(context != null && faultHandlersModel == null)
      {
         if (context instanceof AeFaultHandlersValidator || context instanceof AeCompensationHandlerValidator || context instanceof AeTerminationHandlerValidator)
         {
            skip = true;
         }
         else if (context instanceof AeBaseScopeValidator)
         {
            AeBaseScopeValidator scope = (AeBaseScopeValidator) context;
            if (skip)
            {
               skip = false;
            }
            else 
            {
               faultHandlersModel = scope.getFaultHandlersModel(); 
            }
         }
         context = context.getParent();
      }
      return faultHandlersModel;
   }

   /**
    * Convenience method that returns true if the value is null, empty, or "undefined".
    * Undefined is a value from our designer tool that lists the value as "(none)"
    *
    * @param aValue
    */
   protected boolean isUndefined(String aValue)
   {
      // TODO (MF) the designer shouldn't write bpel with "(none)". It should use
      // nulls in the object model to indicate when values haven't been set and simply
      // write nothing for the attributes.
      return AeUtil.isNullOrEmpty(aValue) || FIELD_UNDEFINED.equals(aValue);
   }
   
   /**
    * Gets a reference to the partnerLinkModel with the given name. 
    * @param aName - name of the plink, records error if undefined
    * @param aRecordReference - if true, we'll record a reference to the plink if found or report error if not found
    */
   protected AePartnerLinkValidator getPartnerLinkValidator(String aName, boolean aRecordReference)
   {
      if (isUndefined(aName))
      {
         getReporter().reportProblem( BPEL_FIELD_MISSING_CODE, ERROR_FIELD_MISSING,
               new String[] { AeActivityPartnerLinkBaseDef.TAG_PARTNER_LINK },
               getDefinition());
         return null;
      }
      
      AePartnerLinkValidator plinkValidator = null;
      if (getParent() != null)
         plinkValidator = getParent().getPartnerLinkValidator(aName, false);
      
      if (aRecordReference)
      {
         if (plinkValidator == null)
         {
            // error - partner link name not resolved.
            //
            getReporter().reportProblem( BPEL_PARTNER_LINK_NOT_FOUND_CODE, ERROR_PARTNER_LINK_NOT_FOUND,
                                    new String[] { aName },
                                    getDefinition() );
         }
         else
         {
            plinkValidator.addReference();
         }
         
      }
      
      return plinkValidator;
   }

   /**
    * Gets reference to the variable model with the given name.
    * 
    * @param aName - name of the variable, will report error if undefined and not abstract process
    * @param aFieldName - name of the field, something like inputVariable, outputVariable, or variable or null if no error should be reported if not found
    * @param aRecordReference - if true then we record a reference on the variable if found or report error if not found
    * @param aMode - Indicates read/write usage of variable
    */
   protected final AeVariableValidator getVariableValidator(String aName, String aFieldName,
         boolean aRecordReference, int aMode)
   {
      return getVariableValidator(aName, aFieldName, aRecordReference, aMode, getDefinition());
   }

   /**
    * Gets reference to the variable model with the given name.
    * 
    * @param aName - name of the variable, will report error if undefined and not abstract process
    * @param aFieldName - name of the field, something like inputVariable, outputVariable, or variable or null if no error should be reported if not found
    * @param aRecordReference - if true then we record a reference on the variable if found or report error if not found
    * @param aMode - Indicates read/write usage of variable
    * @param aDef - The def to use when reporting errors
    */
   protected AeVariableValidator getVariableValidator(String aName, String aFieldName,
         boolean aRecordReference, int aMode, AeBaseXmlDef aDef)
   {
      if ( isUndefined(aName) )
      {
         // if this is not an abstract process then it is an error
         // fixme should handle abstract process differently in 2.0, use a diff model impl
         if ( aFieldName != null && !getProcessDef().isAbstractProcess() )
         {
            // Missing input variable.
            getReporter().reportProblem(BPEL_FIELD_MISSING_CODE, ERROR_FIELD_MISSING, new String[] { aFieldName }, aDef);
         }
         return null;
      }
      
      AeVariableValidator variableModel = null;
      if (getParent() != null)
         variableModel = getParent().getVariableValidator(aName, aFieldName, false, aMode, aDef);
      
      if (variableModel != null)
      {
         variableModel.addVariableUsage(aMode);
      }
      
      if (aRecordReference)
      {
         if (variableModel == null)
         {
            // Never found the variable.
            getReporter().reportProblem(BPEL_VAR_NOT_FOUND_CODE, ERROR_VAR_NOT_FOUND, new String[] { aName }, aDef);
         }
         else
         {
            variableModel.addReference();
         }
      }
      return variableModel;
   }
   
   /**
    * Gets the correlationSet model with the given name
    * @param aName - name of the correlation set
    * @param aRecordReference - if true, we record a reference on the correlation set if found or report error if not
    */
   protected AeCorrelationSetValidator getCorrelationSetValidator(String aName, boolean aRecordReference)
   {
      AeCorrelationSetValidator corrSetModel = null;
      if (getParent() != null)
         corrSetModel = getParent().getCorrelationSetValidator(aName, false);
      
      if (aRecordReference)
      {
         if (corrSetModel == null)
         {
            // Never found the variable.
            //
            getReporter().reportProblem( BPEL_CORR_SET_NOT_FOUND_CODE, ERROR_CORR_SET_NOT_FOUND, new String[] { aName }, getDefinition() );
         }
         else
         {
            corrSetModel.addReference();
         }
      }
      return corrSetModel;
   }
   
   /**
    * Get the prefix for the namespace URI specified. Only used during error reporting
    *
    * @param aURI The namespace URI to search.
    *
    * @return String
    */
   public String getNSPrefix( String aURI )
   {
      // TODO (MF) carry over from prev validation code, look where used and possibly change
      if ( AeUtil.isNullOrEmpty( aURI ))
         return "{no namespace}"; //$NON-NLS-1$

      Set prefixes = getProcessDef().findPrefixesForNamespace(aURI);
      if (!prefixes.isEmpty())
      {
         return (String) prefixes.iterator().next();
      }
      
      return "{" + aURI + "}" ; //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * Convenience method for reporting that the given QName type wasn't found
    * @param aErrorMessage
    * @param aQName
    */
   protected void addTypeNotFoundError(String aErrorMessage, QName aQName)
   {
      addTypeNotFoundError(aErrorMessage, aQName, getDefinition());
   }
   
   /**
    * Convenience method for reporting that the given QName type wasn't found
    * @param aErrorMessage
    * @param aQName
    * @param aErrorDefinition
    */
   public void addTypeNotFoundError(String aErrorMessage, QName aQName, AeBaseXmlDef aErrorDefinition)
   {
      if ( aQName == null )
         aQName = IAeValidationDefs.EMPTY_QNAME;
      
      getReporter().reportProblem( BPEL_TYPE_NOT_FOUND_CODE, aErrorMessage,
                              new String[] { getNSPrefix(aQName.getNamespaceURI()),
                                             aQName.getLocalPart() },
                              aErrorDefinition );
   }
   
   /**
    * Validates that the name of the activity/def is a valid ncname
    * @param aRequiredFlag
    */
   protected void validateNCName(boolean aRequiredFlag)
   {
      AeNamedDef def = ((AeNamedDef)getDefinition());
      
      if (!aRequiredFlag && AeUtil.isNullOrEmpty(def.getName()))
      {
         return;
      }
      
      if (!AeXmlUtil.isValidNCName(def.getName()))
      {
         getReporter().reportProblem( BPEL_INVALID_NAME_CODE, ERROR_INVALID_NAME, new Object[] {def.getName()}, getDefinition() );
      }
   }
   
   /**
    * Validates that variables are NCNames and ws-bpel variables don't contain "." 
    * @param aVariableName
    * @param aDef
    */
   protected void validateVariableName(String aVariableName, AeBaseXmlDef aDef)
   {
      if (!AeXmlUtil.isValidNCName(aVariableName) || (isWSBPEL() && aVariableName.indexOf('.') != -1))
      {
         getReporter().reportProblem( BPEL_INVALID_NAME_CODE, ERROR_INVALID_NAME, new Object[] {aVariableName}, aDef );
      }
   }

   /**
    * Perform some additional validation with the help of an adapter.
    * 
    * @param aExtensionObject
    */
   protected void doExtensionObjectValidation(IAeExtensionObject aExtensionObject)
   {
      // Perform some additional validation with the help of an adapter.
      if (aExtensionObject != null)
      {
         IAeValidationAdapter validationAdapter = (IAeValidationAdapter) aExtensionObject.getAdapter(IAeValidationAdapter.class);
         if (validationAdapter != null)
         {
            // Perform custom validation specified by the adapter.
            validationAdapter.validate(getValidationContext());
            Set variableReferences = validationAdapter.getVariableUsage(getValidationContext());
            recordExtensionVariableUsage(variableReferences);
         }
         
         IAeExtensionUsageAdapter extensionUsageAdapter = (IAeExtensionUsageAdapter) aExtensionObject.getAdapter(IAeExtensionUsageAdapter.class);
         if (extensionUsageAdapter != null)
         {
            List requiredExtensions = extensionUsageAdapter.getRequiredExtensions();
            if (requiredExtensions != null)
            {
               for(Iterator it=requiredExtensions.iterator(); it.hasNext();)
               {
                  AeExtensionNamespaceInfo nsInfo = (AeExtensionNamespaceInfo) it.next();
                  AeExtensionValidator extensionValidator = findExtensionValidator(nsInfo.getNamespace());
                  processExtensionValidator(extensionValidator, nsInfo.isMustUnderstand(), nsInfo.getNamespace());
               }
            }
         }
         
      }
   }

   /**
    * Records variable usage from the given list of varible references
    * supplied by the validation adapter.
    * 
    * @param aVariableReferences
    */
   protected void recordExtensionVariableUsage(Set aVariableReferences)
   {
      for (Iterator iter = aVariableReferences.iterator(); iter.hasNext(); )
      {
         AeVariableReference varReference = (AeVariableReference) iter.next();
         getVariableValidator(varReference.getVariableName(), null, true, varReference.getMode(),
               varReference.getDef());
      }
   }

   /**
    * Getter for the optional port type flag
    */
   public boolean isPortTypeOptional()
   {
      return isWSBPEL();
   }

   /**
    * Returns true if we're WS-BPEL
    * 
    * fixme deprecate this method
    */
   protected boolean isWSBPEL()
   {
      return !getBpelNamespace().equals(IAeBPELConstants.BPWS_NAMESPACE_URI);
   }

   /**
    * Returns true if the expression def is null or empty
    * @param aExprDef
    */
   protected boolean isNullOrEmpty(IAeExpressionDef aExprDef)
   {
      return aExprDef == null || AeUtil.isNullOrEmpty(aExprDef.getExpression());
   }
   
   /**
    * Returns true if the expression validator is null or has an empty expression
    * @param aExpressionModelValidator
    */
   protected boolean isNullOrEmpty(IAeExpressionModelValidator aExpressionModelValidator)
   {
      return aExpressionModelValidator == null || isNullOrEmpty(aExpressionModelValidator.getExpressionDef());
   }
} 