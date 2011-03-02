// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/policy/AePolicyIO.java,v 1.1 2007/07/27 18:08:54 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def.policy;

import java.io.PrintWriter;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.xml.namespace.QName;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.AeWSDLExtensionIO;
import org.activebpel.rt.wsdl.def.AeWSDLPrefixes;
import org.activebpel.rt.wsdl.def.IAeBPELExtendedWSDLConst;
import org.w3c.dom.Element;

/**
 * This class serializes and deserializes a Policy extension
 * implementation. 
 */
public class AePolicyIO extends AeWSDLExtensionIO implements IAeBPELExtendedWSDLConst
{
   /**
    * Serialize a Policy extension element into a the PrintWriter.
    * 
    * @param aParentType a class object indicating where in the WSDL document
    * this extensibility element was encountered. 
    * @param aQname the QName of this extension element.
    * @param aExtElement the extensibility element to be serialized.
    * @param aWriter the PrintWriter where we're serializing to.
    * @param aDefinition the Definition that this extensibility element was
    * encountered in.
    * @param aExtReg the associated ExtensionRegistry.
    */
   public void marshall(
      Class aParentType,
      QName aQname,
      ExtensibilityElement aExtElement,
      PrintWriter aWriter,
      Definition aDefinition,
      ExtensionRegistry aExtReg)
      throws WSDLException
   {
      IAePolicy policy = (IAePolicy)aExtElement;

      Element domElem = createElement(aQname);

      // Determine the Namespace prefix of the policy extension.
      String preferredPrefix = AeWSDLPrefixes.getPolicyPrefix(aQname.getNamespaceURI());
      String prefix = getPrefixOrCreateLocally(aQname.getNamespaceURI(), aDefinition, domElem, preferredPrefix); 
      domElem.setPrefix(prefix);
      
      if (policy.getPolicyElement() != null)
         AeXmlUtil.copyNodeContents(policy.getPolicyElement(), domElem);

      if (!AeUtil.isNullOrEmpty(policy.getReferenceId()))
      {
         preferredPrefix = AeWSDLPrefixes.getSecUtilPrefix(IAeConstants.WSU_NAMESPACE_URI);
         prefix = getPrefixOrCreateLocally(IAeConstants.WSU_NAMESPACE_URI, aDefinition, domElem, preferredPrefix); 
         domElem.setAttributeNS(IAeConstants.WSU_NAMESPACE_URI, prefix + ":" + IAePolicy.WSU_ID_ATTRIBUTE, policy.getReferenceId()); //$NON-NLS-1$
      }
      
      writeElement(domElem, aWriter);
   }

   /**
    * Deserialize a policy extension element into a
    * AePolicyImpl implementation object.
    * 
    * @param aParentType a class object indicating where in the WSDL document
    * this extensibility element was encountered. 
    * @param aQName the QName of this extension element.
    * @param aPolicyElem the extensibility DOM element to be deserialized.
    * @param aDefinition the Definition that this extensibility element was
    * encountered in.
    * @param aExtReg the associated ExtensionRegistry.
    * 
    * @return ExtensibilyElement the implementation mapping class for this
    * extension.
    */
   public ExtensibilityElement unmarshall(
      Class aParentType,
      QName aQName,
      Element aPolicyElem,
      Definition aDefinition,
      ExtensionRegistry aExtReg)
      throws WSDLException
   {
      // Create a new policy implementation object.
      AePolicyImpl policy = new AePolicyImpl(aPolicyElem);
      policy.setRequired(Boolean.TRUE);
      policy.setElementType(aQName);
      return policy;
   }

}
