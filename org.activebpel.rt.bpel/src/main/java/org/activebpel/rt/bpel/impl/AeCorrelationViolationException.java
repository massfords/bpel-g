// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeCorrelationViolationException.java,v 1.6 2006/08/15 18:32:57 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeFault;

/**
 * models a bpws:correlationViolation
 */
public class AeCorrelationViolationException extends AeBpelException
{
   /** an activity attempted to access a correlation set before it was initialized */
   public static final int UNINITIALIZED_CORRELATION_SET = 0;
   /** the properties for a correlation were going to be changed */
   public static final int IMMUTABLE = 1;
   /** a property contained a null value after being updated */
   public static final int NULL_VALUE = 2;
   /** a subprocess invoke where create instance is false */
   public static final int NOT_CREATEINSTANCE = 3;
   /** signals an attempt to initialize a correlationSet after its been initialized*/
   public static final int ALREADY_INITIALIZED = 4;
   
   private static final String[] sMessages = 
   {
      AeMessages.getString("AeCorrelationViolationException.0"), //$NON-NLS-1$
      AeMessages.getString("AeCorrelationViolationException.1"), //$NON-NLS-1$
      AeMessages.getString("AeCorrelationViolationException.2"), //$NON-NLS-1$
      AeMessages.getString("AeCorrelationViolationException.3"),  //$NON-NLS-1$
      AeMessages.getString("AeCorrelationViolationException.4")  //$NON-NLS-1$
   };
   
   /**
    * Ctor accepts namespace and error code
    * @param aNamespace
    * @param aCode
    */
   public AeCorrelationViolationException(String aNamespace, int aCode)
   {
      super(sMessages[aCode], AeFaultFactory.getFactory(aNamespace).getCorrelationViolation());
   }
   
   /**
    * Ctor for subclasses
    * @param aMessage
    * @param aFault
    */
   protected AeCorrelationViolationException(String aMessage, IAeFault aFault)
   {
      super(aMessage, aFault);
   }
}
