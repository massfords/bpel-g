//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeBPWSDefMessagePartsMapVisitor.java,v 1.3 2008/01/11 19:31:16 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import java.util.Iterator;

import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;

/**
 * Visits each of the wsio activities and inlines the message part information. 
 * This version differs from the WS-BPEL implementation in that it needs to 
 * account for the operation overloading. Operation overloading was supported as 
 * part of BPEL4WS but was disallowed by WS-i Basic Profile and removed from the 
 * WS-BPEL specification. 
 */
public class AeBPWSDefMessagePartsMapVisitor extends AeAbstractDefMessagePartsMapVisitor
{
   /** Empty QName object with an empty selection localPart */
   public static final QName EMPTY_QNAME = new QName("", "(none)");  //$NON-NLS-1$ //$NON-NLS-2$

   /**
    * Constructs the visitor with the given WSDL provider.
    *
    * @param aWSDLProvider
    */
   public AeBPWSDefMessagePartsMapVisitor(IAeContextWSDLProvider aWSDLProvider)
   {
      super(aWSDLProvider);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
    */
   public void visit(AeActivityInvokeDef aDef)
   {
      QName portType = getPartnerRolePortType(aDef);
      String operation = aDef.getOperation();
      String locationPath = aDef.getLocationPath();
   
      // message types are only used for BPWS processes, left the call in the base class to leverage the visit() methods
      QName requestMessageType = getMessageType(aDef, aDef.getInputVariable());
      QName responseMessageType = getMessageType(aDef, aDef.getOutputVariable());
      
      // If the outputVariable wasn't specified, then it's either a one-way 
      // operation or it's an error. Passing the EMPTY_QNAME into the method 
      // below will match on a one-way operation or report an error if it's a 
      // request-response style operation and the response is required. This is 
      // consistent with the validation logic that was in place for the previous 
      // releases for BPEL4WS so it will remain in effect for older processes.
      if (responseMessageType == null)
      {
         responseMessageType = EMPTY_QNAME;
      }

      if (aDef.getProducerMessagePartsMap() == null)
      {
         AeMessagePartsMap inputMap = createInputMessagePartsMap(portType, operation, requestMessageType, responseMessageType, locationPath);
         aDef.setProducerMessagePartsMap(inputMap);
      }
   
      if (aDef.getConsumerMessagePartsMap() == null)
      {
         AeMessagePartsMap outputMap = createOutputMessagePartsMap(portType, operation, requestMessageType, responseMessageType, locationPath);
         aDef.setConsumerMessagePartsMap(outputMap);
      }
   
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefMessagePartsMapVisitor#createFaultMessagePartsMap(javax.xml.namespace.QName, java.lang.String, javax.xml.namespace.QName, java.lang.String)
    */
   protected AeMessagePartsMap createFaultMessagePartsMap(QName aPortType, String aOperation, QName aFaultName, String aLocationPath)
   {
      Message message = getFaultVarType(getWSDLProvider(), aPortType, aOperation, aFaultName);
      return (message == null) ? null : createMessagePartsMap(message, aLocationPath);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefMessagePartsMapVisitor#createInputMessagePartsMap(javax.xml.namespace.QName, java.lang.String, javax.xml.namespace.QName, javax.xml.namespace.QName, java.lang.String)
    */
   protected AeMessagePartsMap createInputMessagePartsMap(QName aPortType, String aOperation, QName aRequestMessageType, QName aResponseMessageType, String aLocationPath)
   {
      AeBPELExtendedWSDLDef def = getWSDLDefinition(getWSDLProvider(), aPortType, aOperation, aRequestMessageType, aResponseMessageType);
      if (def != null)
      {
         Message message = def.getMessage(aRequestMessageType);
         if (message != null)
            return createMessagePartsMap(message, aLocationPath);
      }
      return null;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefMessagePartsMapVisitor#createOutputMessagePartsMap(javax.xml.namespace.QName, java.lang.String, javax.xml.namespace.QName, javax.xml.namespace.QName, java.lang.String)
    */
   protected AeMessagePartsMap createOutputMessagePartsMap(QName aPortType, String aOperation, QName aRequestMessageType, QName aResponseMessageType, String aLocationPath)
   {
      AeBPELExtendedWSDLDef def = getWSDLDefinition(getWSDLProvider(), aPortType, aOperation, aRequestMessageType, aResponseMessageType);
      if (def != null)
      {
         Message message = def.getMessage(aResponseMessageType);
         if (message != null)
            return createMessagePartsMap(message, aLocationPath);
      }
      return null;
   }
   
   /**
    * Returns the WSDL definition containing the given portType/operation 
    * combination with the specified input/output variables, or null if not 
    * found.
    * 
    * This method accounts for operation overloading.
    * 
    * @param aProvider The IAeContextWSDLProvider instance to use for lookup.
    * @param aPortType The portType QName.
    * @param aOperation The operation name.
    * @param aInput The name of the input variable, or null.
    * @param aOutput The name of the output variable, or null.
    * 
    * @return AeBPELExtendedWSDLDef
    */   
   private AeBPELExtendedWSDLDef getWSDLDefinition(IAeContextWSDLProvider aProvider, 
                                                   QName aPortType, String aOperation, 
                                                   QName aInput, QName aOutput )
   {
      // Note: This implementation does not catch the problem of having an 
      //       overloaded operation and a BPEL process that has a mismatched 
      //       receive/reply. It seems possible that a process could have a 
      //       receive which complies with the input data for variant 1 of an 
      //       overloaded operation and a reply which complies with the output 
      //       data for variant 2 of the overloaded operation.
      AeBPELExtendedWSDLDef def = AeWSDLDefHelper.getWSDLDefinitionForPortType( aProvider, aPortType );
      if ( def != null )
      {
         for ( Iterator iter = def.getOperations( aPortType ) ; iter.hasNext() ; )
         {
            // Check for multiple operation matches, since the operations can 
            //  be overloaded with different input and/or output variables.
            //
            Operation op = (Operation)iter.next(); 
            if ( op.getName().equals( aOperation ))
            {
               if ( aInput != null )
               {
                  if ( op.getInput() != null && op.getInput().getMessage() != null &&
                        !AeUtil.compareObjects( aInput, op.getInput().getMessage().getQName()))
                     // no input match - keep looking
                     continue ;
               }
               
               if ( aOutput != null )
               {
                  if ( op.getOutput() != null && op.getOutput().getMessage() != null && 
                       !AeUtil.compareObjects( aOutput, op.getOutput().getMessage().getQName()))
                     // no output match - keep looking
                     continue ;
               }

               return def;
            }
         }
      }

      return null;
   }

   /**
    * Returns the WSDL definition containing the given portType/operation/fault 
    * combination with the specified fault variable, or null if not found.
    * 
    * This method accounts for operation overloading.
    * 
    * @param aProvider The IAeContextWSDLProvider instance to use for lookup.
    * @param aPortType The portType QName.
    * @param aOperation The operation name.
    * @param aFault The name of the fault.
    * 
    * @return Message The fault variable's message or null if FAULT not found.
    */
   private Message getFaultVarType(IAeContextWSDLProvider aProvider, 
                                   QName aPortType, String aOperation,   
                                   QName aFault)
   {
      AeBPELExtendedWSDLDef def = AeWSDLDefHelper.getWSDLDefinitionForPortType( aProvider, aPortType );
      if ( def != null )
      {
         for ( Iterator iter = def.getOperations( aPortType ) ; iter.hasNext() ; )
         {
            // Check for multiple operation matches, since the operations can 
            //  be overloaded with different input and/or output variables.
            //
            Operation op = (Operation)iter.next(); 
            if ( op.getName().equals( aOperation ))
            {
               // Find associated fault.
               //
               Fault fault = op.getFault( aFault.getLocalPart() );
               if ( fault != null )
               {
                  // Find associated msg type.
                  //
                  if ( fault.getMessage().getQName() == null )
                     return null;
                  else
                     return fault.getMessage();
               }
            }
         }
      }

      return null;
   }
}