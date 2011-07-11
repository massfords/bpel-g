package org.activebpel.rt.bpel.def.validation.variable;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.bpel.def.validation.IAeValidationProblemCodes;
import org.activebpel.rt.bpel.def.validation.IAeValidator;
import org.activebpel.rt.message.AeMessagePartTypeInfo;
import org.activebpel.rt.xml.IAeNamespaceContext;
import org.exolab.castor.xml.schema.XMLType;

/**
 * Subclass of AeMessagePartUsage that adds support for query validation
 */
public class AeMessagePartQueryUsage extends AeMessagePartUsage
{
   /** query used on part */
   private String mQuery;
   
   /**
    * ctor
    * @param aVariableValidator - the variable validator
    * @param aValidator - validator using the variable
    * @param aPart - name of the part
    * @param aQuery - query used on the part
    */
   public AeMessagePartQueryUsage(AeVariableValidator aVariableValidator, IAeValidator aValidator, String aPart, String aQuery)
   {
      super(aVariableValidator, aValidator, aPart);
      setQuery(aQuery);
   }
   
   /**
    * Relies on super to validate message and part usage and then validates the query
    * @see org.activebpel.rt.bpel.def.validation.variable.AeVariableUsage#validate()
    */
   public boolean validate()
   {
      if (super.validate())
      {
         return validateQuery();
      }
      return false;
   }

   /**
    * Validates the query using the xpath query validator on the process validator
    */
   protected boolean validateQuery()
   {
      // TODO (EPW) once there is a query framework, do additional validation here
      boolean isValid;

      try
      {
         // Add a warning if a WS-BPEL process uses an absolute path query with a complex type.
         String processNS = getVariableValidator().getProcessValidator().getProcessDef().getNamespace();
         if (!IAeBPELConstants.BPWS_NAMESPACE_URI.equals(processNS))
         {
            if (getQuery().startsWith("/") && !getVariableValidator().getDef().isPartElement(getPart())) //$NON-NLS-1$
            {
               getVariableValidator().getReporter().reportProblem( IAeValidationProblemCodes.BPEL_MSG_ABSOLUTE_PATH_SYNTAX_CODE,
                     AeMessages.getString("AeVariableUsage.AbsolutePathSyntaxWarning"), //$NON-NLS-1$
                     new String[] { getQuery() }, getValidator().getDefinition());
            }
         }

         AeMessagePartTypeInfo part = getVariableValidator().getDef().getPartInfo(getPart());
         XMLType type = getVariableValidator().getDef().getPartType(part.getName());
         QName root = part.getElementName();
         if(root == null)
            root = new QName(null, part.getName());
         try
         {
            IAeNamespaceContext nsContext = createNamespaceContextForQuery();
            getVariableValidator().getProcessValidator().getXPathQueryValidator().validate(nsContext,
                  getQuery(), type, root);
         }
         catch (Exception ex)
         {
            getVariableValidator().getReporter().reportProblem( IAeValidationProblemCodes.BPEL_MSG_PART_INVALID_XPATH_CODE,
                                    AeVariableValidator.ERROR_INVALID_XPATH,
                                    new String[] { getQuery(), ex.getLocalizedMessage() },
                                    getValidator().getDefinition() );
         }
         isValid = true;
      }
      catch (Exception ex)
      {
         getVariableValidator().getReporter().reportProblem( IAeValidationProblemCodes.BPEL_XPATH_VALIDATION_EXCEPTION_CODE,
                                 AeVariableValidator.ERROR_INVALID_XPATH,
                                 new String[] { getQuery(), ex.getLocalizedMessage() },
                                 getValidator().getDefinition() );
         isValid = false;
      }

      return isValid;
   }

   /**
    * Getter for the query
    */
   protected String getQuery()
   {
      return mQuery;
   }
   
   /**
    * Setter for the query
    * @param aQuery
    */
   protected void setQuery(String aQuery)
   {
      mQuery = aQuery;
   }
}
