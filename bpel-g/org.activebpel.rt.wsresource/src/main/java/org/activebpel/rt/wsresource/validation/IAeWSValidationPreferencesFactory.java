// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/IAeWSValidationPreferencesFactory.java,v 1.1 2008/02/21 21:17:43 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation;

/**
 * Interface for factories that will create <code>IAeWSResourceValidationPreferences</code>.
 */
public interface IAeWSValidationPreferencesFactory
{
   /**
    * Create a IAeWSResourceValidationPreferences object
    */
   public IAeWSResourceValidationPreferences createPreferences();
   
}
