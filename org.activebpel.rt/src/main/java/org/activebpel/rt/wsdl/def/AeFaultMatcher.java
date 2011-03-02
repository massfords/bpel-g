//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/AeFaultMatcher.java,v 1.1 2007/06/18 13:06:36 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Utility class that determines the proper Fault defintion for a given port type, operation and 
 * input data. It will match the data against the defined fault messages to figure out 
 * what fault is being thrown
 */
public class AeFaultMatcher 
{
   private String mOpName;
   private QName mPortTypeQName;
   private Operation mOperation;
   private Fault mFault;
   private QName mCode;
   private String mReason;
   private Element mDetail;

   /**
    * Constructor that accepts the necessary components to determine the proper fault definition 
    *  
    * @param aPortTypeName
    * @param aOperation
    * @param aFaultCode
    * @param aDetailElement
    */
   public AeFaultMatcher(QName aPortTypeName, Operation aOperation, QName aFaultCode, Element aDetailElement)
   {
      mPortTypeQName = aPortTypeName;
      mCode = aFaultCode;
      mDetail = aDetailElement;
      setOperation(aOperation);
      
      mFault = resolveToWsdlFault(aPortTypeName.getNamespaceURI(), aOperation, aFaultCode, aDetailElement);
   }

   /**
    * Constructor that will resolve and extract the components necessary to determine proper fault definition
    * before matching the fault
    * 
    * @param aPortTypeDef
    * @param aPortTypeName
    * @param aOperationName
    * @param aFaultElement
    */
   public AeFaultMatcher(AeBPELExtendedWSDLDef aPortTypeDef, QName aPortTypeName, String aOperationName, Element aFaultElement)
   {
      mPortTypeQName = aPortTypeName;
      mCode = findFaultCode(aFaultElement);
      mDetail = findDetail(aFaultElement);
      setOperation(findOperation(aPortTypeDef, aOperationName));
      
      mFault = resolveToWsdlFault(aPortTypeName.getNamespaceURI(), getOperation(), getFaultCode(), getDetail()); 
   }
   
   /**
    * @return the wsdl operation that throws the fault
    */
   public Operation getOperation()
   {
      return mOperation;
   }
   
   /**
    * @return the operation name
    */
   protected String getOperationName()
   {
      return mOpName;
   }
   
   /**
    * Sets the wsdl operation
    * @param aOperation
    */
   protected void setOperation(Operation aOperation)
   {
      mOperation = aOperation;
      mOpName = aOperation != null ? aOperation.getName() : null;
   }
   
   /**
    * @return the name of the port type
    */
   protected QName getPortTypeQName()
   {
      return mPortTypeQName;
   }
   
   /**
    * Finds the wsdl operation definition using definition and name
    */
   protected Operation findOperation(AeBPELExtendedWSDLDef aDef, String aOperationName)
   {
      if (aDef == null)
      {
         return null;
      }
      
      PortType portType = aDef.getPortType(getPortTypeQName());
      if (portType == null)
      {
         return null;
      }
      
      return portType.getOperation(aOperationName, null, null);
   }
   
   /**
    * @return the matching WSDL fault definition
    */
   public Fault getWsdlFault()
   {
      return mFault;
   }
   
   /**
    * Returns the fault name based on the rules below.  
    * 
    * If there's a matching wsdl fault, use the name from wsdl def
    * If the code is a BPEL fault code, use the BPEL fault code
    * No matching wsdl fault, not a BPEL fault, use the type name of the first detail element, if present
    * As a last resort, we'll fall back to the fault code, which could be null.
    */
   public QName getFaultName()
   {
      Fault wsdlFault = getWsdlFault();
      String faultNamespace = (getFaultCode() != null ? getFaultCode().getNamespaceURI() : ""); //$NON-NLS-1$
      
      QName faultName = null;      
      if (wsdlFault != null)
      {
         // use the wsdl fault name 
         faultName = new QName(getPortTypeQName().getNamespaceURI(), wsdlFault.getName());
      }
      else if (IAeBPELExtendedWSDLConst.BPWS_NAMESPACE_URI.equals(faultNamespace) ||
            IAeBPELExtendedWSDLConst.WSBPEL_2_0_NAMESPACE_URI.equals(faultNamespace) )
      {
         // it's a BPEL fault (probably from us)
         faultName = getFaultCode();
      }
      else if (getDetail() != null)
      {
         // use the type name of the detail element
         faultName = AeXmlUtil.getElementType(getDetail());
      }
      else
      {
         // use the code as given (could be null)
         faultName = getFaultCode();
      }
      
      return faultName;
   }
   
   /**
    * @see org.activebpel.rt.wsdl.def.AeMessageMatcher#getMessage()
    */
   public Message getMessage()
   {
      if (getWsdlFault() == null)
      {
         return null;
      }
      return getWsdlFault().getMessage();
   }
   
   /**
    * @return the wsdl fault message name
    */
   public QName getMessageName()
   {
      if (getMessage() == null)
      {
         return null;
      }
      return getMessage().getQName();
   }
   
   /**
    * @return the fault message part name
    */
   public String getFaultPartName()
   {
      if (getMessage() == null)
      {
         return null;
      }
         
      return (String)getMessage().getParts().keySet().iterator().next();
   }
   
   /**
    * Returns true if we were able to find a match
    */
   public boolean foundMatch()
   {
      return getWsdlFault() != null;
   }
   
   /**
    * @return the first Detail element from the soap:Fault element
    */
   protected Element findDetail(Element aFault)
   {
      String namespace = aFault.getNamespaceURI();
      NodeList nodes = aFault.getElementsByTagNameNS(namespace, "Detail"); //$NON-NLS-1$
      if (nodes.getLength() == 0)
      {
         return null;
      }
      Element detail = AeXmlUtil.getFirstSubElement((Element) nodes.item(0));
      if (detail == null)
      {
         return null;
      }
      return AeXmlUtil.cloneElement(detail);
   }

   /**
    * @return the Detail element from the fault message
    */
   public Element getDetail()
   {
      return mDetail;
   }
   
   /**
    * Setter for the detail element
    * @param aDetail
    */
   protected void setDetail(Element aDetail)
   {
      mDetail = aDetail;
   }

   /**
    * @return the fault code QName from a soap:Fault element
    */
   protected QName findFaultCode(Element aFault)
   {
      if (aFault == null)
      {
         return null;
      }
      
      String namespace = aFault.getNamespaceURI();
      NodeList nodes = aFault.getElementsByTagNameNS(namespace, "Code"); //$NON-NLS-1$
      if (nodes.getLength() == 0)
      {
         return null;
      }
      Element code = (Element) nodes.item(0);
      nodes = code.getElementsByTagNameNS(namespace, "Value"); //$NON-NLS-1$
      if (nodes.getLength() == 0)
      {
         return null;
      }
      
      return AeXmlUtil.getQName((Element) nodes.item(0));
   }

   /**
    * @return the fault code from the input data
    */
   public QName getFaultCode()
   {
      return mCode;
   }
   
   /**
    * Setter for the fault code QName
    * @param aCode
    */
   protected void setFaultCode(QName aCode)
   {
      mCode = aCode;
   }
   
   /**
    * @return the fault reason text from a soap:Fault element
    */
   protected String findReason(Element aFault)
   {
      if (aFault == null)
      {
         return null;
      }
      
      String namespace = aFault.getNamespaceURI();
      NodeList nodes = aFault.getElementsByTagNameNS(namespace, "Reason"); //$NON-NLS-1$
      if (nodes.getLength() == 0)
      {
         return null;
      }
      Element code = (Element) nodes.item(0);
      nodes = code.getElementsByTagNameNS(namespace, "Text"); //$NON-NLS-1$
      if (nodes.getLength() == 0)
      {
         return null;
      }
      
      return AeXmlUtil.getText((Element) nodes.item(0));
   }

   /**
    * @return the fault reason text from the input data
    * This is only populated if the input was a soap:Fault element
    */
   public String getReason()
   {
      return mReason;
   }
   
   /**
    * Setter for the reason string
    * @param aReason
    */
   protected void setReason(String aReason)
   {
      mReason = aReason;
   }
   
   /**
    * Attempts to resolve the data to a fault defined for the operation being invoked.
    * We first attempt to match the faultcode to a fault name. Failing that, we'll examine
    * the faultdetails to see if we can match the element's type to one of the operation's
    * fault messages.
    *
    * @param aNamespaceUri namespace for the operation, used to identify the fault name
    * @param aOperation wsdl operation invoked
    * @param aFaultCode QName from the faultcode element in the fault
    * @param aFirstDetailElement The first element within the fault details
    */
   protected Fault resolveToWsdlFault(String aNamespaceUri, Operation aOperation, QName aFaultCode, Element aFirstDetailElement)
   {
      Fault wsdlFault = null;
      
      if (aOperation == null)
      {
         return null;
      }

      Map faultsMap = aOperation.getFaults();
      String namespace = aFaultCode != null ? aFaultCode.getNamespaceURI() : null;
      String localname = aFaultCode != null ? aFaultCode.getLocalPart() : null;

      // don't bother with this routine if the operation doesn't define any faults.
      if ( faultsMap != null && !faultsMap.isEmpty() )
      {
         // step 1: determine the fault name
         // try the faultCode first since this is the cleanest place to indicate the
         // fault's name
         if ( aNamespaceUri.equals(namespace) )
         {
            // the faultcode is in the same namespace as our port type, see if the
            // local part matches up to a fault name
            wsdlFault = aOperation.getFault(localname);
         }

         // step 2: fault code didn't pan out, try comparing the first fault detail
         // element to the operation's faults
         if ( wsdlFault == null && aFirstDetailElement != null )
         {
            wsdlFault = findMatchingWsdlFault(aFirstDetailElement, faultsMap.values());
         }
      }
      return wsdlFault;
   }

   /**
    * Attempts to match the element to one of the declared faults for the operation
    * invoked.
    *
    * WS-I Basic Profile 1.1 requires that fault parts are elements:
    * http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#Bindings_and_Faults
    *
    * The code here is a little flexible in this regard. It'll handle getting
    * a complex type for a fault part if it has an xsi:type attribute. Otherwise,
    * the fault detail element must be a valid schema element or we cannot
    * reliably determine what fault we should map to.
    *
    *
    * @param aFaultDetailElement The first detail element from the soap fault
    * @param aDefinedFaultsForOperationColl Collection of faults defined for the operation invoked.
    */
   protected Fault findMatchingWsdlFault(Element aFaultDetailElement, Collection aDefinedFaultsForOperationColl)
   {
      Fault wsdlFault = null;
      
      if (aFaultDetailElement == null)
      {
         return null;
      }
      
      // TODO (MF) add a test with a type-based fault
      boolean isType = true;

      QName type = AeXmlUtil.getElementType(aFaultDetailElement);
      if ( AeUtil.isNullOrEmpty(aFaultDetailElement.getNamespaceURI()) )
      {
         // if the element was in the empty namespace, check to see if it has an
         // xsi:type attribute. This isn't strictly allowed under ws-i basic profile
         // but is being done here since there may be a number of services that aren't
         // compliant and we should attempt to achieve interoperability where possible.
         type = AeXmlUtil.getXSIType(aFaultDetailElement);
      }
      else
      {
         // we found a schema element, set the flag to indicate such
         isType = false;
      }

      if ( type != null )
      {
         // we found a type. walk all of the existing faults and stop at the
         // first one who has a message that matches our type.
         for (Iterator iter = aDefinedFaultsForOperationColl.iterator(); iter.hasNext();)
         {
            Fault possibleMatch = (Fault)iter.next();
            Message msg = possibleMatch.getMessage();
            // fault messages will only have a single part
            Map partsMap = msg.getParts();
            if ( partsMap != null && partsMap.size() == 1 )
            {
               Part part = (Part)msg.getParts().values().iterator().next();
               // if we found a complex type, then compare its qname with the part's type='...'
               // if we found an element, then compare its qname with the part's element='...'
               if ( isMatch(isType, type, part) )
               {
                  wsdlFault = possibleMatch;
                  break;
               }
            }
         }
      }
      return wsdlFault;
   }

   /**
    * Determines if the part we're inspecting matches the type that we found in
    * the fault details.
    *
    * @param aTypeFlag true if QName we found maps to a type, false if it maps to a schema element.
    * @param aType qname of the type we're looking for
    * @param aPart wsdl part from the fault message
    */
   protected boolean isMatch(boolean aTypeFlag, QName aType, Part aPart)
   {
      QName partType = aTypeFlag ? aPart.getTypeName() : aPart.getElementName();
      return aType.equals(partType);
   }
}
