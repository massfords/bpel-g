package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.activity.IAeScopeTerminationStrategy;

/**
 * Strategy used to terminate an executing scope for WS-BPEL 2.0. If the scope is already executing a fault handler
 * then the termination handler is unavailable and the fault handler is allowed to continue. Otherwise, queue
 * the termination handler to execute.
 */
public class AeWSBPELScopeTerminationStrategy implements IAeScopeTerminationStrategy
{
   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeScopeTerminationStrategy#onHandleTermination(org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl)
    */
   public void onHandleTermination(AeActivityScopeImpl aImpl) throws AeBusinessProcessException
   {
      aImpl.getProcess().queueObjectToExecute(aImpl.getTerminationHandler());
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeScopeTerminationStrategy#onStartTermination(org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl)
    */
   public void onStartTermination(AeActivityScopeImpl aImpl) throws AeBusinessProcessException
   {
      if (aImpl.isExecutingFaultHandler())
      {
         return;
      }
      else
      {
         aImpl.startTermination();
      }
   }
}