// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/AeWSResourceValidationPreferences.java,v 1.2 2008/02/21 21:18:08 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.wsdl.def.castor.AeSchemaParserUtil;
import org.activebpel.rt.wsresource.IAeWSResourceConstants;
import org.activebpel.rt.wsresource.validation.rules.AeRulesUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.exolab.castor.xml.schema.Schema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Abstract class to provide functionality to load a validation preferences file to extending classes.
 */
public abstract class AeWSResourceValidationPreferences implements IAeWSResourceValidationPreferences
{
   /** schema for the severity file */
   private static Schema sSeveritySchema;
   /** prefix to namespace mapping*/
   private static Map sPrefixMap = new HashMap();
   /** Map of rule ids -> severity */
   private Map mSeverity = new HashMap();
   
   static
   {
      sPrefixMap.put("ae", IAeWSResourceConstants.SEVERITY_NAMESPACE); //$NON-NLS-1$
      sSeveritySchema = AeSchemaParserUtil.loadSchema("aePreferences.xsd", AeRulesUtil.class); //$NON-NLS-1$
   }
   
   /**
    * Loads the rules found in the severity preferences file.
    * 
    * @param aSeverityFileURL
    */
   public void loadPreferencesFile(URL aSeverityFileURL)
   {
      try
      {
         AeXMLParserBase parser = new AeXMLParserBase(true, true);
         InputSource severityIS = new InputSource(new InputStreamReader(aSeverityFileURL.openStream()));
         severityIS.setSystemId(aSeverityFileURL.toString());
         Document severityDoc = parser.loadDocument(severityIS, Collections.singleton(sSeveritySchema).iterator());

         List ruleNodes = AeXPathUtil.selectNodes(
                                                severityDoc, 
                                                "ae:preferences/ae:preference", //$NON-NLS-1$
                                                sPrefixMap); 
         
         String targetNS = AeXPathUtil.selectSingleNode(
                                                severityDoc, 
                                                "ae:preferences/@targetNamespace", //$NON-NLS-1$
                                                sPrefixMap).getNodeValue();
         
         for (Iterator iter = ruleNodes.iterator(); iter.hasNext(); )
         {
            Element ruleElem = (Element) iter.next();
            String code = AeXPathUtil.selectText(ruleElem, "ae:ruleCode", sPrefixMap); //$NON-NLS-1$
            String severity = AeXPathUtil.selectText(ruleElem, "ae:preferredSeverity", sPrefixMap); //$NON-NLS-1$
            QName id = new QName(targetNS, code);
            
            getSeverityMap().put(id, AeRulesUtil.convertSeverity(severity));
         }
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }
   
   /** 
    * @see org.activebpel.rt.wsresource.validation.IAeWSResourceValidationPreferences#getSeverity(org.activebpel.rt.wsresource.validation.IAeWSResourceValidationRule)
    */
   public int getSeverity(IAeWSResourceValidationRule aValidationRule)
   {
      int prefSeverity = getSeverityCode(aValidationRule.getId());
      return prefSeverity != -1 ? prefSeverity : aValidationRule.getDefaultSeverity();
   }

   /**
    * @return Returns the int severity code.
    */
   protected int getSeverityCode(QName aRuleId)
   {
      if (getSeverityMap().containsKey(aRuleId))
      {
         return ((Integer) mSeverity.get(aRuleId)).intValue();
      }
      else
      {
         return -1;
      }
   }
   
   /**
    * @return Returns the severity Map.
    */
   protected Map getSeverityMap()
   {
      return mSeverity;
   }

}
