// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeFaultFactory.java,v 1.20 2008/02/27 17:54:22 rnaylor Exp $
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
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;

/**
 * Factory for selecting the IAeFaultFactory by namespace
 */
public class AeFaultFactory
{
   /** provides faults for WSBPEL */
   private static final IAeFaultFactory WSBPEL = new AeWSBPELFaultFactory(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI);
   /** provides faults for BPEL4WS */
   private static final IAeFaultFactory BPEL4WS = new AeBPWSFaultFactory(IAeBPELConstants.BPWS_NAMESPACE_URI);

   /** Identifier for internal ae:systemError fault: used for unrecoverable system errors */
   protected static final String SYSTEM_ERROR = "systemError"; //$NON-NLS-1$
   /** Identifier for internal ae:bad process fault: used when we encounter invalid BPEL (static analysis should catch all of these issues */
   protected static final String BAD_PROCESS = "badProcess"; //$NON-NLS-1$
   /** Identifier for internal ae:timeout fault: used for timeout conditions */
   protected static final String TIMEOUT = "timeout"; //$NON-NLS-1$

   /**
    * no ctor access
    */
   private AeFaultFactory()
   {
   }

   /**
    * Gets the fault factory given a namespace value
    * @param aNamespace
    */
   public static IAeFaultFactory getFactory(String aNamespace)
   {
      if (IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI.equals(aNamespace))
         return WSBPEL;
      else
         return BPEL4WS;
   }

   /**
    * Gets system fault
    * @param aThrowable
    * @param aMessage
    */
   public static IAeFault getSystemErrorFault(Throwable aThrowable, String aMessage)
   {
      IAeFault fault = makeAeNamespaceFault(SYSTEM_ERROR);
      fault.setInfo(aMessage);
      if (aThrowable != null)
         fault.setDetailedInfo(AeUtil.getStacktrace(aThrowable));
      return fault;
   }

   /**
    * Gets a fault for a timeout condition.
    * @param aMessage optional message detail to set
    */
   public static IAeFault getTimeoutFault(String aMessage)
   {
      IAeFault fault = makeAeNamespaceFault(TIMEOUT);
      fault.setInfo(aMessage);
      return fault;
   }
   
   /**
    * Returns flag indicating if the fault code is a Ae timeout fault 
    * @param aFaultCode
    */
   public static boolean isTimeoutFault(QName aFaultCode)
   {
      if (aFaultCode == null)
         return false;
      
      return (IAeBPELConstants.AE_NAMESPACE_URI.equals(aFaultCode.getNamespaceURI()) && TIMEOUT.equals(aFaultCode.getLocalPart())); 
   }

   /**
    * Gets system fault
    * @param aThrowable
    */
   public static IAeFault getSystemErrorFault(Throwable aThrowable)
   {
      return getSystemErrorFault(aThrowable, aThrowable.getLocalizedMessage());
   }

   /**
    * The system errors are the same for the different versions so this is
    * provided as a convenience
    */
   public static IAeFault getSystemErrorFault()
   {
      return getSystemErrorFault(null, null);
   }

   /**
    * Makes a fault in the system namespace
    * @param aName
    */
   protected static IAeFault makeAeNamespaceFault(String aName)
   {
      return new AeFault(new QName(IAeBPELConstants.AE_NAMESPACE_URI, aName), (IAeMessageData) null);
   }

   /**
    * Makes a system fault with the specified faultname and detailed error info
    * @param aFaultName
    * @param aThrowable
    * @param aMessage
    */
   protected static IAeFault makeAeNamespaceFault(String aFaultName, Throwable aThrowable, String aMessage)
   {
      IAeFault fault = makeAeNamespaceFault(aFaultName);
      fault.setInfo(aMessage);
      fault.setDetailedInfo(AeUtil.getStacktrace(aThrowable));
      return fault;
   }

   /**
    * Gets the 'bad process' fault instance.
    */
   public static IAeFault getBadProcess()
   {
      return makeAeNamespaceFault(BAD_PROCESS);
   }

   /**
    * Returns a copy of the given fault that is rethrowable, suspendable and the fault source
    * reset to <code>null</code>.
    * @param aFault
    * @return copy of fault that is suspendable and rethrowable.
    */
   public static IAeFault makeUncaughtFault(IAeFault aFault)
   {
      AeFault copy = null;
      if (aFault.hasElementData())
      {
         copy = new AeFault(aFault.getFaultName(), AeXmlUtil.cloneElement( aFault.getElementData() ));
      }
      else if (aFault.hasMessageData() )
      {
         copy = new AeFault(aFault.getFaultName(), (IAeMessageData)aFault.getMessageData().clone());
      }
      else
      {
         copy = new AeFault(aFault.getFaultName(), (IAeMessageData) null);
      }
      copy.setDetailedInfo( aFault.getDetailedInfo() );
      copy.setInfo( aFault.getInfo() );
      copy.setSource( null );
      copy.setSuspendable( true);
      copy.setRethrowable( true );
      return copy;
   }
}
