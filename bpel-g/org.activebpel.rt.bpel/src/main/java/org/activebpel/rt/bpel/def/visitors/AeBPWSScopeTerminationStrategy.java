package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.IAeBPWSFaultFactory;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.activity.IAeScopeTerminationStrategy;

/**
 * Strategy used to terminate an executing scope for BPEL4WS 1.1. If the scope is already executing
 * a fault handler, then the fault handler is allowed to continue. Otherwise, raise the bpws:forcedTermination
 * fault and the fault will be processed and not rethrown when child termination is complete.
 */
public class AeBPWSScopeTerminationStrategy implements IAeScopeTerminationStrategy
{
   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeScopeTerminationStrategy#onHandleTermination(org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl)
    */
   public void onHandleTermination(AeActivityScopeImpl aScope)
   {
      throw new UnsupportedOperationException(AeMessages.format(AeMessages.getString("AeDefToImplVisitor.InvalidScopeTermination"), aScope.getLocationPath())); //$NON-NLS-1$
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
         IAeBPWSFaultFactory factory = (IAeBPWSFaultFactory) aImpl.getFaultFactory();
         IAeFault fault = factory.getForcedTermination();
         aImpl.triggerFaultHandling(fault);
      }
   }
}