//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/AeDeploymentValidationUtil.java,v 1.3 2008/03/17 19:54:43 vvelusamy Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.validation.query.AeXPathQueryValidator;
import org.activebpel.rt.bpel.def.visitors.AeDefFindVisitor;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.message.AeMessagePartTypeInfo;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.xml.def.AeBaseDefNamespaceContext;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.XMLType;

/**
 * Util class for performing deployment related validation.
 */
public class AeDeploymentValidationUtil
{

   /** XPath query validator for validating deployment description --> indexed property --> query validation. */
   private static AeXPathQueryValidator mXPathQueryValidator = new AeXPathQueryValidator();

   /**
    * Gets the xpath query validator.
    */
   private static AeXPathQueryValidator getXPathQueryValidator()
   {
      return mXPathQueryValidator;
   }

   /**
    * Validates that the variable information is correct with respect to the process definition
    */
   public static void validateVariableInfo(String aType, String aPath, String aPart, String aQuery,
            AeProcessDef aProcessDef, IAeContextWSDLProvider aContextWSDLProvider, IAeBaseErrorReporter aErrorReporter)
   {
      // assign location paths for definitions
      AeDefFindVisitor findVisitor = new AeDefFindVisitor(aPath);
      findVisitor.visit(aProcessDef);
      AeBaseXmlDef def = findVisitor.getFoundDef();
      if (def != null && def instanceof AeVariableDef)
      {
         AeVariableDef varDef = (AeVariableDef) def;
         if (varDef.getMessageType() != null)
         {
            if (AeUtil.isNullOrEmpty(aPart))
            {
               // no part for message type
               String severity = AeMessages.getString("AeDeploymentValidationUtil.SEVERITY_ERROR"); //$NON-NLS-1$
               aErrorReporter.addError(AeMessages.getString("AeDeploymentValidationUtil.PART_REQUIRED"), //$NON-NLS-1$
                        new Object[] { severity, aPath }, null);
            }
            else
            {
               AeMessagePartTypeInfo part = null;
               try
               {
                  part = varDef.getPartInfo(aPart);
               }
               catch (AeBpelException e)
               {
                  // ignore, the error is reported below
               }

               if (part == null)
               {
                  // part name doesn't match for message type
                  String severity = AeMessages.getString("AeDeploymentValidationUtil.SEVERITY_ERROR"); //$NON-NLS-1$
                  aErrorReporter.addError(
                           AeMessages.getString("AeDeploymentValidationUtil.INVALID_VARIABLE_PATH"), //$NON-NLS-1$
                           new Object[] { severity, aPath }, null);
               }
               else if (AeUtil.notNullOrEmpty(aQuery))
               {
                  // validate the query against the part is good
                  validatePartQuery(varDef, aPart, aQuery, aPath, aErrorReporter);
               }
            }
         }
         else
         {
            if (AeUtil.notNullOrEmpty(aPart))
            {
               // part name supplied for a non-message type variable
               String severity = AeMessages.getString("AeDeploymentValidationUtil.SEVERITY_WARNING"); //$NON-NLS-1$
               aErrorReporter.addWarning(AeMessages.getString("AeDeploymentValidationUtil.PART_NOT_APPLICABLE"), //$NON-NLS-1$
                        new Object[] { severity, aPath, aPart }, null);
            }

            // if the indexed property has a query
            if (AeUtil.notNullOrEmpty(aQuery))
            {
               if (varDef.getElement() != null)
               {
                  // validate the query against the element is good
                  validateElementQuery(varDef, aQuery, aPath, aContextWSDLProvider, aErrorReporter);
               }
               else
               {
                  // query supplied for a simple type variable
                  String severity = AeMessages.getString("AeDeploymentValidationUtil.SEVERITY_ERROR"); //$NON-NLS-1$
                  aErrorReporter.addError(AeMessages
                           .getString("AeDeploymentValidationUtil.INVALID_QUERY_FOR_SIMPLE_TYPE"), new Object[] { //$NON-NLS-1$
                           severity, aPath, aQuery }, null);
               }
            }
         }

      }
      else
      {
         // no def or nonvar definition found
         String severity = AeMessages.getString("AeDeploymentValidationUtil.SEVERITY_ERROR"); //$NON-NLS-1$
         aErrorReporter.addError(AeMessages.getString("AeDeploymentValidationUtil.INVALID_VARIABLE_PATH"), //$NON-NLS-1$
                  new Object[] { severity, aPath }, null);
      }
   }

   /**
    * Validates a part query and if in error reports it against the passed def object.
    * 
    * @param aVar
    *            The variable used in query.
    * @param aPart
    *            The part of the message/variable used in query.
    * @param aQuery
    *            The query to validate.
    * @param aPath
    *            the variable path.
    * @param aErrorReporter
    *            Reporter to which validation errors are to be added.
    */
   private static void validatePartQuery(AeVariableDef aVar, String aPart, String aQuery, String aPath,
            IAeBaseErrorReporter aErrorReporter)
   {
      try
      {
         AeMessagePartTypeInfo part = aVar.getPartInfo(aPart);
         XMLType type = aVar.getPartType(part.getName());
         if (type != null)
         {
            QName root = part.getElementName();
            if (root == null)
               root = new QName(null, part.getName());
            getXPathQueryValidator().validate(new AeBaseDefNamespaceContext(aVar), aQuery, type, root);
         }
      }
      catch (Exception ex)
      {
         String severity = AeMessages.getString("AeDeploymentValidationUtil.SEVERITY_ERROR"); //$NON-NLS-1$
         aErrorReporter.addError(AeMessages.getString("AeDeploymentValidationUtil.INVALID_PART_QUERY"), //$NON-NLS-1$
                  new String[] { severity, aPath, aQuery, ex.getLocalizedMessage() }, null);
      }
   }

   /**
    * Validates a query against an element and if in error reports it against the passed def object.
    * 
    * @param aVar
    *            The variable used in query.
    * @param aQuery
    *            The query to validate.
    * @param aPath
    *            the variable path
    * @param aContextWSDLProvider
    *           Context wsdl provider
    * @param aErrorReporter
    *            Reporter to which validation errors are to be added.
    */
   private static void validateElementQuery(AeVariableDef aVar, String aQuery, String aPath,
            IAeContextWSDLProvider aContextWSDLProvider, IAeBaseErrorReporter aErrorReporter)
   {
      try
      {
         if (aVar.getElement() != null)
         {
            // verify the element query
            AeBPELExtendedWSDLDef wsdl = AeWSDLDefHelper.getWSDLDefinitionForElement(aContextWSDLProvider, aVar
                     .getElement());
            // if wsdl is null it would have already been reported as a problem
            if (wsdl != null)
            {
               ElementDecl elem = wsdl.findElement(aVar.getElement());
               if (elem != null)
               {
                  QName root = aVar.getElement();
                  getXPathQueryValidator().validate(new AeBaseDefNamespaceContext(aVar), aQuery, elem.getType(), root);
               }
            }
         }
      }
      catch (Exception ex)
      {
         String severity = AeMessages.getString("AeDeploymentValidationUtil.SEVERITY_ERROR"); //$NON-NLS-1$
         aErrorReporter.addError(AeMessages.getString("AeDeploymentValidationUtil.INVALID_ELEMENT_QUERY"), //$NON-NLS-1$
                  new String[] { severity, aPath, aQuery, ex.getLocalizedMessage() }, null);
      }
   }
   
   /**
    * For legacy reasons, deployment validation messages have severity prefixed to the messages. It is used as is, when
    * displaying deployment results in admin console and when deploying from designer. However, when used for validating
    * pdds, the prefixes shouldn't be there.
    * 
    * todo: Remove severity prefix from the actual validation messages.
    */
   public static String stripSeverityPrefix(String aMessage)
   {
      String severity = AeMessages.getString("AeDeploymentValidationUtil.SEVERITY_ERROR"); //$NON-NLS-1$
      String warning = AeMessages.getString("AeDeploymentValidationUtil.SEVERITY_WARNING"); //$NON-NLS-1$

      if (aMessage.startsWith(severity))
      {
         return aMessage.replaceFirst(severity, ""); //$NON-NLS-1$
      }
      else if (aMessage.startsWith(warning))
      {
         return aMessage.replaceFirst(warning, ""); //$NON-NLS-1$
      }

      return aMessage;
   }

}
