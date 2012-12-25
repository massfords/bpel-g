package org.activebpel.rt.bpel.def.validation.variable;

import java.util.Iterator;

import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.bpel.def.validation.IAeValidationProblemCodes;
import org.activebpel.rt.bpel.def.validation.IAeValidator;


/**
 * Validates that the variable used is a message variable and that it contains
 * the part specified
 */
public class AeMessagePartUsage extends AeVariableUsage
{
   /** name of the part being used */
   private String mPart;

   /**
    * ctor
    * @param aVariableValidator - the variable validator
    * @param aValidator - validator using the variable
    * @param aPart - name of the part being used
    */
   public AeMessagePartUsage(AeVariableValidator aVariableValidator, IAeValidator aValidator, String aPart)
   {
      super(aVariableValidator, aValidator);
      setPart(aPart);
   }

   /**
    * Valdiates:
    * 1. variable is a message variable
    * 2. definition for message can be found
    * 3. part exists on message
    * @see org.activebpel.rt.bpel.def.validation.variable.AeVariableUsage#validate()
    */
   public boolean validate()
   {
      if (getVariableValidator().getDef().getMessageType() == null)
      {
         QName type = getVariableValidator().getType() == null? AeVariableValidator.EMPTY_QNAME : getVariableValidator().getType();
         
         getVariableValidator().getReporter().reportProblem( IAeValidationProblemCodes.BPEL_MSG_PART_VAR_TYPE_MISMATCH_CODE,
                              AeVariableValidator.ERROR_VAR_TYPE_MISMATCH_MESSAGE,
                              new String[] { getVariableValidator().getDef().getName(),
                              getVariableValidator().getNSPrefix( type.getNamespaceURI()),
                              type.getLocalPart(),
                              getPart() },
               getValidator().getDefinition());
         return false;
      }
      
      if (getVariableValidator().getWsdlDef() == null)
      {
         getVariableValidator().addTypeNotFoundError(AeVariableValidator.ERROR_MSG_SPEC_NOT_FOUND, getVariableValidator().getDef().getMessageType(), getValidator().getDefinition());
         return false;
      }
      
      if (findPart() == null)
      {
         // fixme allow the subclass to change this error since it should indicate whether the error stems from a std message/part usage or through a property alias
         getVariableValidator().getReporter().reportProblem( IAeValidationProblemCodes.BPEL_MSG_PART_VAR_PART_NOT_FOUND_CODE,
                              AeVariableValidator.ERROR_VAR_PART_NOT_FOUND,
                              new String[] { getPart(),
                              getVariableValidator().getNSPrefix( getVariableValidator().getDef().getMessageType().getNamespaceURI()),
                              getVariableValidator().getDef().getMessageType().getLocalPart()
                               },
               getValidator().getDefinition());
      }

      return true;
   }

   /**
    * Finds the part in the given WSDL.
    */
   protected Part findPart()
   {
      for ( Iterator iterParts = getVariableValidator().getWsdlDef().getMessageParts( getVariableValidator().getDef().getMessageType() ) ; iterParts.hasNext() ; )
      {
         Part part = (Part)iterParts.next();
         if ( part.getName().equals( getPart() ))
         {
            return part;
         }
      }
      return null;
   }
   
   /**
    * Getter for the part name
    */
   protected String getPart()
   {
      return mPart;
   }
   
   /**
    * Setter for the part name
    * @param aPart
    */
   protected void setPart(String aPart)
   {
      mPart = aPart;
   }
}