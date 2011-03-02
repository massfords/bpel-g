//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeBaseFaultFactory.java,v 1.3 2006/11/09 21:33:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl; 

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.impl.activity.support.AeFault;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Base class for the fault factories. Provides the fault names for the AE 
 * faults that cover fault conditions not described in the BPEL spec.
 */
public abstract class AeBaseFaultFactory implements IAeFaultFactory
{
   /** 
    * Identifier for interal ae:coordinatedCompensationTerminated: used to 
    * signal that a coordinated compensation was terminated 
    */
   protected static final String COORD_COMP_TERMINATED = "coordinatedCompensationTerminated"; //$NON-NLS-1$
   
   /** 
    * Identifier for internal ae:retryFault: used to trigger a scope's 
    * compensation handlers if it's being retried 
    */
   protected static final String RETRY_FAULT = "retryFault"; //$NON-NLS-1$

   /** 
    * Identifier for internal ae:earlyTermination: used to implement breaking 
    * out of a loop via a break or continue 
    */
   protected static final String EARLY_TERMINATION_FAULT = "earlyTerminationFault"; //$NON-NLS-1$

   /** 
    * Identifier for internal ae:processTerminated fault, thrown when a process 
    * is terminated by means of admin API or exit activity 
    */
   protected static final String PROCESS_TERMINATED = "processTerminated"; //$NON-NLS-1$

   /** 
    * Identifier for internal ae:processCancelled fault, thrown when a sub 
    * process is cancelled by the coordination manager 
    */
   protected static final String PROCESS_CANCELLED = "processCancelled"; //$NON-NLS-1$

   /** Identifier for ae:static analysis fault. */
   protected static final String STATIC_ANALYSIS_FAILURE = "staticAnalysisFailure"; //$NON-NLS-1$

   /** Identifier for a missingReply that needs to be propagated to an external service */
   protected static final String EXTERNAL_MISSING_REPLY = "missingReply"; //$NON-NLS-1$

   /** Identifier for a ambiguousReceive that needs to be propagated to an external service */
   protected static final String EXTERNAL_AMBIGUOUS_RECEIVE = "ambiguousReceive"; //$NON-NLS-1$

   /** namespace for the current version of BPEL */
   private String mNamespace;
   
   /**
    * Ctor accepts the namespace
    * @param aNamespace
    */
   public AeBaseFaultFactory(String aNamespace)
   {
      setNamespace(aNamespace);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeFaultFactory#getExternalMissingReply()
    */
   public IAeFault getExternalMissingReply()
   {
      return AeFaultFactory.makeAeNamespaceFault(EXTERNAL_MISSING_REPLY);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeFaultFactory#getExternalAmbiguousReceive()
    */
   public IAeFault getExternalAmbiguousReceive()
   {
      return AeFaultFactory.makeAeNamespaceFault(EXTERNAL_AMBIGUOUS_RECEIVE);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeFaultFactory#getCoordinatedCompensationTerminated()
    */
   public IAeFault getCoordinatedCompensationTerminated()
   {
      return AeFaultFactory.makeAeNamespaceFault(COORD_COMP_TERMINATED);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeFaultFactory#getRetryFault()
    */
   public IAeFault getRetryFault()
   {
      AeFault fault = (AeFault) AeFaultFactory.makeAeNamespaceFault(RETRY_FAULT);
      fault.setRethrowable(false);
      return fault;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeFaultFactory#isRetryFault(org.activebpel.rt.bpel.IAeFault)
    */
   public boolean isRetryFault(IAeFault aFault)
   {
      return IAeBPELConstants.AE_NAMESPACE_URI.equals(aFault.getFaultName().getNamespaceURI()) 
            && aFault.getFaultName().getLocalPart().equals(RETRY_FAULT);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeFaultFactory#getEarlyTerminationFault()
    */
   public IAeFault getEarlyTerminationFault()
   {
      AeFault fault = (AeFault) AeFaultFactory.makeAeNamespaceFault(EARLY_TERMINATION_FAULT);
      fault.setRethrowable(false);
      return fault;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeFaultFactory#isEarlyTerminationFault(org.activebpel.rt.bpel.IAeFault)
    */
   public boolean isEarlyTerminationFault(IAeFault aFault)
   {
      return IAeBPELConstants.AE_NAMESPACE_URI.equals(aFault.getFaultName().getNamespaceURI()) 
      && aFault.getFaultName().getLocalPart().equals(EARLY_TERMINATION_FAULT);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeFaultFactory#getProcessTerminated()
    */
   public IAeFault getProcessTerminated()
   {
      return AeFaultFactory.makeAeNamespaceFault(PROCESS_TERMINATED);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeFaultFactory#getProcessCancelled()
    */
   public IAeFault getProcessCancelled()
   {
      return AeFaultFactory.makeAeNamespaceFault(PROCESS_CANCELLED);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeFaultFactory#getStaticAnalysisFailure(java.lang.String)
    */
   public IAeFault getStaticAnalysisFailure(String aMessage)
   {
      AeFault fault = (AeFault) AeFaultFactory.makeAeNamespaceFault(STATIC_ANALYSIS_FAILURE);
      fault.setInfo(aMessage);
      return fault;
   }

   /**
    * Makes a fault in the BPEL namespace
    * @param aName
    */
   protected IAeFault makeBpelFault(String aName)
   {
      return new AeFault(new QName(getNamespace(), aName), (IAeMessageData) null); 
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeFaultFactory#getNamespace()
    */
   public String getNamespace()
   {
      return mNamespace;
   }
   
   /**
    * Setter for the namespace
    * @param aNamespace
    */
   protected void setNamespace(String aNamespace)
   {
      mNamespace = aNamespace;
   }
}
 