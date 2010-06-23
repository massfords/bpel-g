//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeAbstractDefMessagePartsMapVisitor.java,v 1.3 2008/01/11 19:31:16 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import javax.wsdl.Message;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.def.AeActivityPartnerLinkBaseDef;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReplyDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;

/**
 * Base visitor that visits each wsio activity and inlines the message parts data
 * for the message being sent or received. If the visitor can't locate the message
 * parts then the activity will either get a null or the visitor will throw, depending
 * on the parameter to {@link #assignMessagePartsMaps} 
 */
public abstract class AeAbstractDefMessagePartsMapVisitor extends AeAbstractDefVisitor implements IAeDefMessagePartsMapVisitor
{
   /** The WSDL provider set during visitor creation. */
   protected IAeContextWSDLProvider mWSDLProvider;
   /** <code>true</code> if and only if the visitor encountered one or more errors. */
   private boolean mHasErrors;
   
   /**
    * Constructs the visitor with the given WSDL provider.
    *
    * @param aWSDLProvider
    */
   public AeAbstractDefMessagePartsMapVisitor(IAeContextWSDLProvider aWSDLProvider)
   {
      setWSDLProvider(aWSDLProvider);

      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
   }

   /**
    * Traverses the given process definition and assigns message parts maps to
    * web service activities.
    *
    * @param aDef
    * @param aThrowOnErrorsFlag
    */
   public void assignMessagePartsMaps(AeProcessDef aDef, boolean aThrowOnErrorsFlag) throws AeBusinessProcessException
   {
      aDef.accept(this);

      if (aThrowOnErrorsFlag && hasErrors())
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeAbstractDefMessagePartsMapVisitor.ERROR_HasErrors")); //$NON-NLS-1$
      }
   }

   /**
    * Returns the <code>myRole</code> <code>portType</code> for the given
    * activity.
    */
   protected QName getMyRolePortType(AeActivityPartnerLinkBaseDef aDef)
   {
      QName portType = aDef.getPortType();
      if (portType == null)
      {
         AePartnerLinkDef partnerLinkDef = aDef.getPartnerLinkDef();
         if (partnerLinkDef != null)
         {
            portType = partnerLinkDef.getMyRolePortType();
         }
      }
   
      return portType;
   }

   /**
    * Returns the <code>myRole</code> <code>portType</code> for the given
    * <code>onMessage</code>.
    */
   protected QName getMyRolePortType(AeOnMessageDef aDef)
   {
      QName portType = aDef.getPortType();
      if (portType == null)
      {
         AePartnerLinkDef partnerLinkDef = aDef.getPartnerLinkDef();
         if (partnerLinkDef != null)
         {
            portType = partnerLinkDef.getMyRolePortType();
         }
      }
   
      return portType;
   }

   /**
    * Returns the <code>partnerRole</code> <code>portType</code> for the given
    * activity.
    */
   protected QName getPartnerRolePortType(AeActivityPartnerLinkBaseDef aDef)
   {
      QName portType = aDef.getPortType();
      if (portType == null)
      {
         AePartnerLinkDef partnerLinkDef = aDef.getPartnerLinkDef();
         if (partnerLinkDef != null)
         {
            portType = partnerLinkDef.getPartnerRolePortType();
         }
      }
   
      return portType;
   }

   /**
    * Returns a {@link AeMessagePartsMap} for the given WSDL <code>Message</code>.
    *
    * @param aMessage
    * @param aLocationPath BPEL object location path for error messages
    */
   protected AeMessagePartsMap createMessagePartsMap(Message aMessage, String aLocationPath)
   {
      try
      {
         // TODO (MF) can we avoid searching for the message def?
         AeBPELExtendedWSDLDef wsdlDefinition = AeWSDLDefHelper.getWSDLDefinitionForMsg(getWSDLProvider(), aMessage.getQName());
         if (wsdlDefinition == null)
         {
            throw new AeException(AeMessages.format("AeAbstractDefMessagePartsMapVisitor.ERROR_UnknownWSDL", aMessage.getQName())); //$NON-NLS-1$
         }
   
         return AeMessagePartsMap.createMessagePartsMap(aMessage, wsdlDefinition);
      }
      catch (AeException e)
      {
         setHasErrors();
   
         AeException.logError(e, AeMessages.format("AeAbstractDefMessagePartsMapVisitor.ERROR_MessagePartsMap", aLocationPath)); //$NON-NLS-1$
         return null;
      }
   }

   /**
    * Gets the messageType for the given variable name or null if the variable
    * is not a message variable or wasn't specified. This is used to differentiate
    * the operations where operation overloading is permitted.
    * @param aDef
    * @param varName
    */
   protected QName getMessageType(AeBaseDef aDef, String varName)
   {
      QName messageType = null;
      if (AeUtil.notNullOrEmpty(varName))
      {
         AeVariableDef def = AeDefUtil.getVariableByName(varName, aDef);
         if (def != null)
            messageType = def.getMessageType();
      }
      return messageType;
   }

   /**
    * Returns a {@link AeMessagePartsMap} for the input message corresponding to
    * the given port type and operation.
    *
    * @param aPortType
    * @param aOperation
    * @param aRequestMessageType - used to differentiate the operations where operation overloading is permitted.
    * @param aResponseMessageType - used to differentiate the operations where operation overloading is permitted
    * @param aLocationPath BPEL object location path for error messages
    */
   // TODO (MF) These create methods require multiple passes through the WSDL provider. Seems like this could be changed to only require a single pass through the provider to locate the wsdl and message part info.
   protected abstract AeMessagePartsMap createInputMessagePartsMap(QName aPortType, String aOperation, QName aRequestMessageType, QName aResponseMessageType, String aLocationPath);

   /**
    * Returns a {@link AeMessagePartsMap} for the output message corresponding
    * to the given port type and operation.
    *
    * @param aPortType
    * @param aOperation
    * @param aRequestMessageType - used to differentiate the operations where operation overloading is permitted.
    * @param aResponseMessageType - used to differentiate the operations where operation overloading is permitted.
    * @param aLocationPath BPEL object location path for error messages
    */
   protected abstract AeMessagePartsMap createOutputMessagePartsMap(QName aPortType, String aOperation, QName aRequestMessageType, QName aResponseMessageType, String aLocationPath);

   /**
    * Returns a {@link AeMessagePartsMap} for the message corresponding to the
    * given port type, operation, and fault.
    *
    * @param aPortType
    * @param aOperation
    * @param aFaultName
    * @param aLocationPath BPEL object location path for error messages
    */
   protected abstract AeMessagePartsMap createFaultMessagePartsMap(QName aPortType, String aOperation, QName aFaultName, String aLocationPath);

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef aDef)
   {
      if (aDef.getConsumerMessagePartsMap() == null)
      {
         QName portType = getMyRolePortType(aDef);
         String operation = aDef.getOperation();
         String locationPath = aDef.getLocationPath();
         // msgType is only used for BPWS processes, left the call in the base class to leverage the visit() methods
         QName msgType = getMessageType(aDef, aDef.getVariable());
         
         AeMessagePartsMap map = createInputMessagePartsMap(portType, operation, msgType, null, locationPath);
         aDef.setConsumerMessagePartsMap(map);
      }
   
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
    */
   public void visit(AeActivityReplyDef aDef)
   {
      if (aDef.getProducerMessagePartsMap() == null)
      {
         QName portType = getMyRolePortType(aDef);
         String operation = aDef.getOperation();
         QName faultName = aDef.getFaultName();
         String locationPath = aDef.getLocationPath();
   
         AeMessagePartsMap outputMap;
         
         if (faultName != null)
         {
            outputMap = createFaultMessagePartsMap(portType, operation, faultName, locationPath);
         }
         else
         {
            // msgType is only used for BPWS processes, left the call in the base class to leverage the visit() methods
            QName msgType = getMessageType(aDef, aDef.getVariable());
            outputMap = createOutputMessagePartsMap(portType, operation, null, msgType, locationPath);
         }
   
         aDef.setProducerMessagePartsMap(outputMap);
      }
   
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef aDef)
   {
      if (aDef.getConsumerMessagePartsMap() == null)
      {
         QName portType = getMyRolePortType(aDef);
         String operation = aDef.getOperation();
         String locationPath = aDef.getLocationPath();
         // msgType is only used for BPWS processes, left the call in the base class to leverage the visit() methods
         QName msgType = getMessageType(aDef, aDef.getVariable());
   
         AeMessagePartsMap map = createInputMessagePartsMap(portType, operation, msgType, null, locationPath);
         aDef.setConsumerMessagePartsMap(map);
      }
   
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      visit((AeOnMessageDef) aDef);
   }

   /**
    * Setter for the provider
    * @param aProvider
    */
   protected void setWSDLProvider(IAeContextWSDLProvider aProvider)
   {
      mWSDLProvider = aProvider;
   }

   /**
    * Returns the WSDL provider.
    */
   protected IAeContextWSDLProvider getWSDLProvider()
   {
      return mWSDLProvider;
   }

   /**
    * Returns <code>true</code> if and only if this visitor encountered one or
    * more errors.
    */
   protected boolean hasErrors()
   {
      return mHasErrors;
   }

   /**
    * Indicates that this visitor encountered one or more errors.
    */
   protected void setHasErrors()
   {
      mHasErrors = true;
   }
}
 