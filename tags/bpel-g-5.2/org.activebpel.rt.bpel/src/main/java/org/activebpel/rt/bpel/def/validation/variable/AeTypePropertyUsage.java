package org.activebpel.rt.bpel.def.validation.variable;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.bpel.def.validation.IAeValidationProblemCodes;
import org.activebpel.rt.bpel.def.validation.IAeValidator;
import org.activebpel.rt.bpel.impl.AeNamespaceResolver;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;
import org.activebpel.rt.xml.IAeNamespaceContext;
import org.exolab.castor.xml.schema.XMLType;

/**
 * Validates a type and property usage.
 * 
 * fixme (JP) add a warning if the property alias namespace is different than the process's bpel namespace (do this also in the other Ae*PropertyUsage classes)
 *  ** Note: the property alias interface does not have enough information to do this - need to modify the prop alias io layer to add the namespace of the property alias def **
 *  ** Note: this warning needs to be added for AeElementPropertyUsage and AeMessagePropertyUsage as well. **
 */
public class AeTypePropertyUsage extends AeVariableUsage
{
   /** property used in conjunction with this variable */
   private QName mProperty;
   /** the property alias */
   private IAePropertyAlias mPropertyAlias;

   /**
    * ctor
    * @param aVariableValidator - the variable validator
    * @param aModel - validator that references the variable
    * @param aProperty - property used
    */
   public AeTypePropertyUsage(AeVariableValidator aVariableValidator, IAeValidator aModel, QName aProperty)
   {
      super(aVariableValidator, aModel);
      setProperty(aProperty);
   }

   /**
    * Validates:
    * 1. variable is a type
    * 2. type can be resolved by WSDL provider
    * 3. xpath query is valid with the type
    * 
    * @see org.activebpel.rt.bpel.def.validation.variable.AeVariableUsage#validate()
    */
   public boolean validate()
   {
      if( getVariableValidator().getDef().isType() )
      {
         IAePropertyAlias alias = AeWSDLDefHelper.getPropertyAlias(
               getVariableValidator().getValidationContext().getContextWSDLProvider(),
               getVariableValidator().getDef().getType(),
               IAePropertyAlias.TYPE,
               getProperty());

         if (alias == null)
         {
            getVariableValidator().getReporter().reportProblem( IAeValidationProblemCodes.BPEL_TYPE_MISSING_PROPERTY_ALIAS_CODE,
                  AeMessages.getString("AeProcessDef.MissingPropertyAlias"), //$NON-NLS-1$
                  new Object[] {
                  new Integer(IAePropertyAlias.TYPE),
                  getVariableValidator().getDef().getName(),
                  getVariableValidator().getNSPrefix(getProperty().getNamespaceURI()),
                  getProperty().getLocalPart()},
                  getValidator().getDefinition());
            return false;
         }
         setPropertyAlias(alias);
         String query = alias.getQuery();
         // TODO (EPW) the validateQuery() may someday validate $varReferences, but those are not legal here and should be prohibited
         if (!validateQuery(query))
         {
            return false;
         }
      }
      
      return super.validate();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.variable.AeMessagePartQueryUsage#createNamespaceContextForQuery()
    */
   protected IAeNamespaceContext createNamespaceContextForQuery()
   {
      return new AeNamespaceResolver(getPropertyAlias());
   }

   /**
    * Validates the query and returns true/false.
    */
   protected boolean validateQuery(String aQuery)
   {
      try
      {
         XMLType xmlType = getVariableValidator().getDef().getXMLType();
         IAeNamespaceContext nsContext = createNamespaceContextForQuery();
         getVariableValidator().getProcessValidator().getXPathQueryValidator().validate(nsContext, aQuery,
               xmlType, getVariableValidator().getDef().getType());
      }
      catch (Exception ex)
      {
         getVariableValidator().getReporter().reportProblem(IAeValidationProblemCodes.BPEL_TYPE_INVALID_XPATH_CODE,
               AeVariableValidator.ERROR_INVALID_XPATH,
               new String[] { aQuery, ex.getLocalizedMessage() },
               getValidator().getDefinition());
      }
      return true;
   }

   /**
    * Getter for the property.
    */
   protected QName getProperty()
   {
      return mProperty;
   }

   /**
    * @param aProperty The property to set.
    */
   protected void setProperty(QName aProperty)
   {
      mProperty = aProperty;
   }

   /**
    * @return Returns the propertyAlias.
    */
   protected IAePropertyAlias getPropertyAlias()
   {
      return mPropertyAlias;
   }

   /**
    * @param aPropertyAlias the propertyAlias to set
    */
   protected void setPropertyAlias(IAePropertyAlias aPropertyAlias)
   {
      mPropertyAlias = aPropertyAlias;
   }
}