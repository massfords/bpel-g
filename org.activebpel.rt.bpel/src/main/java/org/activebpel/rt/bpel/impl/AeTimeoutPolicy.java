//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeTimeoutPolicy.java,v 1.1 2007/10/12 17:33:09 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.w3c.dom.Element;

/**
 * Helper class to handle timeout policy interactions.
 */
public class AeTimeoutPolicy
{
   /** The Tag which identifies a Timeout assertion */
   public static final String TAG_ASSERT_TIMEOUT = "Timeout"; //$NON-NLS-1$
   public static final String TAG_TIMEOUT_LENGTH = "seconds"; //$NON-NLS-1$   

   /** The qualified name for the timeout policy */
   public static final QName TIMEOUT_ID = new QName(IAeConstants.ABP_NAMESPACE_URI, TAG_ASSERT_TIMEOUT);

   /**
    * Private constructor
    */
   private AeTimeoutPolicy()
   {
   }
   
   /**
    * Returns the timeout configured by the policy. 
    * @param aTimeoutPolicy the policy element
    */
   public static int getTimeoutValue(Element aTimeoutPolicy)
   {
      try
      {
         return Integer.parseInt(aTimeoutPolicy.getAttribute(TAG_TIMEOUT_LENGTH).trim());
      }
      catch (NumberFormatException nfe)
      {
         AeException.logError(nfe);
         return 0;
      }
   }
}