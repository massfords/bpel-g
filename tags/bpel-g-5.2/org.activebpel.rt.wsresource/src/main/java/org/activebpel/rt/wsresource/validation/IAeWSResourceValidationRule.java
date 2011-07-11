// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/IAeWSResourceValidationRule.java,v 1.5 2008/01/28 18:35:21 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation;

import javax.xml.namespace.QName;

/**
 * Represents a single valdiation rule.
 */
public interface IAeWSResourceValidationRule
{
   /**
    * Returns the web service resource validation rule's ID.  The
    * rule ID is a QName composed of the WS Resource type and an
    * error code.
    */
   public QName getId();

   /**
    * Returns the rules default severity.  This default will be used
    * if there is no configured severity for this rule in the validation
    * preferences.
    */
   public int getDefaultSeverity();

   /**
    * Returns the rule description.
    */
   public String getDescription();

   /**
    * Gets the validator that will do the actual validation of
    * the web service resource.
    * 
    * @param aContext
    */
   public IAeWSResourceValidator createValidator(IAeWSResourceValidationContext aContext);
}
