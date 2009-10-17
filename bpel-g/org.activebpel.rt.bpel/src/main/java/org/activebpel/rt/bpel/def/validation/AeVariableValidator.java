//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/AeVariableValidator.java,v 1.13 2008/03/20 16:01:32 dvilaverde Exp $
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
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.io.readers.def.IAeFromStrategyNames;
import org.activebpel.rt.bpel.def.validation.variable.AeComplexTypeQueryUsage;
import org.activebpel.rt.bpel.def.validation.variable.AeElementPropertyUsage;
import org.activebpel.rt.bpel.def.validation.variable.AeElementQueryUsage;
import org.activebpel.rt.bpel.def.validation.variable.AeMessagePartQueryUsage;
import org.activebpel.rt.bpel.def.validation.variable.AeMessagePartUsage;
import org.activebpel.rt.bpel.def.validation.variable.AeMessagePropertyUsage;
import org.activebpel.rt.bpel.def.validation.variable.AeTypePropertyUsage;
import org.activebpel.rt.bpel.def.validation.variable.AeVariableUsage;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;

/**
 * Model for validating variables.
 */
public class AeVariableValidator extends AeBaseValidator
{
   /** reference to the WSDL def that defines the variable's message or type */
   private AeBPELExtendedWSDLDef mWsdlDef;
   /** list of variable usage objects that record a variable's usage by a wsio activity, correlation, or assign */
   private List mVariableUsers = new LinkedList();
   /** flag that indicates a variable is referenced by another model but without requiring any additional validation like the usage list */
   private boolean mReferenced;
   private BitSet mVariableUsage = new BitSet();

   private boolean mAttemptedToResolveWSDL = false;
   
   /** Constants for variable usage */
   
   // BitSet Position for variables used in the from-spec of an assignment or expressions 
   public final static int VARIABLE_READ_FROM_SPEC = 1;
   // BitSet Position for variables used in read of a operations : receive, onMessage and invoke 
   public final static int VARIABLE_READ_WSIO = 2;
   public final static int VARIABLE_READ_VALIDATION = 3;
   public final static int VARIABLE_READ_THROW = 4;
   public final static int VARIABLE_READ_OTHER = 5;
   public final static int VARIABLE_READ_IMPLICIT = 6;

   public final static int VARIABLE_WRITE_TO_SPEC = 7;
   public final static int VARIABLE_WRITE_INIT = 8;
   public final static int VARIABLE_WRITE_WSIO = 9;
   public final static int VARIABLE_WRITE_IMPLICIT = 10;
   
   // This variable is used only to support getVariableValidator() method singnature and not used to generate usage warnings
   public final static int VARIABLE_IMPLICIT = 11;
   
   /**
    * All of the read bits are set. Used for doing a bitwise and on usage.
    */
   public static final BitSet ALL_READS = new BitSet();
   static
   {
      ALL_READS.set(VARIABLE_READ_FROM_SPEC);
      ALL_READS.set(VARIABLE_READ_IMPLICIT);
      ALL_READS.set(VARIABLE_READ_OTHER);
      ALL_READS.set(VARIABLE_READ_THROW);
      ALL_READS.set(VARIABLE_READ_VALIDATION);
      ALL_READS.set(VARIABLE_READ_WSIO);
   }

   /**
    * All of the write bits are set. Used for doing a bitwise and on usage.
    */
   public static final BitSet ALL_WRITES = new BitSet();
   static
   {
      ALL_WRITES.set(VARIABLE_WRITE_IMPLICIT);
      ALL_WRITES.set(VARIABLE_WRITE_INIT);
      ALL_WRITES.set(VARIABLE_WRITE_TO_SPEC);
      ALL_WRITES.set(VARIABLE_WRITE_WSIO);
   }

   /**
    * WRITE IO bits are set. Used for doing a bitwise and on usage.
    */
   public static final BitSet WRITE_IO = new BitSet();
   static
   {
      WRITE_IO.set(VARIABLE_WRITE_INIT);
      WRITE_IO.set(VARIABLE_WRITE_TO_SPEC);
      WRITE_IO.set(VARIABLE_WRITE_WSIO);
   }
   
   /**
    * WRITE WSIO bit is set. Used for doing a bitwise and on usage.
    */
   public static final BitSet WRITE_WSIO_IMPLICIT = new BitSet();
   static
   {
      WRITE_WSIO_IMPLICIT.set(VARIABLE_WRITE_WSIO);
      WRITE_WSIO_IMPLICIT.set(VARIABLE_WRITE_IMPLICIT);
   }
   
   /**
    * ctor
    * @param aDef
    */
   public AeVariableValidator(AeVariableDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Returns true if the passed variable is the same type as this variable.
    * @param aModel
    */
   public boolean isSameType(AeVariableValidator aModel)
   {
      return AeUtil.compareObjects(getDef().getMessageType(), aModel.getDef().getMessageType()) &&
             AeUtil.compareObjects(getDef().getElement(), aModel.getDef().getElement()) &&
             AeUtil.compareObjects(getDef().getType(), aModel.getDef().getType());
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      if (!isReferenced() && !getDef().isImplicit())
      {
         getReporter().reportProblem( BPEL_VARIABLE_USAGE_CODE, WARNING_VARIABLE_NOT_USED,  new String[] { getDef().getName() }, getDef() );
      }

      validateInitialization();
      validateVariableName(getDef().getName(), getDefinition());
      validateVariableDefinition();
      validateVariableUsage();
      
      if (isReferenced() && !getDef().isImplicit())
      {
         for(Iterator iter = buildVariableUsageWarnings().iterator(); iter.hasNext();)
         {
            getReporter().reportProblem(BPEL_VARIABLE_USAGE_CODE, (String) iter.next(),  new String[] { getDef().getName() }, getDef() );
         }
      }
   }
   
   /**
    * Validates that the variable initialization is correct. Only applies to message style variables.
    */
   protected void validateInitialization()
   {
      if (getDef().getFromDef() != null && getDef().isMessageType())
      {
         // report an error if they're trying to init the variable from something other than another message variable
         if (getDef().getFromDef().getStrategyKey() != null)
         {
            if (!IAeFromStrategyNames.FROM_VARIABLE_MESSAGE.equals(getDef().getFromDef().getStrategyKey().getStrategyName()))
            {
               getReporter().reportProblem(BPEL_INVALID_MESSAGE_VARIABLE_INIT_CODE, 
                                          WARNING_INVALID_MESSAGE_VARIABLE_INIT, 
                                          new String[] {getDef().getName()}, getDef());
            }
            else
            {
               // it's being init'd from another message variable, make sure the type is compatible
               String variableName = getDef().getFromDef().getVariable();
               AeVariableValidator otherValidator = getVariableValidator(variableName, null, false, AeVariableValidator.VARIABLE_READ_FROM_SPEC);
               if (!AeUtil.compareObjects(getDef().getMessageType(), otherValidator.getDef().getMessageType()))
               {
                  getReporter().reportProblem(BPEL_INVALID_MESSAGE_VARIABLE_INIT_CODE, 
                                             WARNING_INVALID_MESSAGE_VARIABLE_INIT, 
                                             new String[] {getDef().getName()}, getDef());
               }
            }
         }
      }
      if (getDef().getFromDef() != null)
         getVariableValidator(getDef().getName(), null, false, AeVariableValidator.VARIABLE_WRITE_INIT);
   }

   /**
    * validates we can find a definition for the given variable from the WSDL provider.
    * This applies to complex types, elements, and WSDL message variables. 
    */
   protected void validateVariableDefinition()
   {
      // verify the element exists
      if(getDef().isElement())
      {
         validateElement();
      }
      // else verify the type exists
      else if(getDef().isType())
      {
         validateType();
      }
      // else message so verify the message exists
      else
      {
         validateMessageType();
      }
   }

   /**
    * Validates the Element.
    */
   protected void validateElement()
   {
      if ( getWsdlDef() == null )
      {
         addTypeNotFoundError(ERROR_ELEMENT_SPEC_NOT_FOUND, getDef().getElement());
      }
      if (getDef().isType() || getDef().isMessageType())
      {
         getReporter().reportProblem(BPEL_INVALID_VARIABLE_TYPE_CODE, AeMessages.getString("AeVariableModel.InvalidVariableTypeError"), //$NON-NLS-1$
               null, getDef());
      }
   }

   /**
    * Validates the type.
    */
   protected void validateType()
   {
      // don't need to locate def for simple types
      // and potentially other well known types
      QName schemaQName = getDef().getType();
      if( !IAeBPELConstants.DEFAULT_SCHEMA_NS.equals( schemaQName.getNamespaceURI() ) )
      {
         if ( getWsdlDef() == null )
         {
            addTypeNotFoundError(ERROR_TYPE_SPEC_NOT_FOUND, getDef().getType());
         }
      }
      if (getDef().isElement() || getDef().isMessageType())
      {
         getReporter().reportProblem(BPEL_INVALID_VARIABLE_TYPE_CODE, AeMessages.getString("AeVariableModel.InvalidVariableTypeError"), //$NON-NLS-1$
               null, getDef());
      }
   }

   /**
    * Validates the message type.
    */
   protected void validateMessageType()
   {
      // Ensure that the Variable has an assigned MessageType
      //  and that it can be resolved.
      //
      QName msgType = getDef().getMessageType();
      if (msgType == null || EMPTY_QNAME.equals(msgType))
      {
         getReporter().reportProblem(BPEL_VAR_HAS_NO_TYPE_CODE, ERROR_VAR_HAS_NO_TYPE, new String[] { getDef().getName() }, getDef());
      }
      else
      {
         Message message = getWsdlDef() == null ? null : getWsdlDef().getMessage(getDef().getMessageType());
         if ( message == null || message.isUndefined())
         {
            addTypeNotFoundError(ERROR_MSG_SPEC_NOT_FOUND, getDef().getMessageType());
         }
         else
         {
            for (Iterator iter=getWsdlDef().getMessageParts(getDef().getMessageType()); iter.hasNext();)
            {
               Part part = (Part)iter.next();
               try
               {
                  getDef().addPartTypeInfo(part, getWsdlDef());
               }
               catch (AeException e)
               {
                  getReporter().reportProblem( BPEL_DISCOVERING_PART_TYPE_SPECS_CODE, 
                                          ERROR_DISCOVERING_PART_TYPE_SPECS,
                                          new String[] { part.getName(),
                                                         getNSPrefix( getDef().getMessageType().getNamespaceURI()),
                                                         getDef().getMessageType().getLocalPart(),
                                                         e.getMessage()},
                                          getDef() );
               }
            }
         }
      }
      if (getDef().isType() || getDef().isElement())
      {
         getReporter().reportProblem(BPEL_INVALID_VARIABLE_TYPE_CODE, AeMessages.getString("AeVariableModel.InvalidVariableTypeError"), //$NON-NLS-1$
               null, getDef());
      }
   }
   
   /**
    * Gets the QName of the variable's type, returning either the message, element, or type type.
    */
   public QName getType()
   {
      if (getDef().isMessageType())
         return getDef().getMessageType();
      else if (getDef().isElement())
         return getDef().getElement();
      else
         return getDef().getType();
   }

   /**
    * pass through to the def to get the name
    */
   public String getName()
   {
      return getDef().getName();
   }
   
   /**
    * Getter for the def
    */
   public AeVariableDef getDef()
   {
      return (AeVariableDef) getDefinition();
   }
   
   /**
    * Records the models usage of the variable
    * @param aModel
    * @param aPart
    * @param aQuery
    * @param aProperty
    */
   public void addUsage(IAeValidator aModel, String aPart, String aQuery, QName aProperty)
   {
      // message + part
      if (AeUtil.notNullOrEmpty(aPart) && AeUtil.isNullOrEmpty(aQuery))
      {
         mVariableUsers.add(new AeMessagePartUsage(this, aModel, aPart));
      }
      // message + part + query
      else if (AeUtil.notNullOrEmpty(aPart) && AeUtil.notNullOrEmpty(aQuery))
      {
         mVariableUsers.add(new AeMessagePartQueryUsage(this, aModel, aPart, aQuery));
      }
      // var (elem, type, message) + property
      else if (aProperty != null)
      {
         if (getDef().isElement())
         {
            mVariableUsers.add(new AeElementPropertyUsage(this, aModel, aProperty));
         }
         else if (getDef().isType())
         {
            mVariableUsers.add(new AeTypePropertyUsage(this, aModel, aProperty));
         }
         else
         {
            mVariableUsers.add(new AeMessagePropertyUsage(this, aModel, aProperty));
         }
      }
      // element + query or complex type + query
      else if (AeUtil.notNullOrEmpty(aQuery))
      {
         if (getDef().isType())
         {
            mVariableUsers.add(new AeComplexTypeQueryUsage(this, aModel, aQuery));
         }
         else
         {
            mVariableUsers.add(new AeElementQueryUsage(this, aModel, aQuery));
         }
      }
   }
   
   /**
    * Returns true if this variable is referenced
    */
   public boolean isReferenced()
   {
      return mReferenced || mVariableUsers.size() > 0;
   }
   
   /**
    * Returns a List of usage warnings   
    */
   public List buildVariableUsageWarnings()
   {
      List warnings = new ArrayList();
      // Get the BitSet for all types of reads
      BitSet readSet = and( mVariableUsage, ALL_READS);
      // Get the BitSet for all types of writes
      BitSet writeSet = and(mVariableUsage, ALL_WRITES);
      // Get the BitSet for writes related to WSIO, toSpec and Initialization  
      BitSet writeIO = and(mVariableUsage, WRITE_IO);
      // Get the BitSet for WSIO and implicit writes 
      BitSet writeWSIOImplicit = and(mVariableUsage, WRITE_WSIO_IMPLICIT);
      // If no read bits are set to true  and not a WSIO write then true else false
      boolean neverRead = readSet.cardinality() == 0 ? true : false;
      // If no write bits are set to true then true else false
      boolean neverWrittenTo = writeSet.cardinality() == 0 ? true : false;
      // If WSIO read is set and the variable is not initialized(during declaration or thru copy) then true else false
      boolean noWriteForIORead = ((mVariableUsage.get(2)) && (writeIO.cardinality() == 0)) ? true : false;
      // If usage is only write WSIO then false and else true
      boolean notWriteWSIOImplicit = writeWSIOImplicit.cardinality() == 0 ? true : false;
         
      if ( (neverRead) && (notWriteWSIOImplicit) ) 
         warnings.add(WARNING_VARIABLE_NOT_READ);
      
      if (noWriteForIORead)  
         if (getDef().getMessageParts().getPartsCount() == 0)
            return warnings;
         else
            warnings.add(WARNING_VARIABLE_NO_INIT);
      else if (neverWrittenTo)
         warnings.add(WARNING_VARIABLE_NOT_WRITTEN_TO);
      
      return warnings;
   }
   
   private BitSet and(BitSet aBitSet, BitSet aMask)
   {
      BitSet set = (BitSet)aBitSet.clone();
      set.and(aMask);
      return set;
   }
   
   /**
    * sets the reference flag
    */
   public void addReference()
   {
      mReferenced = true;
   }
   
   /**
    * sets the read reference flag
    * @patam aUsageType : Position indicating the type of variable usage
    */
   public void addVariableUsage(int aUsageType)
   {
      mVariableUsage.set(aUsageType);
   }
   
   /**
    * Getter for the WSDL def
    */
   public AeBPELExtendedWSDLDef getWsdlDef()
   {
      if(mWsdlDef == null && !mAttemptedToResolveWSDL)
      {         
         mAttemptedToResolveWSDL = true;
         
         // set wsdlDef for element
         if(getDef().isElement())
         {
            setWsdlDef(AeWSDLDefHelper.getWSDLDefinitionForElement( getValidationContext().getContextWSDLProvider(), getDef().getElement()));
         }
         // set wsdlDef for type
         else if(getDef().isType())
         {
            setWsdlDef(AeWSDLDefHelper.getWSDLDefinitionForType( getValidationContext().getContextWSDLProvider(), getDef().getType() ));
         }
         // set wsdlDef for message type
         else
         {
            setWsdlDef(AeWSDLDefHelper.getWSDLDefinitionForMsg( getValidationContext().getContextWSDLProvider(), getDef().getMessageType() ));
         }                          
      }
      
      return mWsdlDef;
   }
   
   /**
    * Setter for the WSDL def
    * @param aDef
    */
   protected void setWsdlDef(AeBPELExtendedWSDLDef aDef)
   {
      mWsdlDef = aDef;
   }
   
   
   
   /**
    * Validates all of the recorded users of the variable
    */
   protected void validateVariableUsage()
   {
      for(Iterator it=mVariableUsers.iterator(); it.hasNext();)
      {
         AeVariableUsage usage = (AeVariableUsage) it.next();
         usage.validate();
      }
   }
   
}
 