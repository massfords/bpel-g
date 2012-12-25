//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeValidationPolicy.java,v 1.3 2008/02/29 04:09:36 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeXPathUtil;
import org.w3c.dom.Element;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Helper class to handle validation policy interactions.
 */
public class AeValidationPolicy
{
   private static final Map sMap = Collections.singletonMap("abp", IAeConstants.ABP_NAMESPACE_URI); //$NON-NLS-1$
   private static final String XPATH_NO_OP_ATTR = "abp:Validation[not(@operation)]"; //$NON-NLS-1$
   
   // Attributes of the policy definition
   private static final String ATTR_VALIDATION_DIRECTION = "direction"; //$NON-NLS-1$
   private static final String VALIDATION_NONE = "none"; //$NON-NLS-1$
   private static final String VALIDATION_BOTH = "both"; //$NON-NLS-1$
   private static final String VALIDATION_IN = "in"; //$NON-NLS-1$
   private static final String VALIDATION_OUT = "out"; //$NON-NLS-1$

   /**
    * Private constructor to enforce singleton
    */
   private AeValidationPolicy()
   {
   }
   
   /**
    * Determines if validation is enabled given a list of endpoint policies to check
    * @param aPolicies the list of policies
    * @param aIsOutput True if this is an outbound request and False if inbound
    * @param aDefault the default configured by engine
    */
   public static boolean isValidateEnabled(Collection aPolicies, String aOperation, boolean aIsOutput, boolean aDefault)
   {
      if (aPolicies != null)
      {
         String exactMatch = MessageFormat.format("abp:Validation[@operation=''{0}'']", aOperation);  //$NON-NLS-1$

          for (Object aPolicy : aPolicies) {
              Element policyElement = (Element) aPolicy;

              // look for exact match first
              Element e = (Element) AeXPathUtil.selectSingleNodeIgnoreException(policyElement, exactMatch, sMap);
              if (e == null)
                  e = (Element) AeXPathUtil.selectSingleNodeIgnoreException(policyElement, XPATH_NO_OP_ATTR, sMap);

              if (e != null) {
                  String pattern = e.getAttribute(ATTR_VALIDATION_DIRECTION);
                  if (VALIDATION_BOTH.equals(pattern))
                      return true;
                  else if (VALIDATION_NONE.equals(pattern))
                      return false;
                  else if (VALIDATION_OUT.equals(pattern))
                      return (aIsOutput ? true : false);
                  else if (VALIDATION_IN.equals(pattern))
                      return (aIsOutput ? false : true);
              }
          }
      }
      return aDefault;
   }
}