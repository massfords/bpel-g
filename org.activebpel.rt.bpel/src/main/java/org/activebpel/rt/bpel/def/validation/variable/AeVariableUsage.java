package org.activebpel.rt.bpel.def.validation.variable;

import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.bpel.def.validation.IAeValidator;
import org.activebpel.rt.xml.IAeNamespaceContext;
import org.activebpel.rt.xml.def.AeBaseDefNamespaceContext;



/**
 * Base class for a variable usage, includes reference to the model
 * that is using the variable
 */
public abstract class AeVariableUsage
{
   /** The variable validator. */
   private AeVariableValidator mVariableValidator;
   /** Validator that is using the variable */
   private IAeValidator mValidator;

   /**
    * ctor
    * 
    * @param aVariableValidator
    * @param aValidator
    */
   protected AeVariableUsage(AeVariableValidator aVariableValidator, IAeValidator aValidator)
   {
      setVariableValidator(aVariableValidator);
      setValidator(aValidator);
   }

   /**
    * Getter for the validator
    */
   protected IAeValidator getValidator()
   {
      return mValidator;
   }
   
   /**
    * validates the usage
    */
   public boolean validate()
   {
      return true;
   }

   /**
    * @return Returns the varModel.
    */
   protected AeVariableValidator getVariableValidator()
   {
      return mVariableValidator;
   }

   /**
    * @param aVarModel The varModel to set.
    */
   protected void setVariableValidator(AeVariableValidator aVarModel)
   {
      mVariableValidator = aVarModel;
   }

   /**
    * @param aModel The model to set.
    */
   protected void setValidator(IAeValidator aModel)
   {
      mValidator = aModel;
   }
   
   /**
    * Returns a namespace context that uses the defs to resolve the namespace.
    * This should be overridden by any property alias based variable usage.
    */
   protected IAeNamespaceContext createNamespaceContextForQuery()
   {
      IAeNamespaceContext nsContext = new AeBaseDefNamespaceContext(getValidator().getDefinition());
      return nsContext;
   }
}
