package org.activebpel.rt.bpel.def.faults;

import org.activebpel.rt.bpel.IAeFaultTypeInfo;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;

/**
 * interface for the fault matching rules
 */
public interface IAeFaultMatchingRule
{
   /**
    * Return a match if the given fault handler and the fault data matches.
    * @param aProvider
    * @param aCatch
    * @param aFault
    * @return IAeMatch if it is a match, otherwise return null
    */
   public IAeMatch getMatch(IAeContextWSDLProvider aProvider, IAeCatch aCatch, IAeFaultTypeInfo aFault);

   /**
    * Set the priority of this match
    * @param aPriority
    */
   public void setPriority(int aPriority);

}