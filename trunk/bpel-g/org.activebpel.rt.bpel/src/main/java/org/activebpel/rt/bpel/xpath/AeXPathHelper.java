// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/xpath/AeXPathHelper.java,v 1.25 2008/02/17 21:37:12 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.xpath;

import java.text.MessageFormat;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeCorrelationViolationException;
import org.activebpel.rt.bpel.impl.AeFaultFactory;
import org.activebpel.rt.bpel.impl.AeNamespaceResolver;
import org.activebpel.rt.bpel.impl.AeSelectionFailureException;
import org.activebpel.rt.bpel.impl.IAeFaultFactory;
import org.activebpel.rt.bpel.impl.expr.AeExpressionException;
import org.activebpel.rt.bpel.impl.expr.xpath.AeXPathNamespaceContext;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.jaxen.FunctionCallException;
import org.jaxen.FunctionContext;
import org.jaxen.NamespaceContext;
import org.jaxen.UnresolvableException;
import org.jaxen.VariableContext;
import org.jaxen.XPath;
import org.jaxen.XPathFunctionContext;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A helper class for executing XPath expressions. 
 */
public class AeXPathHelper
{
   /** namespace for the version of BPEL we're executing expressions for, this affects the errors reported */
   private String mNamespace;
   
   private static final AeXPathHelper BPEL4WS = new AeXPathHelper(IAeBPELConstants.BPWS_NAMESPACE_URI);
   private static final AeXPathHelper WSBPEL = new AeXPathHelper(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI);
   
   /** private ctor to prevent instantiation */
   private AeXPathHelper(String aNamespace)  
   {
      mNamespace = aNamespace;
   }
   
   /**
    * Getter for the namespace
    */
   protected String getNamespace()
   {
      return mNamespace;
   }
   
   /**
    * Getter for the fault factory
    */
   protected IAeFaultFactory getFaultFactory()
   {
      return AeFaultFactory.getFactory(getNamespace());
   }
   
   /**
    * Gets the helper configured to work with the given namespace
    * @param aNamespace
    */
   public static AeXPathHelper getInstance(String aNamespace)
   {
      if (WSBPEL.getNamespace().equals(aNamespace))
         return WSBPEL;
      else
         return BPEL4WS;
   }

   /**
    * A version of unwrapXPathValue that does not throw an exception when it fails to select 
    * something or when multiple items are selected.  This should be called rather than 
    * passing "true, true" to the 3-arg version of this method.
    * 
    * @param aData
    */
   public Object unwrapXPathValue(Object aData)
   {
      try
      {
         return unwrapXPathValue(aData, true, true);
      }
      catch (AeBpelException ex)
      {
         // When passing true for the 2nd argument, this exception should never happen.  If it
         // does, make sure to log it.
         AeException.logError(ex);
         return null;
      }
   }

   /**
    * Unwrap an XPath value.  If the argument is a java.util.List then the behavior is determined by
    * the passed flags (for empty selection - selection of multiple nodes will cause an exception).
    * 
    * @param aData
    * @param aAllowEmptySelection
    */
   public Object unwrapXPathValue(Object aData, boolean aAllowEmptySelection) throws AeBpelException
   {
      return unwrapXPathValue(aData, aAllowEmptySelection, false);
   }

   /**
    * Unwrap an XPath value.  If the argument is a java.util.List then the behavior is determined by
    * the passed flags.
    * 
    * @param aData The data to unwrap.
    * @param aAllowEmptySelection If true a empty string is returned for no selection, else an exception is thrown.
    * @param aAllowMultiSelection If true, selecting multiple nodes is allowed, else an exception is thrown.
    * @throws AeBpelException
    */
   private Object unwrapXPathValue(Object aData, boolean aAllowEmptySelection, boolean aAllowMultiSelection) throws AeBpelException
   {
      Object returnValue = aData;
      if (aData instanceof List)
      {
         List list = (List)aData;
         if (list.size() == 1)
            returnValue = list.get(0);
         else if(list.size() == 0 && aAllowEmptySelection)
            returnValue = null;
         else if(list.size() > 1 && aAllowMultiSelection)
            returnValue = list;
         else
            throw new AeSelectionFailureException(getNamespace(), list.size()); 
      }
      
      if (returnValue instanceof Double)
      {
         // if the double can safely be converted to a Long then do it.
         // this handles the case where we are attempting to set an int
         // value on a complex type and introducing a floating point number
         // where we don't want one.
         returnValue = AeUtil.doubleToLong((Double) returnValue);
      }
      
      return returnValue;
   }

   /**
    * Convenience method that evaluates the property and then converts it to a simple type.
    * @param aPropAlias
    * @param aMsgData
    * @param aTypeMapping
    * @param aType
    * @throws AeBusinessProcessException
    */
   public Object extractCorrelationPropertyValue(
      IAePropertyAlias aPropAlias,
      IAeMessageData aMsgData,
      AeTypeMapping aTypeMapping,
      QName aType)
      throws AeBusinessProcessException
   {
      // fixme (TypeMapping) pass XMLType instead of type QName. Enables deserialization code in type mapper to search for base types.
      Object propData = null;
      Object varData = aPropAlias.getType() == IAePropertyAlias.MESSAGE_TYPE ? aMsgData.getData(aPropAlias.getPart()) : aMsgData.getData((String) aMsgData.getPartNames().next());
      if (varData instanceof Document)
      {
         Element docElem = ((Document) varData).getDocumentElement();
         if (AeUtil.notNullOrEmpty(aPropAlias.getQuery()))
         {
            NamespaceContext namespaceContext = new AeXPathNamespaceContext(new AeNamespaceResolver(aPropAlias));
            FunctionContext functionContext = XPathFunctionContext.getInstance(); // TODO should we allow custom functions to be used here?
            Object result = executeXPathExpression(aPropAlias.getQuery(), docElem, functionContext, namespaceContext);
            propData = unwrapXPathValue(result, false);
         }
         else
         {
            propData = ((Document)varData).getDocumentElement();
         }
      }
      else
      {
         propData = varData;
      }
      Object value = propData;
      return convertPropertyResultToSimpleType(value, aTypeMapping, aType);
   }

   /**
    * Executes the xpath expression using the contextual information passed in AND the default Jaxen
    * XPathFunctionContext.  This should only be used if the XPath expression being executed does
    * not need access to the custom function contexts.
    * 
    * @param aExpr
    * @param aContext
    * @param aNamespaceResolver
    */
   public Object executeXPathExpression(String aExpr, Object aContext, NamespaceContext aNamespaceResolver)
         throws AeBpelException
   {
      return executeXPathExpression(aExpr, aContext, XPathFunctionContext.getInstance(), aNamespaceResolver);
   }

   /**
    * Executes the xpath expression using the contextual information passed in.
    * 
    * @param aExpr
    * @param aContext
    * @param aFunctionContext
    * @param aNamespaceResolver
    */
   public Object executeXPathExpression(String aExpr, Object aContext, FunctionContext aFunctionContext,
         NamespaceContext aNamespaceResolver) throws AeBpelException
   {
      return executeXPathExpression(aExpr, aContext, aFunctionContext, null, aNamespaceResolver);
   }

   /**
    * Executes the xpath expression using the contextual information passed in.
    * 
    * @param aExpr
    * @param aContext
    * @param aFunctionContext
    * @param aVariableContext
    * @param aNamespaceResolver
    */
   public Object executeXPathExpression(String aExpr, Object aContext, FunctionContext aFunctionContext,
         VariableContext aVariableContext, NamespaceContext aNamespaceResolver) throws AeBpelException
   {
      Object result = null;
      Object context = aContext;
      
      try
      {
         XPath path = new DOMXPath(aExpr);
         path.setFunctionContext(aFunctionContext);
         path.setNamespaceContext(aNamespaceResolver);
         if (aVariableContext != null)
            path.setVariableContext(aVariableContext);
         if (context instanceof Document)
            context = ((Document) context).getDocumentElement();
         result = path.evaluate(context);
      }
      catch (FunctionCallException fe)
      {
         rethrowFunctionCallException(aExpr, fe);
      }
      catch (UnresolvableException ue)
      {
         String msg = AeMessages.format("AeXPathHelper.ERROR_EVALUATING_EXPRESSION_UNRESOLVEABLE", new Object[] { aExpr, ue.getLocalizedMessage() }); //$NON-NLS-1$
         throw new AeBpelException(msg, getFaultFactory().getSubLanguageExecutionFault(IAeFaultFactory.XPATH_FUNCTION_ERROR,ue, ue.getLocalizedMessage()));
      }
      catch (AeExpressionException aex)
      {
         throw aex;
      }
      catch (Throwable ex)
      {
         throw new AeBpelException( MessageFormat.format(AeMessages.getString("AeXPathHelper.ERROR_1"), //$NON-NLS-1$
                                                         new Object[] {aExpr, ex.getMessage()}), 
                                    getFaultFactory().getSubLanguageExecutionFault(IAeFaultFactory.XPATH_FUNCTION_ERROR, ex, ex.getLocalizedMessage()) );    
      }
      
      return result;
   }
   
   /**
    * Determines the root cause of a function call exception and rethrows it in the form of a 
    * AeBpelException.
    * 
    * @param aExpression
    * @param aFunctionCallException
    * @throws AeBpelException
    */
   private void rethrowFunctionCallException(String aExpression, FunctionCallException aFunctionCallException) throws AeBpelException
   {
      if (aFunctionCallException.getCause() instanceof AeBusinessProcessException)
         throw new AeBpelException(MessageFormat.format(AeMessages.getString("AeXPathHelper.ERROR_1"), //$NON-NLS-1$
               new Object[] { aExpression, aFunctionCallException.getCause().getMessage() }),
               getFaultFactory().getSubLanguageExecutionFault(IAeFaultFactory.XPATH_FUNCTION_ERROR, aFunctionCallException, aFunctionCallException.getLocalizedMessage()));
      else if (aFunctionCallException.getCause() instanceof AeFunctionCallException)
      {
         Throwable rootException = aFunctionCallException.getCause();
         if (rootException != null && rootException.getCause() != null)
         {
            rootException = rootException.getCause();
         }
         String msg = ""; //$NON-NLS-1$
         if (rootException != null)
         {
            msg = rootException.getMessage();
         }
         if (rootException instanceof AeBpelException)
            throw (AeBpelException) rootException;
         throw new AeBpelException(MessageFormat.format(AeMessages.getString("AeXPathHelper.ERROR_1"), //$NON-NLS-1$
               new Object[] { aExpression, msg }),
               getFaultFactory().getSubLanguageExecutionFault(IAeFaultFactory.XPATH_FUNCTION_ERROR, aFunctionCallException, aFunctionCallException.getLocalizedMessage()));
         
      }
      else
         throw new AeBpelException( MessageFormat.format(AeMessages.getString("AeXPathHelper.ERROR_1"), //$NON-NLS-1$
               new Object[] {aExpression, aFunctionCallException.getMessage()}), 
               getFaultFactory().getSubLanguageExecutionFault(IAeFaultFactory.XPATH_FUNCTION_ERROR, aFunctionCallException, aFunctionCallException.getLocalizedMessage()) );
   }

   /**
    * Converts the argument to a simple type. If the argument is already a simple 
    * type (java.lang.String or java.lang.Number) then it is returned unchanged. 
    * If not then we're expecting a dom Element in which case we'll extract the 
    * text from the element and return that as our value.  
    * @param aObject - if you pass null, you'll get an exception
    */
   private Object convertPropertyResultToSimpleType(Object aObject, AeTypeMapping aTypeMapping, QName aType)
      throws AeBusinessProcessException
   {
      // a null signals a problem
      if (aObject == null)
      {
         throw new AeCorrelationViolationException(getNamespace(), AeCorrelationViolationException.NULL_VALUE);
      }
      Object simpleType = null;
      if (aObject instanceof Element)
      {
         Element element = (Element)aObject;
         element.normalize();
         String xmlString = element.getFirstChild()!=null? element.getFirstChild().getNodeValue() : null;
         simpleType = aTypeMapping.deserialize(aType, xmlString);
      }
      else if (aObject instanceof Node)
      {
         simpleType = ((Node)aObject).getNodeValue();
      }
      else
      {
         simpleType = aObject;
      }

      if (simpleType == null)
      {
         throw new AeCorrelationViolationException(getNamespace(), AeCorrelationViolationException.NULL_VALUE);
      }

      return simpleType.toString();
   }
}
