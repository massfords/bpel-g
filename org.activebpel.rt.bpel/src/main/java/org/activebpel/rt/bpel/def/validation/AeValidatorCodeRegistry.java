// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/AeValidatorCodeRegistry.java,v 1.1 2008/03/20 15:59:51 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.wsdl.def.castor.AeSchemaParserUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.rt.xml.AeXMLParserErrorHandler;
import org.exolab.castor.xml.schema.Schema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Registry of codes and severity for the 3 different BPEL namespaces (BPEL 1.1, BPEL 2.0 and Abstract BPEL).
 */
public class AeValidatorCodeRegistry
{
   /** schema for the validator severity file */
   private static Schema sSeverityFileSchema;
   /** default namespace serverity map */
   private static Map sPrefixMap = new HashMap();
   /** map of namespace to map of code/severity */
   private static Map sSeverityMap = new HashMap();
   
   /** valid values for validator severity */
   public static final String ERROR = "ERROR";     //$NON-NLS-1$
   public static final String WARNING = "WARNING"; //$NON-NLS-1$
   public static final String INFO = "INFO";       //$NON-NLS-1$
   public static final String SKIP = "SKIP";       //$NON-NLS-1$
   
   
   static
   {
      sPrefixMap.put("aeSeverity", IAeValidationProblemCodes.SEVERITY_NAMESPACE); //$NON-NLS-1$
      sSeverityFileSchema = AeSchemaParserUtil.loadSchema("validatorSeverity.xsd", AeValidationProblemReporter.class); //$NON-NLS-1$
      
      // load all known severity mappings
      AeValidatorCodeRegistry.registerCodes(AeValidatorCodeRegistry.class.getResource("WsBpelRuleSeverity.xml"));        //$NON-NLS-1$
      AeValidatorCodeRegistry.registerCodes(AeValidatorCodeRegistry.class.getResource("BpwsRuleSeverity.xml"));          //$NON-NLS-1$
      AeValidatorCodeRegistry.registerCodes(AeValidatorCodeRegistry.class.getResource("AbstractBpelRuleSeverity.xml"));  //$NON-NLS-1$
   }
   
   /**
    * private C'tor.
    */
   private AeValidatorCodeRegistry()
   {
      
   }
   
   /**
    * Load a severity file to a cache of codes and severity values.
    *  
    * @param aSeverityURL
    */
   public static void registerCodes(URL aSeverityURL)
   {
      try
      {
         // load the document
         AeXMLParserBase parser = new AeXMLParserBase(true, true);
         parser.setErrorHandler(new AeXMLParserErrorHandler());
         InputSource inputSrc = new InputSource(aSeverityURL.toExternalForm());
         inputSrc.setSystemId(aSeverityURL.toString());
         Document severityDoc = parser.loadDocument(inputSrc, Collections.singleton(sSeverityFileSchema).iterator());
         
         // Fail if the rules file is not schema valid.
         if (parser.getErrorHandler().hasParseWarnings())
         {
            throw parser.getErrorHandler().getParseException();
         }
         
         List codeNodes = AeXPathUtil.selectNodes(severityDoc, "aeSeverity:codes/aeSeverity:code", sPrefixMap); //$NON-NLS-1$
         
         Map codeMap = new HashMap();
         String targetNS = AeXPathUtil.selectSingleNode(severityDoc, "aeSeverity:codes/@targetNamespace", sPrefixMap).getNodeValue();//$NON-NLS-1$
         
         // loop over the code nodes and load the map
         for (Iterator iter = codeNodes.iterator(); iter.hasNext(); )
         {
            Element codeElem = (Element) iter.next();
            String id = AeXPathUtil.selectText(codeElem, "aeSeverity:id", sPrefixMap); //$NON-NLS-1$
            String severity = AeXPathUtil.selectText(codeElem, "aeSeverity:severity", sPrefixMap); //$NON-NLS-1$
            codeMap.put(id, severity); 
         }
         
         sSeverityMap.put(targetNS, Collections.unmodifiableMap(codeMap));
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }
   
   /**
    * Return a Map of codes, where the key is the problem code and the value is the severity.  A namespace
    * is used to return the correct set of codes.
    * 
    * @param aNamespace
    */
   public static Map getCodes(String aNamespace)
   {
      return (Map) sSeverityMap.get(aNamespace);
   }
   
   /**
    * Return severity for the problem severity given a code.
    * 
    * @param aNamespace the namespace for where the code is associated to
    * @param aProblemCode the code value
    * @param aDefaultSeverity default severity if the problem code isn't found.
    */
   public static String getProblemSeverity(String aNamespace, String aProblemCode, String aDefaultSeverity)
   {
      Map codes = (Map) sSeverityMap.get(aNamespace);
      
      if (codes == null)
      {
         return AeValidatorCodeRegistry.ERROR;
      }
      else
      {
         Object sev = codes.get(aProblemCode);
         return AeUtil.isNullOrEmpty((String) sev) ? AeValidatorCodeRegistry.ERROR : String.valueOf(sev);
      }
   }
   
}
