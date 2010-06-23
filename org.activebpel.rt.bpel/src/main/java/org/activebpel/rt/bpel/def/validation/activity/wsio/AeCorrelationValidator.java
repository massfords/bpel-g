//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/wsio/AeCorrelationValidator.java,v 1.10 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.wsio; 

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.def.io.AeCorrelationPatternIOFactory;
import org.activebpel.rt.bpel.def.io.IAeCorrelationPatternIO;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.bpel.def.validation.activity.IAeCorrelationUser;
import org.activebpel.rt.bpel.def.validation.activity.scope.AeCorrelationSetValidator;
import org.activebpel.rt.bpel.impl.AeNamespaceResolver;
import org.activebpel.rt.message.AeMessagePartTypeInfo;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;
import org.exolab.castor.xml.schema.XMLType;

/**
 * Model provides validation for the correlation def
 */
public class AeCorrelationValidator extends AeBaseValidator
{
   /** correlationSet model that this correlation references */
   private AeCorrelationSetValidator mSetModel;

   /**
    * ctor
    * @param aDef
    */
   public AeCorrelationValidator(AeCorrelationDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Getter for the def
    */
   protected AeCorrelationDef getDef()
   {
      return (AeCorrelationDef) getDefinition();
   }
   
   /**
    * Gets the correlationSet model
    */
   public AeCorrelationSetValidator getSetModel()
   {
      return mSetModel;
   }
   
   /**
    * Setter for the correlationSet model
    * @param aModel
    */
   protected void setSetModel(AeCorrelationSetValidator aModel)
   {
      mSetModel = aModel;
   }

   /**
    * Validates:
    * 1. correlationSet can be resolved
    * 2. use of pattern checked against owner activity
    * 3. records property usage of each variable/property combination from correlationSet
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      IAeCorrelationUser correlationUser = getCorrelationUser();
      
      setSetModel( getCorrelationSetValidator(getDef().getCorrelationSetName(), true) );
      if (getSetModel() != null)
      {
         // if pattern present, validate that it's allowed
         boolean hasPattern = getDef().getPattern() != null;
         if (!correlationUser.isPatternRequired() && hasPattern)
         {
            getReporter().reportProblem( BPEL_CORRELATION_PATTERN_NOT_ALLOWED_CODE, ERROR_CORRELATION_PATTERN_NOT_ALLOWED,
                  null, getDefinition() );
         }
         else if (correlationUser.isPatternRequired() && !hasPattern)
         {
            getReporter().reportProblem( BPEL_CORRELATION_PATTERN_REQUIRED_CODE, ERROR_CORRELATION_PATTERN_REQUIRED,
                  null, getDefinition() );
         }
         else if (hasPattern)
         {
            if (getDef().isRequestDataUsedForCorrelation())
            {
               if (correlationUser.getProducerMessagePartsMap() != null)
               {
                  recordPropertyUsage(correlationUser.getProducerMessagePartsMap());
               }
               else
               {
                  // no need to report an error here since the invoke will already have an error message
                  // for the missing producer message parts map. All invokes must specify a message
                  // for transmission.
               }
            }
            
            if (getDef().isResponseDataUsedForCorrelation())
            {
               if (correlationUser.getConsumerMessagePartsMap() != null)
               {
                  recordPropertyUsage(correlationUser.getConsumerMessagePartsMap());
               }
               else
               {
                  reportPatternError(ERROR_CORRELATION_OUT_PATTERN_MISMATCH);
               }
            }
            
            // if it's neither, then it's an invalid pattern value
            if (!getDef().isRequestDataUsedForCorrelation() && !getDef().isResponseDataUsedForCorrelation())
            {
               reportPatternError(ERROR_CORRELATION_INVALID_PATTERN);
            }
         }
         else if (correlationUser.getConsumerMessagePartsMap() != null)
         {
            recordPropertyUsage(correlationUser.getConsumerMessagePartsMap());
         }
         else if (correlationUser.getProducerMessagePartsMap() != null)
         {
            recordPropertyUsage(correlationUser.getProducerMessagePartsMap());
         }
      }
   }

   /**
    * The value for the pattern isn't valid, report an error
    * @param aErrorMessage
    */
   protected void reportPatternError(String aErrorMessage)
   {
      IAeCorrelationPatternIO patternIO = AeCorrelationPatternIOFactory.getInstance(getProcessDef().getNamespace());
      getReporter().reportProblem( BPEL_PATTERN_INVALID_CODE, aErrorMessage,
            new String [] { patternIO.toString(getDef().getPattern()) }, getDefinition() );
   }

   /**
    * Locates a property alias for the message and verifies that the property alias can be
    * used with this correlation set's properties.
    * @param aMap
    */
   protected void recordPropertyUsage(AeMessagePartsMap aMap)
   {
      IAeContextWSDLProvider wsdlProvider = getValidationContext().getContextWSDLProvider();
      
      for(Iterator it=getSetModel().getDef().getPropertiesList(); it.hasNext();)
      {
         QName propName = (QName) it.next();
         
         // Look for a messageType property alias first, they take precedence over element style property aliases
         IAePropertyAlias propAlias = AeWSDLDefHelper.getPropertyAlias(wsdlProvider, aMap.getMessageType(), IAePropertyAlias.MESSAGE_TYPE, propName);

         if (propAlias == null && isWSBPEL() && aMap.isSinglePartElement())
         {
            // try searching for an element type property alias
            propAlias = AeWSDLDefHelper.getPropertyAlias(wsdlProvider, aMap.getSingleElementPart(), IAePropertyAlias.ELEMENT_TYPE, propName);
         }
         
         if (propAlias == null)
         {
            getReporter().reportProblem( BPEL_CORR_MISSING_PROPERTY_ALIAS_CODE, AeMessages.getString("AeProcessDef.MissingPropertyAlias"), //$NON-NLS-1$
                  new Object[] {
               new Integer(IAePropertyAlias.MESSAGE_TYPE),
                  aMap.getMessageType().getLocalPart(),
                  getNSPrefix(propName.getNamespaceURI()),
                  propName.getLocalPart()},
                  getDefinition());
         }
         else 
         {
            // fixme validate that we can resolve the property
            
            // we found a prop alias, we'll validate its query
            try
            {
               if (propAlias.getType() == IAePropertyAlias.MESSAGE_TYPE)
               {
                  AeMessagePartTypeInfo part = aMap.getPartInfo(propAlias.getPart());
                
                  if (part == null)
                  {
                     getReporter().reportProblem( BPEL_PROPERTY_ALIAS_BAD_PART_CODE, 
                                          ERROR_PROPERTY_ALIAS_BAD_PART,
                                          new String[] { getNSPrefix(aMap.getMessageType().getNamespaceURI()),
                                          aMap.getMessageType().getLocalPart(),
                                          getNSPrefix(propName.getNamespaceURI()),
                                          propName.getLocalPart()},
                           getDefinition());
                  }
                  else
                  {
                     XMLType type = part.getXMLType();
                     
                     if (!type.isSimpleType() && AeUtil.isNullOrEmpty(propAlias.getQuery()))
                     {
                        getReporter().reportProblem( BPEL_NO_QUERY_FOR_PROP_ALIAS_CODE, 
                                             ERROR_NO_QUERY_FOR_PROP_ALIAS,
                                             new String[] { getNSPrefix(aMap.getMessageType().getNamespaceURI()),
                                             aMap.getMessageType().getLocalPart(),
                                             getNSPrefix(propName.getNamespaceURI()),
                                             propName.getLocalPart(),
                                             propAlias.getPart()},
                              getDefinition());
                        
                     }
                     else if (AeUtil.notNullOrEmpty(propAlias.getQuery()))
                     {
                        if (isWSBPEL())
                        {
                           if (propAlias.getQuery().startsWith("/") && !part.isElement()) //$NON-NLS-1$
                           {
                              getReporter().reportProblem(BPEL_CORR_ABSOLUTE_PATH_SYNTAX_CODE,
                                    AeMessages.getString("AeVariableUsage.AbsolutePathSyntaxWarning"), //$NON-NLS-1$
                                    new String[] { propAlias.getQuery() }, getDefinition());
                              return;
                           }
                        }

                        QName root = part.getElementName();
                        if (root == null)
                           root = new QName(null, part.getName());

                        getProcessValidator().getXPathQueryValidator().validate(
                              new AeNamespaceResolver(propAlias), propAlias.getQuery(), type, root);
                     }
                  }
               }
               else // propAlias.getType() == IAePropertyAlias.ELEMENT_TYPE
               {
                  XMLType xmlType = aMap.getSingleElementPartInfo().getXMLType();
                  getProcessValidator().getXPathQueryValidator().validate(new AeNamespaceResolver(propAlias),
                        propAlias.getQuery(), xmlType, aMap.getSingleElementPart());
               }
            }
            catch(AeException e)
            {
               getReporter().reportProblem(BPEL_CORR_INVALID_XPATH_CODE,AeVariableValidator.ERROR_INVALID_XPATH, new String[] { propAlias.getQuery(),
                           e.getLocalizedMessage() },
                           getDefinition());
            }
         }
      }
   }
   
   /**
    * Gets the correlation user which is a wsio activity like receive, reply, invoke, onMessage, onEvent.
    */
   protected IAeCorrelationUser getCorrelationUser()
   {
      return (IAeCorrelationUser) getAnscestor(IAeCorrelationUser.class);
   }
}
 