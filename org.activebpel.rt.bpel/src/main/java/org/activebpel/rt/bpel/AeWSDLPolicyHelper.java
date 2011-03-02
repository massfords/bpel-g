// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/AeWSDLPolicyHelper.java,v 1.6 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.extensions.ElementExtensible;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.policy.AePolicyImpl;
import org.activebpel.rt.wsdl.def.policy.IAePolicy;
import org.activebpel.rt.wsdl.def.policy.IAePolicyReference;
import org.activebpel.wsio.IAeWebServiceEndpointReference;
import org.w3c.dom.Element;

/**
 * Utility helper class to do various WSDL policy attachment lookups.
 */
public class AeWSDLPolicyHelper
{
   /**
    * Resolves the policy references for an endpoint reference 
    * from both WSDL attachments and references on the partner link.
    * 
    * This will resolve WSDL attachments for service and port subjects    
    * 
    * Unresolved policies are reported as warnings
    * 
    */
   public static List getEffectiveWSDLPolicies(IAeContextWSDLProvider aProvider, IAeWebServiceEndpointReference aEndpoint, IAeBaseErrorReporter aReporter) 
   {
      // Resolve from the wsdl definition
      List policies = new ArrayList();
      if (!AeUtil.isNullOrEmpty(aEndpoint.getServiceName()))
      {
         AeBPELExtendedWSDLDef def = AeWSDLDefHelper.getWSDLDefinitionForService( aProvider, aEndpoint.getServiceName() );
         policies.addAll(getEffectivePolicy(def, aEndpoint.getServiceName(), aEndpoint.getServicePort(), null, aReporter));
      }
      
      // The policy mappers will expect an array of elements
      // TODO (KP) maybe treat all policy lists as IAePolicy, rather than converting back and forth
      List policyElements = new ArrayList();
      if (!AeUtil.isNullOrEmpty(policies)) 
      {
         for (Iterator it = policies.iterator(); it.hasNext();)
         {
            IAePolicy policy = (IAePolicy) it.next();
            policyElements.add(policy.getPolicyElement());
         }
      }

      // resolve from endpoint policy
      policyElements.addAll(resolvePolicyReferences(aProvider, aEndpoint.getPolicies(), aReporter));
      
      return policyElements;
   }

   /**
    * Resolves the policy references for an endpoint reference for a particular operation
    *  
    * This will resolve WSDL attachments for service, operation, endpoint, and message subjects
    * as well as references on the wsa endpoint itself.
    * 
    * Unresolved references are reported as warnings
    *  
    */
   public static List getEffectiveOperationPolicies(IAeContextWSDLProvider aProvider, IAeWebServiceEndpointReference aEndpoint, QName aPortType, String aOperation, IAeBaseErrorReporter aReporter)
   {
      // Resolve from the wsdl definition
      List policies = new ArrayList();
      if (!AeUtil.isNullOrEmpty(aEndpoint.getServiceName()))
      {
         AeBPELExtendedWSDLDef def = AeWSDLDefHelper.getWSDLDefinitionForService( aProvider, aEndpoint.getServiceName() );
         policies.addAll(getEffectivePolicy(def, aEndpoint.getServiceName(), aEndpoint.getServicePort(), aOperation, aReporter));
      }
      else
      {
         AeBPELExtendedWSDLDef def = AeWSDLDefHelper.getWSDLDefinitionForPortType(aProvider, aPortType);
         policies.addAll(getEffectivePolicy(def, aPortType, aOperation, aReporter));
      }
      
      // The policy mappers will expect an array of elements
      List policyElements = new ArrayList();
      if (!AeUtil.isNullOrEmpty(policies)) 
      {
         for (Iterator it = policies.iterator(); it.hasNext();)
         {
            IAePolicy policy = (IAePolicy) it.next();
            policyElements.add(policy.getPolicyElement());
         }
      }

      // resolve from endpoint policy
      policyElements.addAll(resolvePolicyReferences(aProvider, aEndpoint.getPolicies(), aReporter));
      
      return policyElements;
   }
   
   /**
    * Returns the list of effective <code>IAePolicy</code> by merging the 
    * policies for all WSDL subjects defined by WS-Policy Attachment
    * 
    * Service Subject = Service object
    * Endpoint Subject = Port and Binding objects
    * Operation Subject = Operation and BindingOperation objects
    * Message Subject = BindingInput and Input Message objects
    * 
    * @param aDef
    * @param aServiceName
    * @param aPortName
    * @param aOperationName
    * @param aReporter
    */
   public static List getEffectivePolicy(AeBPELExtendedWSDLDef aDef, QName aServiceName, String aPortName, String aOperationName, IAeBaseErrorReporter aReporter)
   {
      if (aDef == null)
         return Collections.EMPTY_LIST;
      
      List policies = new ArrayList();
      
      Service service = (Service) aDef.getServices().get(aServiceName);
      if (service == null)
      {
         return policies;
      }

      // Service subject policies
      policies.addAll(getPolicies(aDef, service, aReporter));
      
      // Endpoint subject policies
      Port port = service.getPort(aPortName);
      if (port == null)
      {
         return policies;
      }
      policies.addAll(getEndpointSubjectPolicies(aDef, port, aReporter));
      
      // Operation subject policies
      Binding binding = port.getBinding();
      if (binding == null)
      {
         return policies;
      }
      policies.addAll(getOperationSubjectPolicies(aDef, binding, aOperationName, aReporter));
      
      // Message subject policies
      policies.addAll(getMessageSubjectPolicies(aDef, binding, aOperationName, aReporter));
      
      return policies;
   }

   /**
    * Returns the list of effective <code>IAePolicy</code> by merging the 
    * policies for the abstract WSDL subjects
    * 
    * Operation Subject = Operation object
    * Message Subject = Input Message objects
    * @param aDef
    * @param aPortType
    * @param aOperationName
    * @param aReporter
    */
   public static List getEffectivePolicy(AeBPELExtendedWSDLDef aDef, QName aPortType, String aOperationName, IAeBaseErrorReporter aReporter)
   {
      if (aDef == null)
         return Collections.EMPTY_LIST;
      
      List policies = new ArrayList();
      
      PortType portType = aDef.getPortType(aPortType);
      Operation op = portType.getOperation(aOperationName, null, null);
      if (op != null)
      {
         // Operation policies
         policies.addAll(getPolicies(aDef, op, aReporter));
         // Message policies
         policies.addAll(getPolicies(aDef, op.getInput().getMessage(), aReporter));
      }
      
      return policies;
   }
   
   /**
    * Returns the list of <code>IAePolicy</code> for an Endpoint subject
    * 
    * An Endpoint subject includes the Port, Binding, and PortType
    * 
    * @param aDef
    * @param aPort
    * @param aReporter
    */
   public static List getEndpointSubjectPolicies(AeBPELExtendedWSDLDef aDef, Port aPort, IAeBaseErrorReporter aReporter)
   {
      List policies = new ArrayList();
      
      if (aPort == null)
      {
         return policies;
      }
      
      // add all the port policy
      policies.addAll(getPolicies(aDef, aPort, aReporter));
      
      // add all the binding policy
      Binding binding = aPort.getBinding();
      if (binding != null)
      {
         policies.addAll(getPolicies(aDef, binding, aReporter));
      }
      
      return policies;
   }

   /**
    * Returns the list of <code>IAePolicy</code> for an Operation subject
    * 
    * An Operation subject includes the BindingOperation, and PortType Operation
    * 
    * @param aDef
    * @param aBinding
    * @param aOperationName
    * @param aReporter
    */
   public static List getOperationSubjectPolicies(AeBPELExtendedWSDLDef aDef, Binding aBinding, String aOperationName, IAeBaseErrorReporter aReporter)
   {
      List policies = new ArrayList();
      
      if (aBinding == null)
      {
         return policies;
      }
            
      // add all the Binding Operation policy
      BindingOperation bop = aBinding.getBindingOperation(aOperationName, null, null);
      if (bop != null)
         policies.addAll(getPolicies(aDef, bop, aReporter));

      // all the port type operation policy
      PortType portType = aBinding.getPortType();
      if (portType != null)
      {
         Operation op = portType.getOperation(aOperationName, null, null);
         if (op != null)
            policies.addAll(getPolicies(aDef, op, aReporter));
      }
      
      return policies;
   }

   /**
    * Returns the list of <code>IAePolicy</code> for an Endpoint subject
    * 
    * A Message subject includes the BindingInput and Input Message
    * @param aDef
    * @param aBinding
    * @param aOperationName
    * @param aReporter
    */
   public static List getMessageSubjectPolicies(AeBPELExtendedWSDLDef aDef, Binding aBinding, String aOperationName, IAeBaseErrorReporter aReporter)
   {
      List policies = new ArrayList();
      
      if (aBinding == null)
      {
         return policies;
      }
            
      // add all the Binding Operation input policy
      BindingOperation bop = aBinding.getBindingOperation(aOperationName, null, null);
      if (bop != null)
         policies.addAll(getPolicies(aDef, bop.getBindingInput(), aReporter));

      // all the message policy
      PortType portType = aBinding.getPortType();
      if (portType != null)
      {
         Operation op = portType.getOperation(aOperationName, null, null);
         if (op != null)
            policies.addAll(getPolicies(aDef, op.getInput().getMessage(), aReporter));
      }
      
      return policies;
   }
   
   /**
    * Returns the list of <code>IAePolicy</code> elements associated with the 
    * given WSDL element (Service, PortType, Operation, Message,...)
    * 
    * A wsp:Policy element may be either a direct child, or resolved 
    * as a wsp:PolicyReference 
    * 
    * @param aDef WSDL provider to resolve local references, if null no local lookups are used
    * @param aExtElement element that may contain policy elements
    * @param aReporter
    */
   public static List getPolicies(AeBPELExtendedWSDLDef aDef, ElementExtensible aExtElement, IAeBaseErrorReporter aReporter)
   {
      if (aDef == null)
         return Collections.EMPTY_LIST;
      
      if (aExtElement == null)
         return Collections.EMPTY_LIST;
      
      List elements = aExtElement.getExtensibilityElements();
      if (AeUtil.isNullOrEmpty(elements))
      {
         return Collections.EMPTY_LIST;
      }
      
      List policies = new ArrayList();
      for (Iterator it = elements.iterator(); it.hasNext();)
      {
         ExtensibilityElement ext = (ExtensibilityElement) it.next();
         IAePolicy policy = null;

         // see if we've got a wsp:Policy or wsp:PolicyReference
         if (ext instanceof IAePolicy)
         {
            policy = (IAePolicy) ext;
         }
         else if (ext instanceof IAePolicyReference)
         {
            IAePolicyReference ref = null;
            ref = (IAePolicyReference) ext;
            // See if we can resolve the reference using the given wsdl
            policy = getPolicy(aDef, ref, aReporter);
         }
         
         // add the policy we found
         if (policy != null)
         {
            policies.add(policy);
         }
      }
      return policies;
   }

   /**
    * Returns the list of <code>IAePolicy</code> elements associated with the 
    * given DOM element
    * 
    * A wsp:Policy element may be either a direct child, or resolved 
    * as a wsp:PolicyReference 
    * 
    * @param aProvider WSDL provider to resolve external references by namespace
    * @param aExtElement element that may contain policy references
    */
   public static List getPolicies(IAeContextWSDLProvider aProvider, ElementExtensible aExtElement, IAeBaseErrorReporter aReporter)
   {
      List elements = aExtElement.getExtensibilityElements();
      if (AeUtil.isNullOrEmpty(elements))
      {
         return Collections.EMPTY_LIST;
      }
      
      List policies = new ArrayList();
      // flag indicates if this policy has direct assertion children
      boolean hasAssertions = false; 
      for (Iterator it = elements.iterator(); it.hasNext();)
      {
         ExtensibilityElement ext = (ExtensibilityElement) it.next();
         IAePolicy policy = null;

         // see if we've got a wsp:Policy or wsp:PolicyReference
         if (ext instanceof IAePolicy)
         {
            policy = (IAePolicy) ext;
            policies.add(policy);
         }
         else if (ext instanceof IAePolicyReference)
         {
            IAePolicyReference ref = null;
            ref = (IAePolicyReference) ext;
            // See if we can resolve the reference using the given wsdl
            List resolved = getPolicy(aProvider, ref, aReporter);
            if (!AeUtil.isNullOrEmpty(resolved))
            {
               policies.addAll(resolved);
            }
         }
         else
         {
            // anything that's not policy or policy reference is an assertion
            hasAssertions = true;
         }
      }
      
      // this is a policy element with assertions (not other policy or references)
      if ((aExtElement instanceof IAePolicy) && hasAssertions)
      {
         policies.add(aExtElement);
      }
      
      return policies;
   }
   
   /**
    * Resolves and returns the IAePolicy element at the location specified in the URI attribute 
    * of a wsp:PolicyReference
    * 
    * For example, the namespace URI of "http://www.fabrikam123.com/policies#RmPolicy" 
    * is "http://www.fabrikam123.com/policies" and the wsu:Id is "RmPolicy"
    * 
    * If there's a null or empty namespace on the URI we'll just do the lookup using the id 
    * 
    * @param aDef
    * @param aPolicyRef
    * @param aReporter
    */
   public static IAePolicy getPolicy(AeBPELExtendedWSDLDef aDef, IAePolicyReference aPolicyRef, IAeBaseErrorReporter aReporter)
   {
      if (aDef == null)
      {
         return null;
      }

      IAePolicy policy = null;
      if (aPolicyRef.isLocalReference())
      {
         policy = aDef.getPolicy(aPolicyRef.getReferenceId());
      }
      else 
      {
         policy = aDef.getPolicy(aPolicyRef.getNamespaceURI(), aPolicyRef.getReferenceId());
      }
      
      // didn't find it
      if (policy == null)
      {
         reportWarning(aReporter, AeMessages.format("AeWSDLPolicyHelper.UNRESOLVED_REF", aPolicyRef.getReferenceURI())); //$NON-NLS-1$
      }
      
      return policy;
   }

   /**
    * Resolves and returns the IAePolicy element at the location specified in the URI attribute 
    * of a wsp:PolicyReference
    * 
    * For example, the namespace URI of "http://www.fabrikam123.com/policies#RmPolicy" 
    * is "http://www.fabrikam123.com/policies" and the wsu:Id is "RmPolicy"
    * 
    * If there's a null or empty namespace on the URI we'll just do the lookup using the id 
    * 
    * @param aProvider WSDL provider
    * @param aPolicyRef Policy Reference
    */
   public static List getPolicy(IAeContextWSDLProvider aProvider, IAePolicyReference aPolicyRef, IAeBaseErrorReporter aReporter)
   {
      if (aProvider == null)
      {
         return null;
      }

      if (aPolicyRef == null)
      {
         return null;
      }
    
      List policyMatches = new ArrayList();
      for (Iterator it = aProvider.getWSDLIterator(aPolicyRef.getNamespaceURI()); it.hasNext();)
      {
         AeBPELExtendedWSDLDef def = (AeBPELExtendedWSDLDef) aProvider.dereferenceIteration(it.next());
         IAePolicy policy = def.getPolicy(aPolicyRef.getReferenceId());
         if (policy != null)
         {
            policyMatches.add(policy);
         }
      }
      
      if (policyMatches.size() == 0)
      {
         reportWarning(aReporter, AeMessages.format("AeWSDLPolicyHelper.UNRESOLVED_REF", aPolicyRef.getReferenceURI())); //$NON-NLS-1$
      }
      else if (policyMatches.size() > 1)
      {
         reportWarning(aReporter, AeMessages.format("AeWSDLPolicyHelper.MULTIPLE_REFS", aPolicyRef.getReferenceURI())); //$NON-NLS-1$
      }
            
      return policyMatches;
   }
   
   /**
    * Returns the list of effective policy elements after resolving external policy references
    * 
    * @param aProvider
    * @param aPolicyElementList
    */
   public static List resolvePolicyReferences(IAeContextWSDLProvider aProvider, List aPolicyElementList)
   {
      return resolvePolicyReferences(aProvider, aPolicyElementList, null);      
   }
   
   /**
    * EndpointReference Policy elements may contain references to external policies that need to
    * be resolved before mapping
    * 
    * Note that references are resolved one layer deep.  If the referenced policy
    * contains references to yet another policy, a custom mapper would be needed to
    * handle the resolution beyond the first reference. 
    * 
    * @param aProvider
    * @param aPolicyElementList
    * @param aReporter
    * @return list of resolved policy references as elements
    */
   public static List resolvePolicyReferences(IAeContextWSDLProvider aProvider, List aPolicyElementList, IAeBaseErrorReporter aReporter)
   {
      List resolved = new ArrayList();
      if (AeUtil.isNullOrEmpty(aPolicyElementList))
      {
         return Collections.EMPTY_LIST;
      }
      
      for (Iterator it = aPolicyElementList.iterator(); it.hasNext();) 
      {
         IAePolicy policy = new AePolicyImpl((Element) it.next());
         List policies = getPolicies(aProvider, policy, aReporter );
         if (!AeUtil.isNullOrEmpty(policies))
         {
            for (Iterator p = policies.iterator(); p.hasNext();)
            {
               resolved.add(((IAePolicy) p.next()).getPolicyElement());
            }
         }
      }
      return resolved;
   }
   
   /**
    * Returns a list of policy elements which match the given policy name 
    * @param aPolicyList The list of all policies on endpoint
    * @param aPolicyName The policy name we are looking for
    */
   public static List getPolicyElements(List aPolicyList, QName aPolicyName)
   {
      List elements = new ArrayList();
      for (Iterator iter=aPolicyList.iterator(); iter.hasNext();)
      {
         Element e = AeXmlUtil.findSubElement((Element)iter.next(), aPolicyName);
         if (e != null)
            elements.add(e);
      }
      
      return elements;
   }

   /**
    * Returns the policy which matches the given policy name or null if not defined.
    * @param aPolicyList The list of all policies on endpoint
    * @param aPolicyName The policy name we are looking for
    */
   public static Element getPolicyElement(List aPolicyList, QName aPolicyName)
   {
      for (Iterator iter=aPolicyList.iterator(); iter.hasNext();)
      {
         Element e = AeXmlUtil.findSubElement((Element)iter.next(), aPolicyName);
         if (e != null)
            return e;
      }
      return null;
   }
   
   /**
    * Utility that reports policy resolution warnings
    * @param aReporter
    * @param aMessage
    */
   private static void reportWarning(IAeBaseErrorReporter aReporter, String aMessage)
   {
      if (aReporter != null)
         aReporter.addWarning(aMessage, null, null);
      else
         AeException.logWarning(aMessage);
   }

}