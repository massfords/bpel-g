// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/AeInvokeRecoveryEvaluator.java,v 1.4 2008/02/19 14:20:09 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery; 

import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.IAePolicyConstants;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.activity.AeActivityInvokeImpl;
import org.activebpel.rt.bpel.server.deploy.AeDeployConstants;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Determines whether a given invoke activity requires suspending the process.
 * Call {@link #setInvoke(AeActivityInvokeImpl)} to specify an invoke activity
 * and then {@link #isSuspendProcessRequired()} to determine whether the given
 * invoke activity requires suspending the process.
 */
public class AeInvokeRecoveryEvaluator
{
   /** Return values for {@link #evaluatePolicyAssertion(Element)}. */
   private static final int CONTINUE_PROCESS = 0;
   private static final int SUSPEND_PROCESS  = 1;
   private static final int IGNORE_ASSERTION = 2;

   /** The invoke activity. */
   private AeActivityInvokeImpl mInvoke;
   
   /**
    * Constructs the invoke recovery evaluator.
    */
   public AeInvokeRecoveryEvaluator()
   {
   }

   /**
    * Evaluates the given policy assertion element, and returns one of
    * {@link #CONTINUE_PROCESS}, {@link #SUSPEND_PROCESS}, or
    * {@link #IGNORE_ASSERTION}.
    *
    * @param aPolicyAssertion
    */
   protected int evaluatePolicyAssertion(Element aPolicyAssertion)
   {
      // TODO (KR) Implement an attribute that specifies a service to query.
      String suspendProcess = aPolicyAssertion.getAttribute(IAePolicyConstants.ATTR_SUSPEND_PROCESS);
      int action;
      if (suspendProcess.equals(AeDeployConstants.SUSPEND_TYPE_DEFAULT))
      {
         // If the attribute has value "default", then delegate to the engine
         // configuration setting.
         boolean suspend = AeEngineFactory.getEngineConfig().isSuspendProcessOnInvokeRecovery();
         action = suspend ? SUSPEND_PROCESS : CONTINUE_PROCESS;
      }
      else if (suspendProcess.equals(AeDeployConstants.SUSPEND_TYPE_TRUE))
      {
         action = SUSPEND_PROCESS;
      }
      else if (suspendProcess.equals(AeDeployConstants.SUSPEND_TYPE_FALSE))
      {
         action = CONTINUE_PROCESS;
      }
      else
      {
         // Ignore the unrecognizable value.
         action = IGNORE_ASSERTION;
      }

      return action;
   }
   
   /**
    * Returns the invoke activity definition object.
    */
   protected AeActivityInvokeDef getDef()
   {
      return (AeActivityInvokeDef) getInvoke().getDefinition();
   }

   /**
    * Returns the invoke activity.
    */
   protected AeActivityInvokeImpl getInvoke()
   {
      return mInvoke;
   }

   /**
    * Returns <code>true</code> if and only if the invoke activity set by
    * {@link #setInvoke(AeActivityInvokeImpl)} is a pending invoke that
    * requires suspending the process.
    */
   public boolean isSuspendProcessRequired() throws AeBusinessProcessException
   {
      if (!getInvoke().isPending())
      {
         // If the invoke is not pending, then do not suspend the process for
         // this invoke.
         return false;
      }
   
      AePartnerLink partnerLink = getInvoke().findPartnerLink(getDef().getPartnerLink());
      IAeEndpointReference epr = partnerLink.getPartnerReference();
      
      List resolvedPolicies = null;
      if (epr != null)
      {
         resolvedPolicies = epr.getEffectivePolicies(getInvoke().getProcess().getProcessPlan(), getDef().getPortType(), getDef().getOperation());
      }
      
      if (!AeUtil.isNullOrEmpty(resolvedPolicies))
      {
         
         // Iterate through all the policy assertions associated with the
         // partner role endpoint reference for the invoke's partner link.
         for (Iterator i = epr.getPolicies().iterator(); i.hasNext(); )
         {
            Element policy = (Element) i.next();
   
            // We won't suspend for ws-rm
            NodeList assertions = policy.getElementsByTagNameNS(IAeConstants.WSRM_POLICY_NAMESPACE_URI, IAePolicyConstants.TAG_ASSERT_RM);
            if (assertions != null && assertions.getLength() > 0)
            {
               return false;
            }
            
            assertions = policy.getElementsByTagNameNS(IAeConstants.ABP_NAMESPACE_URI, IAePolicyConstants.TAG_INVOKE_RECOVERY);
            if (assertions != null)
            {
               for (int j = 0; j < assertions.getLength(); ++j)
               {
                  Element assertion = (Element) assertions.item(j);
                  int action = evaluatePolicyAssertion(assertion);

                  switch (action)
                  {
                     case SUSPEND_PROCESS:
                        return true;

                     case CONTINUE_PROCESS:
                        return false;

                     default:
                        // This policy assertion was not definitive; perhaps it
                        // was invalid or ambiguous. Skip it.
                        continue;
                  }
               }
            }
         }
      }
   
      // If there are no applicable policy assertions, then use the setting
      // specified by the process deployment descriptor (by default this
      // delegates to the engine configuration setting).
      return getInvoke().getProcess().getProcessPlan().isSuspendProcessOnInvokeRecoveryEnabled();
   }

   /**
    * Sets the invoke activity.
    *
    * @param aInvoke
    */
   public void setInvoke(AeActivityInvokeImpl aInvoke)
   {
      mInvoke = aInvoke;
   }
}
 