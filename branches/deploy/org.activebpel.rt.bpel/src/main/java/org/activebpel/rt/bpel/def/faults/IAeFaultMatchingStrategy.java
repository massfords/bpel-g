package org.activebpel.rt.bpel.def.faults;

import java.util.Iterator;

import org.activebpel.rt.bpel.IAeFaultTypeInfo;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;

/**
 * Determines which fault handler is capable of catching a fault. The fault
 * matching rules differ between bpws and wsbpel.
 */
public interface IAeFaultMatchingStrategy
{
   /**
    * Select ths catch that is the best match for the given fault.
    * @param aProvider
    * @param aIterOfCatches
    * @param aFault
    * @return IAeCatch or null. In the case of null, the catchAll or implicit fault handler will handle the fault.
    */
   public IAeCatch selectMatchingCatch(IAeContextWSDLProvider aProvider, Iterator aIterOfCatches, IAeFaultTypeInfo aFault);
}