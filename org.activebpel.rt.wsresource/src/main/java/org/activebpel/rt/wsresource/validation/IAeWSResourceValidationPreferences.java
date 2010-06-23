// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/IAeWSResourceValidationPreferences.java,v 1.1 2007/12/17 16:41:42 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation;

/**
 * The preferences to use when validating web service resources.  This
 * interface allows the validation engine to decide what to validate, what
 * severity an error is, etc.
 */
public interface IAeWSResourceValidationPreferences
{
   /*
    * Constants defining the validation severity levels.
    */
   public final int SEVERITY_ERROR = 0;
   public final int SEVERITY_WARNING = 1;
   public final int SEVERITY_INFO = 2;
   public final int SEVERITY_SKIP = 3;

   /**
    * Returns true if schema-validation is enabled for the given
    * web service resource type.
    *
    * @param aWSResourceType
    */
   public boolean isSchemaValidationEnabled(String aWSResourceType);

   /**
    * Gets the severity of the given web service resource validation
    * rule.
    *
    * @param aValidationRule
    */
   public int getSeverity(IAeWSResourceValidationRule aValidationRule);
}
