// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/admin/server/AeActiveBpelAdminImpl.java,v 1.8 2008/02/17 21:29:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.admin.server;

import java.rmi.RemoteException;

import org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin;
import org.activebpel.rt.axis.bpel.admin.types.AdminFault;
import org.activebpel.rt.axis.bpel.admin.types.AesAddAttachmentDataType;
import org.activebpel.rt.axis.bpel.admin.types.AesAddAttachmentResponseType;
import org.activebpel.rt.axis.bpel.admin.types.AesBreakpointRequestType;
import org.activebpel.rt.axis.bpel.admin.types.AesCompleteActivityType;
import org.activebpel.rt.axis.bpel.admin.types.AesConfigurationType;
import org.activebpel.rt.axis.bpel.admin.types.AesDeployBprType;
import org.activebpel.rt.axis.bpel.admin.types.AesDigestType;
import org.activebpel.rt.axis.bpel.admin.types.AesEngineRequestType;
import org.activebpel.rt.axis.bpel.admin.types.AesGetVariableDataType;
import org.activebpel.rt.axis.bpel.admin.types.AesProcessDetailType;
import org.activebpel.rt.axis.bpel.admin.types.AesProcessFilterType;
import org.activebpel.rt.axis.bpel.admin.types.AesProcessInstanceDetail;
import org.activebpel.rt.axis.bpel.admin.types.AesProcessListType;
import org.activebpel.rt.axis.bpel.admin.types.AesProcessObjectType;
import org.activebpel.rt.axis.bpel.admin.types.AesProcessRequestType;
import org.activebpel.rt.axis.bpel.admin.types.AesProcessType;
import org.activebpel.rt.axis.bpel.admin.types.AesRemoveAttachmentDataType;
import org.activebpel.rt.axis.bpel.admin.types.AesRemoveBreakpointRequestType;
import org.activebpel.rt.axis.bpel.admin.types.AesRetryActivityType;
import org.activebpel.rt.axis.bpel.admin.types.AesSetCorrelationType;
import org.activebpel.rt.axis.bpel.admin.types.AesSetPartnerLinkType;
import org.activebpel.rt.axis.bpel.admin.types.AesSetVariableDataType;
import org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType;
import org.activebpel.rt.axis.bpel.admin.types.AesVoidType;
import org.activebpel.rt.axis.bpel.handlers.AeAttachmentUtil;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.server.admin.rdebug.server.AeAddAttachmentResponse;
import org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.wsio.AeWebServiceAttachment;
import org.apache.axis.MessageContext;

public class AeActiveBpelAdminImpl implements IAeActiveBpelAdmin
{
   /** The interface which exposes engine API calls */
   private IAeBpelAdmin mAdmin;

   /**
    * Constructor.
    */
   public AeActiveBpelAdminImpl()
   {
      mAdmin = AeEngineFactory.getRemoteDebugImpl();
   }   
   
   /**
    * Getter for the BPEL Admin object.
    */
   protected IAeBpelAdmin getAdmin()
   {
      return mAdmin;
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#getConfiguration(org.activebpel.rt.axis.bpel.admin.types.AesVoidType)
    */
   public AesConfigurationType getConfiguration(AesVoidType aInput) throws RemoteException, AdminFault
   {
      try
      {
         return new AesConfigurationType(getAdmin().getConfiguration());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new AdminFault(e.getLocalizedMessage(), e.getCause().getLocalizedMessage());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#setConfiguration(org.activebpel.rt.axis.bpel.admin.types.AesConfigurationType)
    */
   public void setConfiguration(AesConfigurationType aInput) throws RemoteException, AdminFault
   {
      try
      {
         getAdmin().setConfiguration(aInput.getXmlString());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new AdminFault(e.getLocalizedMessage(), e.getCause().getLocalizedMessage());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#suspendProcess(org.activebpel.rt.axis.bpel.admin.types.AesProcessType)
    */
   public void suspendProcess(AesProcessType aInput) throws RemoteException, AdminFault
   {
      try
      {
         getAdmin().suspendProcess(aInput.getPid());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new AdminFault(e.getLocalizedMessage(), e.getCause().getLocalizedMessage());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#resumeProcess(org.activebpel.rt.axis.bpel.admin.types.AesProcessType)
    */
   public void resumeProcess(AesProcessType aInput) throws RemoteException, AdminFault
   {
      try
      {
         getAdmin().resumeProcess(aInput.getPid());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new AdminFault(e.getLocalizedMessage(), e.getCause().getLocalizedMessage());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#resumeProcessObject(org.activebpel.rt.axis.bpel.admin.types.AesProcessObjectType)
    */
   public void resumeProcessObject(AesProcessObjectType aInput) throws RemoteException, AdminFault
   {
      try
      {
         getAdmin().resumeProcessObject(aInput.getPid(), aInput.getLocation());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new AdminFault(e.getLocalizedMessage(), e.getCause().getLocalizedMessage());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#restartProcess(org.activebpel.rt.axis.bpel.admin.types.AesProcessType)
    */
   public void restartProcess(AesProcessType aInput) throws RemoteException, AdminFault
   {
      try
      {
         getAdmin().restartProcess(aInput.getPid());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new AdminFault(e.getLocalizedMessage(), e.getCause().getLocalizedMessage());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#terminateProcess(org.activebpel.rt.axis.bpel.admin.types.AesProcessType)
    */
   public void terminateProcess(AesProcessType aInput) throws RemoteException, AdminFault
   {
      try
      {
         getAdmin().terminateProcess(aInput.getPid());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new AdminFault(e.getLocalizedMessage(), e.getCause().getLocalizedMessage());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#addEngineListener(org.activebpel.rt.axis.bpel.admin.types.AesEngineRequestType)
    */
   public void addEngineListener(AesEngineRequestType aInput) throws RemoteException
   {
      try
      {
         getAdmin().addEngineListener(aInput.getCid(), aInput.getEndpointURL());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#addBreakpointListener(org.activebpel.rt.axis.bpel.admin.types.AesBreakpointRequestType)
    */
   public void addBreakpointListener(AesBreakpointRequestType aInput) throws RemoteException
   {
      try
      {
         getAdmin().addBreakpointListener(aInput.getCid(), aInput.getEndpointURL(), AesTypeConversionHelper.convertBreakpoints(aInput.getBreakpointList()));
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#updateBreakpointList(org.activebpel.rt.axis.bpel.admin.types.AesBreakpointRequestType)
    */
   public void updateBreakpointList(AesBreakpointRequestType aInput) throws RemoteException
   {
      try
      {
         getAdmin().updateBreakpointList(aInput.getCid(), aInput.getEndpointURL(), AesTypeConversionHelper.convertBreakpoints(aInput.getBreakpointList()));
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#removeEngineListener(org.activebpel.rt.axis.bpel.admin.types.AesEngineRequestType)
    */
   public void removeEngineListener(AesEngineRequestType aInput) throws RemoteException
   {
      try
      {
         getAdmin().removeEngineListener(aInput.getCid(), aInput.getEndpointURL());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#removeBreakpointListener(org.activebpel.rt.axis.bpel.admin.types.AesRemoveBreakpointRequestType)
    */
   public void removeBreakpointListener(AesRemoveBreakpointRequestType aInput) throws RemoteException
   {
      try
      {
         getAdmin().removeBreakpointListener(aInput.getCid(), aInput.getEndpointURL());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#addProcessListener(org.activebpel.rt.axis.bpel.admin.types.AesProcessRequestType)
    */
   public void addProcessListener(AesProcessRequestType aInput) throws RemoteException
   {
      try
      {
         getAdmin().addProcessListener(aInput.getCid(), aInput.getPid(), aInput.getEndpointURL());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#removeProcessListener(org.activebpel.rt.axis.bpel.admin.types.AesProcessRequestType)
    */
   public void removeProcessListener(AesProcessRequestType aInput) throws RemoteException
   {
      try
      {
         getAdmin().removeProcessListener(aInput.getCid(), aInput.getPid(), aInput.getEndpointURL());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#getVariable(org.activebpel.rt.axis.bpel.admin.types.AesGetVariableDataType)
    */
   public AesStringResponseType getVariable(AesGetVariableDataType aInput) throws RemoteException
   {
      try
      {
         return new AesStringResponseType(getAdmin().getVariable(aInput.getPid(), aInput.getVariablePath()));
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#setVariable(org.activebpel.rt.axis.bpel.admin.types.AesSetVariableDataType)
    */
   public AesStringResponseType setVariable(AesSetVariableDataType aInput) throws RemoteException
   {
      try
      {
         return new AesStringResponseType(getAdmin().setVariable(aInput.getPid(), aInput.getVariablePath(), aInput.getVariableData()));
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }
   
  /**
   * Note: The attachment part is not used. It is generated as part the ActiveBpelAdmin.wsdl WS-I Basic compliance.
   * The attachments are transferred in a Multipart SOAP with attachments message. 
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#addAttachment(org.activebpel.rt.axis.bpel.admin.types.AesAddAttachmentDataType, byte[])
    */
   public AesAddAttachmentResponseType addAttachment(AesAddAttachmentDataType aInput, byte[] aIgnored) throws RemoteException
   {
      try
      {
         MessageContext axisContext = MessageContext.getCurrentContext();
         AeWebServiceAttachment attachment = ((AeWebServiceAttachment)(AeAttachmentUtil.soap2wsioAttachments(axisContext.getCurrentMessage()).get(0)));
         AeAddAttachmentResponse response = getAdmin().addAttachment(aInput.getPid(), aInput.getVariablePath(), attachment);
         return new AesAddAttachmentResponseType(response.getAttachmentId(),AesTypeConversionHelper.convertAttachmentAttributes(response.getAttachmentAttributes()));
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#removeAttachments(org.activebpel.rt.axis.bpel.admin.types.AesRemoveAttachmentDataType)
    */
   public AesStringResponseType removeAttachments(AesRemoveAttachmentDataType aInput) throws RemoteException
   {
      try
      {
         return new AesStringResponseType(getAdmin().removeAttachments(aInput.getPid(), aInput.getVariablePath(), AesTypeConversionHelper.convertAttachmentItemNumbers(aInput.getItemNumbers())));
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }
   
   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#getProcessList(org.activebpel.rt.axis.bpel.admin.types.AesProcessFilterType)
    */
   public AesProcessListType getProcessList(AesProcessFilterType aInput) throws RemoteException
   {
      try
      {
         AeProcessFilter filter = AesTypeConversionHelper.convertProcessFilter(aInput.getFilter());
         return new AesProcessListType(AesTypeConversionHelper.convertProcessListResult(getAdmin().getProcessList(filter)));
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#getProcessDetail(org.activebpel.rt.axis.bpel.admin.types.AesProcessType)
    */
   public AesProcessDetailType getProcessDetail(AesProcessType aInput) throws RemoteException
   {
      try
      {
         AesProcessInstanceDetail detail = AesTypeConversionHelper.convertProcessDetail(getAdmin().getProcessDetail(aInput.getPid()));
         return new AesProcessDetailType(detail);
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#getProcessState(org.activebpel.rt.axis.bpel.admin.types.AesProcessType)
    */
   public AesStringResponseType getProcessState(AesProcessType aInput) throws RemoteException
   {
      try
      {
         return new AesStringResponseType(getAdmin().getProcessState(aInput.getPid()));
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#getProcessDigest(org.activebpel.rt.axis.bpel.admin.types.AesProcessType)
    */
   public AesDigestType getProcessDigest(AesProcessType aInput) throws RemoteException
   {
      try
      {
         return new AesDigestType(getAdmin().getProcessDigest(aInput.getPid()));
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#getProcessDef(org.activebpel.rt.axis.bpel.admin.types.AesProcessType)
    */
   public AesStringResponseType getProcessDef(AesProcessType aInput) throws RemoteException
   {
      try
      {
         return new AesStringResponseType(getAdmin().getProcessDef(aInput.getPid()));
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#getProcessLog(org.activebpel.rt.axis.bpel.admin.types.AesProcessType)
    */
   public AesStringResponseType getProcessLog(AesProcessType aInput) throws RemoteException
   {
      try
      {
         return new AesStringResponseType(getAdmin().getProcessLog(aInput.getPid()));
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#getAPIVersion(org.activebpel.rt.axis.bpel.admin.types.AesVoidType)
    */
   public AesStringResponseType getAPIVersion(AesVoidType aInput) throws RemoteException
   {
      try
      {
         return new AesStringResponseType(getAdmin().getAPIVersion());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#deployBpr(org.activebpel.rt.axis.bpel.admin.types.AesDeployBprType)
    */
   public AesStringResponseType deployBpr(AesDeployBprType aInput) throws RemoteException
   {
      try
      {
         return new AesStringResponseType(getAdmin().deployBpr(aInput.getBprFilename(), aInput.getBase64File()));
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#setPartnerLinkData(org.activebpel.rt.axis.bpel.admin.types.AesSetPartnerLinkType)
    */
   public void setPartnerLinkData(AesSetPartnerLinkType aInput) throws RemoteException
   {
      try
      {
         getAdmin().setPartnerLinkData(aInput.getPid(), aInput.isPartnerRole(), aInput.getLocationPath(), aInput.getData());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#setCorrelationSetData(org.activebpel.rt.axis.bpel.admin.types.AesSetCorrelationType)
    */
   public void setCorrelationSetData(AesSetCorrelationType aInput) throws RemoteException
   {
      try
      {
         getAdmin().setCorrelationSetData(aInput.getPid(), aInput.getLocationPath(), aInput.getData());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#retryActivity(org.activebpel.rt.axis.bpel.admin.types.AesRetryActivityType)
    */
   public void retryActivity(AesRetryActivityType aInput) throws RemoteException
   {
      try
      {
         getAdmin().retryActivity(aInput.getPid(), aInput.getLocationPath(), aInput.isAtScope());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * @see org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin#completeActivity(org.activebpel.rt.axis.bpel.admin.types.AesCompleteActivityType)
    */
   public void completeActivity(AesCompleteActivityType aInput) throws RemoteException
   {
      try
      {
         getAdmin().completeActivity(aInput.getPid(), aInput.getLocationPath());
      }
      catch (RemoteException e)
      {
         throw e;
      }
      catch(Throwable e)
      {
         throw new RemoteException(e.getLocalizedMessage(), e.getCause());
      }
   }

}