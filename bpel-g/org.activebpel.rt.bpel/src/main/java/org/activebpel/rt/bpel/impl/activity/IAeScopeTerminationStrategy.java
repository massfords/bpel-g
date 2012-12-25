package org.activebpel.rt.bpel.impl.activity;

import org.activebpel.rt.bpel.AeBusinessProcessException;

/**
 *  BPEL4WS 1.1 and WS-BPEL have different strategies for terminating a scope.
 *  
 *  The 1.1 version will raise a bpws:forcedTermination fault
 *  The 2.0 version will execute a terminationHandler
 */
public interface IAeScopeTerminationStrategy
{
   /**
    * Called from the scope's <code>terminate()</code> in order to start the termination process
    * @param aImpl
    * @throws AeBusinessProcessException 
    */
   public void onStartTermination(AeActivityScopeImpl aImpl) throws AeBusinessProcessException;
   
   /**
    * Called when all of the scope's children are terminated and it's time to process the termination behavior
    * @param aImpl
    * @throws AeBusinessProcessException 
    */
   public void onHandleTermination(AeActivityScopeImpl aImpl) throws AeBusinessProcessException;
}