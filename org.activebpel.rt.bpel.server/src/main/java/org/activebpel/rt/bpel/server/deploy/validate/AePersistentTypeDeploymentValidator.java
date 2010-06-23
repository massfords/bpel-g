//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AePersistentTypeDeploymentValidator.java,v 1.4 2008/02/17 21:38:47 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.IAeActivityCreateInstanceDef;
import org.activebpel.rt.bpel.def.activity.AeActivityPickDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWaitDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.def.validation.IAeValidationDefs;
import org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefTraverser;
import org.activebpel.rt.bpel.def.visitors.AeTraversalVisitor;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.deploy.AeProcessPersistenceType;
import org.activebpel.rt.bpel.server.deploy.AeProcessTransactionType;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 * A deployment validator that checks to see if the deploypment
 * persistence and transaction type is non-default, then verifies
 * that only permitted BPEL constructs are included; as well as
 * to verify that subprocess invokes are not used. 
 */
public class AePersistentTypeDeploymentValidator extends AeAbstractDefVisitor implements IAeValidationDefs
{
   /** Subprocess invoke handler type . */
   public static final String INVOKE_HANDLER_PROCESS_SUB = "process:subprocess"; //$NON-NLS-1$
   /** Retry policy tag name. */
   public static final String RETRY_POLICY_TAG = "retry"; //$NON-NLS-1$   
   /** Validation replorter */
   private IAeBaseErrorReporter mReporter;
   /** Process deployment plan. */
   private IAeProcessDeployment mProcessDeployment;
   /** Number of receive activities found during validation. */
   private int mReceiveActivityRefCount;
   /** Indicates that a request-response style Receive activity (with CreateInstance) was found. */
   private boolean mCreateActivityFound;
   
   /**
    * Constructs the validator.
    * @param aErrorReporter
    * @param aProcessDeployment
    */
   public AePersistentTypeDeploymentValidator(IAeBaseErrorReporter aErrorReporter, IAeProcessDeployment aProcessDeployment)
   {
      setReporter( aErrorReporter );
      setProcessDeployment(aProcessDeployment);
      init();
   }
   
   /**
    * Initializes the traversing visitor.
    */
   protected void init()
   {
      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
   }

   /**
    * @return Returns the reporter.
    */
   protected IAeBaseErrorReporter getReporter()
   {
      return mReporter;
   }

   /**
    * @param aReporter The reporter to set.
    */
   protected void setReporter(IAeBaseErrorReporter aReporter)
   {
      mReporter = aReporter;
   }
   
   /**
    * @return Returns the processDeployment.
    */
   private IAeProcessDeployment getProcessDeployment()
   {
      return mProcessDeployment;
   }

   /**
    * @param aProcessDeployment The processDeployment to set.
    */
   private void setProcessDeployment(IAeProcessDeployment aProcessDeployment)
   {
      mProcessDeployment = aProcessDeployment;
   }

   /** 
    * @return true if the process is deployed with persistent type of FULL.
    */
   protected boolean isPersistentType()
   {
      return  AeProcessPersistenceType.FULL.equals(getPersistentType()); 
   }
   
   /** 
    * @return Returns the persistent type.
    */
   protected AeProcessPersistenceType getPersistentType()
   {
      return getProcessDeployment().getPersistenceType();
   }

   /**
    * @return true if the process deployment uses container managed transaction types.
    */
   protected boolean isContainerTransactionType()
   {
      return  AeProcessTransactionType.CONTAINER.equals( getTransactionType() ); 
   }
   
   /**
    * @return the transaction type.
    */
   protected AeProcessTransactionType getTransactionType()
   {
      return getProcessDeployment().getTransactionType();
   }
   
   /**
    * Gets the process definition.
    */
   protected AeProcessDef getProcessDef()
   {
      return getProcessDeployment().getProcessDef();
   }
      
   /**
    * Validates the process and partner links as per requirment 98.
    * Non-persistent processes must implement a request-response style operation with a 
    * createInstance activity. 
    * No other inbound message activities (&lt;receive&gt;, &lt;onMessage&gt;, &lt;onEvent&gt;) 
    * or alarms are permitted. The prohibition of alarms applies to BPEL &lt;onAlarm&gt;�s as well
    * as alarms used to track features like retry policies for invokes.
    */ 
   public void validate()
   {
      if (!AeEngineFactory.isPersistentStoreConfiguration() || isPersistentType())
      {
         // Short return if the process is either a persistent type deployment
         // or uses an in-memory store.
         // Non-persistent store uses the in-memory managers.
         // subprocess invoke is also supported via in-memory configuration.
         return;
      }
      setCreateActivityFound(false);
      resetReceiveActivityRefCount();      
      validateProcess();
   }
   
   /**
    * Validates the process and its constructs as per requirement 98 (for non-persistent processes).
    */
   protected void validateProcess()
   {
      getProcessDef().accept( this );   
      if ( !isCreateActivityFound() )
      {
         addError( ERROR_NONPERSISTENT_CREATE_INSTANCE_NOT_FOUND, null );
      }
      
      if (getReceiveActivityRefCount() > 1)
      {
         addError( ERROR_NONPERSISTENT_MULTIPLE_RECEIVES_NOT_ALLOWED, new String[] {String.valueOf( getReceiveActivityRefCount() )} );
      }
   }
   
   /**
    * @return Returns the receiveActivityRefCount.
    */
   protected int getReceiveActivityRefCount()
   {
      return mReceiveActivityRefCount;
   }

   /**
    * Resets the receive activity reference counter to zero.
    */
   protected void resetReceiveActivityRefCount()
   {
      mReceiveActivityRefCount = 0;
   }   
   /**
    * @return Returns the twoWayCreateActivityFound.
    */
   protected boolean isCreateActivityFound()
   {
      return mCreateActivityFound;
   }

   /**
    * @param aTwoWayCreateActivityFound The twoWayCreateActivityFound to set.
    */
   protected void setCreateActivityFound(boolean aTwoWayCreateActivityFound)
   {
      mCreateActivityFound = aTwoWayCreateActivityFound;
   }

   
   /**
    * Returns true if the EPR has one or more retry policies.
    * @param aEpr
    */
   protected boolean hasRetryPolicy(IAeEndpointReference aEpr)
   {
      if (aEpr == null)
         return false;
      
      return (aEpr.findPolicyElements(getProcessDeployment(), RETRY_POLICY_TAG).size() > 0);
   }
   
   /**
    * Returns the invoke handler type string for the given partner link.
    * @param aDef
    *
    */
   protected String getInvokeHandlerType(AePartnerLinkDef aDef)
   {
      return getProcessDeployment().getInvokeHandler(aDef.getName());
   }
   
   /**
    * Returns the end point reference for the given partnerlink.
    * @param aDef
    *
    */
   protected IAeEndpointReference getEndpointReference(AePartnerLinkDef aDef)
   {
      return getProcessDeployment().getPartnerEndpointRef(aDef.getName());
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinkDef)
    */
   public void visit(AePartnerLinkDef aDef)
   {
      String invokeHandlerType = getInvokeHandlerType(aDef);  
      if (INVOKE_HANDLER_PROCESS_SUB.equalsIgnoreCase(invokeHandlerType))
      {
         addWarning( WARNING_NONPERSISTENT_SUBPROCESS_NOT_ALLOWED, new String[] {aDef.getName() } );
      }
      IAeEndpointReference epr = getEndpointReference(aDef);
      if (epr != null && hasRetryPolicy(epr))
      {
         addError( ERROR_NONPERSISTENT_RETRYPOLICY_NOT_ALLOWED, new String[] {aDef.getName() } );
      }      
   }
   
   /**
    * Checks to see if the definition is a create instance activity def.
    * @param aDef
    * @return true if the activity is of create instance.
    */
   protected boolean checkCreateInstance(AeBaseDef aDef)
   {
      boolean rVal = false;
      if (aDef instanceof IAeActivityCreateInstanceDef)
      {  
         // OnMessage, Receive and Pick.
         if ( ((IAeActivityCreateInstanceDef)aDef).isCreateInstance() )
         {
            setCreateActivityFound(true);
            rVal = true;
         }
      }
      return rVal;
   }
   
   /**  
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef aDef)
   {
      mReceiveActivityRefCount++;
      if (!checkCreateInstance(aDef) )
      {
         addError( ERROR_NONPERSISTENT_ACTIVITY_NOT_ALLOWED, new String[] {AeActivityReceiveDef.TAG_RECEIVE} );
      }
      super.visit(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityPickDef)
    */
   public void visit(AeActivityPickDef aDef)
   {
      // fixme (MF) add tests for this class
      mReceiveActivityRefCount++;
      if (!checkCreateInstance(aDef) )
      {
         addError( ERROR_NONPERSISTENT_ACTIVITY_NOT_ALLOWED, new String[] {AeActivityPickDef.TAG_RECEIVE} );
      }
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef aDef)
   {
      // parent could be a 1.1 scope
      if (!(aDef.getParent() instanceof AeActivityPickDef))
      {
         mReceiveActivityRefCount++;
      }
      if (!checkCreateInstance(aDef) )
      {
         addError( ERROR_NONPERSISTENT_ACTIVITY_NOT_ALLOWED, new String[] {AeOnMessageDef.TAG_ON_MESSAGE} );
      }
      super.visit(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      addError( ERROR_NONPERSISTENT_ACTIVITY_NOT_ALLOWED, new String[] {AeOnEventDef.TAG_ON_MESSAGE} );
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
    */
   public void visit(AeActivityWaitDef aDef)
   {
      addError( ERROR_NONPERSISTENT_ACTIVITY_NOT_ALLOWED, new String[] {AeActivityWaitDef.TAG_WAIT} );
      super.visit(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void visit(AeOnAlarmDef aDef)
   {
      addError( ERROR_NONPERSISTENT_ACTIVITY_NOT_ALLOWED, new String[] {AeOnAlarmDef.TAG_ON_ALARM} );
      super.visit(aDef);
   }
   
   /**
    * Adds the error message to the error reporter
    * @param aErrorCode
    * @param aArgs
    */
   protected void addError(String aErrorCode, Object[] aArgs)
   {
      getReporter().addError( aErrorCode , aArgs, null);
   }

   /**
    * Adds the warning message to the error reporter 
    * @param aWarnCode
    * @param aArgs
    */
   protected void addWarning(String aWarnCode, Object[] aArgs)
   {
      getReporter().addWarning( aWarnCode, aArgs, null );
   }   
}

