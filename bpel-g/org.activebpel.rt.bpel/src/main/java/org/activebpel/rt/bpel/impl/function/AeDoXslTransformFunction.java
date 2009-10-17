// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/AeDoXslTransformFunction.java,v 1.15 2007/07/05 19:51:56 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeFaultFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * An implementation of BPEL 2.0's doXslTransform() XPath function.  The doXslTransform function
 * is used to apply an XSLT 1.0 transform to a node set.  The signature is:
 *
 * Object bpel:doXslTransform(String, node-set, [String, Object]*)
 *
 * Notes for validation:
 *
 * 1) The first parameter MUST be a string literal (static analysis)
 * 2) The second parameter MUST contain a single EII.  (runtime error: bpel:xsltInvalidSource fault)
 * 3) The remaining parameters must be matched pairs of (paramName, paramValue) where the name must
 *    be a string and the value can be any Object.
 * 4) The first parameter specifies a style sheet URI, which is used to find the style sheet to
 *    use for the transform.  If the style sheet cannot be found, then a bpel:xsltStylesheetNotFound
 *    fault is thrown
 * 5) The name-value pairs found in the optional arguments to the function are passed as global
 *    parameters to the style sheet for use during transformation.
 * 6) Any problems processing the XSLT must result in a bpel:subLanguageExecutionFault being thrown
 *
 * The return value of this function must be either a single TII (if the style sheet's output
 * method is 'text' or 'html') or a single EII (if the style sheet's output method is 'xml').
 */
public class AeDoXslTransformFunction extends AeAbstractBpelFunction
{
   public static final String FUNCTION_NAME = "doXslTransform"; //$NON-NLS-1$
 
   /**
    * Default c'tor.
    */
   public AeDoXslTransformFunction()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunction#call(org.activebpel.rt.bpel.function.IAeFunctionExecutionContext, java.util.List)
    */
   public Object call(IAeFunctionExecutionContext aContext, List aArgs) throws AeFunctionCallException
   {
      try
      {
         if (aArgs.size() < 2)
         {
            throwStaticAnalysisFailure(aContext, AeMessages.getString("AeDoXslTransformFunction.INCORRECT_NUM_ARGS_ERROR")); //$NON-NLS-1$
         }

         String xslUri = (String) aArgs.get(0);
         Object nodeSet = (Object) aArgs.get(1);
         Map parameters = extractParameters(aArgs);

         Source xsltSource = resolveXslUri(aContext, xslUri);
         Source xmlSource = createXmlSource(aContext, nodeSet);

         Node result = null;
         
         // Do the transform.
         try
         {
            AeXslUriResolver uriResolver = new AeXslUriResolver(aContext);
            result = AeXmlUtil.doTransform(xsltSource, xmlSource, parameters, uriResolver);
         }
         catch (TransformerFactoryConfigurationError ex)
         {
            throwSubLanguageExecutionFault(aContext, ex);
         }
         catch (TransformerException ex)
         {
            throwSubLanguageExecutionFault(aContext, ex);
         }

         // Note: we could easily validate the return value here, since we could call
         // trans.getOutputProperty("method") to get the expected return type (EII or TII).
         // Not doing that right now, since it should "just work".
         return result;
      }
      catch (AeBpelException ex)
      {
         throw new AeFunctionCallException(ex);
      }
   }

   /**
    * Extracts the parameter map from the argument list.  The
    *
    * @param aArguments
    */
   protected Map extractParameters(List aArguments)
   {
      Map params = new HashMap();
      for (int i = 2; i < aArguments.size(); i += 2)
      {
         String paramName = (String) aArguments.get(i);
         Object paramValue = aArguments.get(i + 1);
         params.put(paramName, paramValue);
      }

      return params;
   }
   
   /**
    * Resolves any imports for the xsl.
    */
   private class AeXslUriResolver implements URIResolver
   {
      /** context */
      private IAeFunctionExecutionContext mContext;
      
      /**
       * Ctor
       */
      public AeXslUriResolver(IAeFunctionExecutionContext aContext)
      {
         setContext(aContext);
      }

      /**
       * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String)
       */
      public Source resolve(String aHref, String aBase) throws TransformerException
      {
         String uri = AeUtil.resolveImport(aBase, aHref);
         try
         {
            return resolveXslUri(getContext(), uri);
         }
         catch (AeBpelException e)
         {
            throw new TransformerException(e);
         } 
      }

      /**
       * @return the context
       */
      protected IAeFunctionExecutionContext getContext()
      {
         return mContext;
      }

      /**
       * @param aContext the context to set
       */
      protected void setContext(IAeFunctionExecutionContext aContext)
      {
         mContext = aContext;
      }
      
   }

   /**
    * Given a URI of an XSL resource, resolves that URI into a concrete Source.
    *
    * @param aContext
    * @param aXslUri
    * @throws AeBpelException
    */
   protected Source resolveXslUri(IAeFunctionExecutionContext aContext, String aXslUri) throws AeBpelException
   {
      try
      {
         IAeBusinessProcessEngineInternal engine = aContext.getAbstractBpelObject().getProcess().getEngine();
         // TODO (cck) add type mapping for xsl?
         InputSource isource = (InputSource)engine.loadResource(aXslUri, IAeConstants.XSL_NAMESPACE);
         StreamSource source = new StreamSource(isource.getByteStream());
         source.setSystemId(isource.getSystemId());
         return source;
      }
      catch (Throwable ex)
      {
         throwStylesheetNotFoundFault(aContext, aXslUri, ex);
         return null;
      }
   }

   /**
    * Creates a DOMSource from the given node set.  The node set must be a single EII, otherwise
    * a bpel:xsltInvalidSource fault is thrown.
    *
    * @param aNodeSet
    * @throws AeBpelException
    */
   protected Source createXmlSource(IAeFunctionExecutionContext aContext, Object aNodeSet) throws AeBpelException
   {
      Element element = unwrapNodeSet(aContext, aNodeSet);

      /*
       * According to the BPEL spec:
       *
       *    The single EII as specified by this parameter MUST be treated as the single child
       *    of the root node of the source tree for XSLT processing.
       *
       * To make that happen, clone the Element into its own Document, then use the Document
       * as the Source of the Transform.
       */
      element = AeXmlUtil.cloneElement(element);
      return new DOMSource(element.getOwnerDocument());
   }

   /**
    * Unwraps the given Object into an Element.  The Object must be either an Element, a
    * Document, or a List/NodeSet with a single Element or Document in it.  Otherwise a
    * bpel:xsltInvalidSource is thrown.
    *
    * @param aNodeSet
    * @throws AeBpelException
    */
   protected Element unwrapNodeSet(IAeFunctionExecutionContext aContext, Object aNodeSet) throws AeBpelException
   {
      Element element = null;

      if (aNodeSet instanceof Element)
      {
         element = (Element) aNodeSet;
      }
      else if (aNodeSet instanceof Document)
      {
         element = ((Document) aNodeSet).getDocumentElement();
      }
      else if (aNodeSet instanceof List)
      {
         List list = (List) aNodeSet;
         if (list.size() != 1)
         {
            throwInvalidSourceFault(aContext);
         }
         element = unwrapNodeSet(aContext, list.get(0));
      }
      else if (aNodeSet instanceof NodeList)
      {
         NodeList nodeSet = (NodeList) aNodeSet;
         if (nodeSet.getLength() != 1)
         {
            throwInvalidSourceFault(aContext);
         }
         element = unwrapNodeSet(aContext, nodeSet.item(0));
      }
      else
      {
         throwInvalidSourceFault(aContext);
      }

      return element;
   }

   /**
    * Called to throw a bpel:xsltInvalidSource fault.
    *
    * @param aContext
    * @throws AeBpelException
    */
   protected void throwInvalidSourceFault(IAeFunctionExecutionContext aContext) throws AeBpelException
   {
      IAeFault fault = aContext.getFaultFactory().getXsltInvalidSource();
      throw new AeBpelException(AeMessages.getString("AeDoXslTransformFunction.INVALID_SOURCE_ERROR"), fault); //$NON-NLS-1$
   }

   /**
    * Called to throw a bpel:subLanguageExecutionFault when there is a problem executing the
    * transform.
    *
    * @param aContext
    * @param aException
    * @throws AeBpelException
    */
   protected void throwSubLanguageExecutionFault(IAeFunctionExecutionContext aContext, Throwable aException) throws AeBpelException
   {
      String msg = AeMessages.format("AeDoXslTransformFunction.ERROR_EXECUTING_TRANSFORM", aException.getLocalizedMessage()); //$NON-NLS-1$
      IAeFault fault = aContext.getFaultFactory().getSubLanguageExecutionFault(IAeFaultFactory.XSLT_FUNCTION_ERROR, aException, msg);
      throw new AeBpelException(msg, fault, aException);
   }

   /**
    * Called to throw a bpel:xsltStylesheetNotFound when the XSLT stylesheet can not be located
    * using the URI passed to the function.
    *
    * @param aContext
    * @param aXslUri
    * @param aException
    */
   protected void throwStylesheetNotFoundFault(IAeFunctionExecutionContext aContext, String aXslUri, Throwable aException) throws AeBpelException
   {
      String msg = AeMessages.format("AeDoXslTransformFunction.STYLESHEET_NOT_FOUND_ERROR", aXslUri); //$NON-NLS-1$
      IAeFault fault = aContext.getFaultFactory().getXsltStyleSheetNotFound();
      throw new AeBpelException(msg, fault, aException);
   }

   /**
    * Called to throw a static analysis failure fault.
    *
    * @param aContext
    * @param aMessage
    */
   protected void throwStaticAnalysisFailure(IAeFunctionExecutionContext aContext, String aMessage) throws AeBpelException
   {
      IAeFault fault = aContext.getFaultFactory().getStaticAnalysisFailure(aMessage);
      throw new AeBpelException(aMessage, fault);
   }
}
