// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/IAeWSResourceValidator.java,v 1.2 2007/12/27 15:24:35 dvilaverde Exp $
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
 * Interface that must be implemented by web service resource validators.
 */
public interface IAeWSResourceValidator
{
   /**
    * Called to validate a web service resource.
    * 
    * @param aResourceModel
    * @param aContext
    * @param aErrorReporter
    */
   public void validate(Object aResourceModel, IAeWSResourceValidationContext aContext,
         IAeWSResourceProblemReporter aErrorReporter);
}
