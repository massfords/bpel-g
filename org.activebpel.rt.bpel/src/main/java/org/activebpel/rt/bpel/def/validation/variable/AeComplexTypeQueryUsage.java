package org.activebpel.rt.bpel.def.validation.variable;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.bpel.def.validation.IAeValidationProblemCodes;
import org.activebpel.rt.bpel.def.validation.IAeValidator;
import org.activebpel.rt.xml.IAeNamespaceContext;
import org.activebpel.rt.xml.def.AeBaseDefNamespaceContext;
import org.exolab.castor.xml.schema.XMLType;

/**
 * Validates a complex type and query usage
 */
public class AeComplexTypeQueryUsage extends AeVariableUsage
{
   /** query used on the complex type */
   private String mQuery;

   /**
    * ctor
    * @param aVariableValidator - the variable validator
    * @param aValidator - validator that references the variable
    * @param aQuery - query used on the element
    */
   public AeComplexTypeQueryUsage(AeVariableValidator aVariableValidator, IAeValidator aValidator, String aQuery)
   {
      super(aVariableValidator, aValidator);
      setQuery(aQuery);
   }

   /**
    * Validates:
    * 1. variable is a complex type
    * 2. type can be resolved by WSDL provider
    * 3. xpath query is valid with the type
    * 
    * @see org.activebpel.rt.bpel.def.validation.variable.AeVariableUsage#validate()
    */
   public boolean validate()
   {
      if (getVariableValidator().getWsdlDef() == null)
      {
         getVariableValidator().addTypeNotFoundError(AeVariableValidator.ERROR_TYPE_SPEC_NOT_FOUND, getType(), getValidator().getDefinition());
         return false;
      }

      // Add a warning if a WS-BPEL process uses an absolute path query with a complex type.
      String processNS = getVariableValidator().getProcessValidator().getProcessDef().getNamespace();
      if (!IAeBPELConstants.BPWS_NAMESPACE_URI.equals(processNS))
      {
         if (getQuery().startsWith("/")) //$NON-NLS-1$
         {
            getVariableValidator().getReporter().reportProblem(IAeValidationProblemCodes.BPEL_COMPLEX_ABSOLUTE_PATH_SYNTAX_CODE,
                  AeMessages.getString("AeVariableUsage.AbsolutePathSyntaxWarning"), //$NON-NLS-1$
                  new String[] { getQuery() }, getValidator().getDefinition());
         }
      }

      try
      {
         XMLType xmlType = getVariableValidator().getDef().getXMLType();
         // Note: pass in null for the root since this should be a relative path - hence we do not validate the root node (complex types don't exist in BPEL 1.1)
         IAeNamespaceContext nsContext = new AeBaseDefNamespaceContext(getValidator().getDefinition());
         getVariableValidator().getProcessValidator().getXPathQueryValidator().validate(nsContext,
               getQuery(), xmlType, null);
      }
      catch (Exception ex)
      {
         getVariableValidator().getReporter().reportProblem(IAeValidationProblemCodes.BPEL_COMPLEX_INVALID_XPATH_CODE,
               AeVariableValidator.ERROR_INVALID_XPATH,
               new String[] { getQuery(), ex.getLocalizedMessage() },
               getValidator().getDefinition());
      }
      // Return true (valid) here even if there was a problem with the xpath.  Until we can
      // handle extended types better, we only warn about the xpath query
      return true;
   }
   
   /**
    * Getter for the query
    */
   protected String getQuery()
   {
      return mQuery;
   }
   
   /**
    * Getter for the element
    */
   protected QName getType()
   {
      return getVariableValidator().getDef().getType();
   }

   /**
    * @param aQuery The query to set.
    */
   protected void setQuery(String aQuery)
   {
      mQuery = aQuery;
   }
}