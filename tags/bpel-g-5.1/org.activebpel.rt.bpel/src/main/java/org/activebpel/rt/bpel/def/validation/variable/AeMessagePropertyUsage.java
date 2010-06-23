package org.activebpel.rt.bpel.def.validation.variable;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.bpel.def.validation.IAeValidationProblemCodes;
import org.activebpel.rt.bpel.def.validation.IAeValidator;
import org.activebpel.rt.bpel.impl.AeNamespaceResolver;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;
import org.activebpel.rt.xml.IAeNamespaceContext;

/**
 * Subclass of AeMessagePartQueryUsage that adds support for a property
 * and property alias.
 */
public class AeMessagePropertyUsage extends AeMessagePartQueryUsage
{
   /** property used in conjunction with this variable */
   private QName mProperty;
   /** the property alias */
   private IAePropertyAlias mPropertyAlias;

   /**
    * ctor
    * @param aVariableValidator - the variable validator
    * @param aValidator - validator referencing this variable
    * @param aPropertyName - name of the property being read from this variable
    */
   public AeMessagePropertyUsage(AeVariableValidator aVariableValidator, IAeValidator aValidator, QName aPropertyName)
   {
      super(aVariableValidator, aValidator, null, null);
      setProperty(aPropertyName);
   }
   
   /**
    * Loads the property alias for this variable message and property combination and then
    * relies on super class to validate message part and query usage.
    * @see org.activebpel.rt.bpel.def.validation.variable.AeVariableUsage#validate()
    */
   public boolean validate()
   {
      IAePropertyAlias alias = null;
      
      if( getVariableValidator().getDef().isMessageType() )
      {
         alias = AeWSDLDefHelper.getPropertyAlias(
               getVariableValidator().getValidationContext().getContextWSDLProvider(),
               getVariableValidator().getDef().getMessageType(),
               IAePropertyAlias.MESSAGE_TYPE,
               getProperty());
         
         if (alias == null)
         {
            getVariableValidator().getReporter().reportProblem( IAeValidationProblemCodes.BPEL_MSG_MISSING_PROPERTY_ALIAS_CODE,
                  AeMessages.getString("AeProcessDef.MissingPropertyAlias"), //$NON-NLS-1$
                  new Object[] {
               new Integer(IAePropertyAlias.MESSAGE_TYPE),
                  getVariableValidator().getDef().getName(),
                  getVariableValidator().getNSPrefix(getProperty().getNamespaceURI()),
                  getProperty().getLocalPart()},
                  getVariableValidator().getDefinition());
            return false;
         }
         setPropertyAlias(alias);
         setPart(alias.getPart());
         setQuery(alias.getQuery());
      }
      
      // will validate the message part and query from the property alias
      // TODO (EPW) the super.validate() may someday validate $varReferences, but those are not legal here and should be prohibited
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
    * Validates the query if present. Query is optional with propertyAlias
    * @see org.activebpel.rt.bpel.def.validation.variable.AeMessagePartQueryUsage#validateQuery()
    */
   protected boolean validateQuery()
   {
      if (AeUtil.notNullOrEmpty(getQuery()))
      {
         return super.validateQuery();
      }
      return true;
   }

   /**
    * Getter for the property
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
