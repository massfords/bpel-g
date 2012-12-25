package org.activebpel.rt.bpel.def.validation.variable;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.bpel.def.validation.IAeValidationProblemCodes;
import org.activebpel.rt.bpel.def.validation.IAeValidator;
import org.activebpel.rt.xml.IAeNamespaceContext;

/**
 * Validates an element and query usage
 */
public class AeElementQueryUsage extends AeVariableUsage
{
   /** query used on the element */
   private String mQuery;

   /**
    * ctor
    * @param aVariableValidator - the variable validator
    * @param aValidator - validator that references the variable
    * @param aQuery - query used on the element
    */
   public AeElementQueryUsage(AeVariableValidator aVariableValidator, IAeValidator aValidator, String aQuery)
   {
      super(aVariableValidator, aValidator);
      setQuery(aQuery);
   }

   /**
    * Validates:
    * 1. variable is an element
    * 2. element can be resolved by WSDL provider
    * 3. xpath query is valid with the element
    * 
    * @see org.activebpel.rt.bpel.def.validation.variable.AeVariableUsage#validate()
    */
   public boolean validate()
   {
      if (getElement() == null)
      {
         QName type = getVariableValidator().getType() == null? AeVariableValidator.EMPTY_QNAME : getVariableValidator().getType();
         
         getVariableValidator().getReporter().reportProblem( IAeValidationProblemCodes.BPEL_ELEMENT_VAR_TYPE_MISMATCH_CODE,
                              AeVariableValidator.ERROR_VAR_TYPE_MISMATCH_ELEMENT,
                              new String[] { getVariableValidator().getDef().getName(),
                              getVariableValidator().getNSPrefix( type.getNamespaceURI()),
                              type.getLocalPart()},
               getValidator().getDefinition());
         return false;
      }
      
      if (getVariableValidator().getWsdlDef() == null)
      {
         getVariableValidator().addTypeNotFoundError(AeVariableValidator.ERROR_ELEMENT_SPEC_NOT_FOUND, getElement(), getValidator().getDefinition());
         return false;
      }

      return validateQuery();
   }
   
   /**
    * Validates the query and returns true/false.
    */
   protected boolean validateQuery()
   {
      try
      {
         IAeNamespaceContext nsContext = createNamespaceContextForQuery();
         getVariableValidator().getProcessValidator().getXPathQueryValidator().validate(nsContext,
               getQuery(), getVariableValidator().getDef().getXMLType(), getElement());
         return true;
      }
      catch (Exception ex)
      {
         getVariableValidator().getReporter().reportProblem( IAeValidationProblemCodes.BPEL_ELEMENT_INVALID_XPATH_CODE,
               AeVariableValidator.ERROR_INVALID_XPATH,
               new String[] { getQuery(), ex.getLocalizedMessage() },
               getValidator().getDefinition());
         return true;
      }
   }

   /**
    * Getter for the query
    */
   protected String getQuery()
   {
      return mQuery;
   }
   
   /**
    * Setter for the query.
    * 
    * @param aQuery
    */
   protected void setQuery(String aQuery)
   {
      mQuery = aQuery;
   }
   
   /**
    * Getter for the element
    */
   protected QName getElement()
   {
      return getVariableValidator().getDef().getElement();
   }
}
