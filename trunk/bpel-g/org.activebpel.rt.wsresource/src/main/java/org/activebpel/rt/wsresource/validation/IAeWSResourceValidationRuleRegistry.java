// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/IAeWSResourceValidationRuleRegistry.java,v 1.1 2007/12/17 16:41:42 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation;

import java.util.List;

/**
 *
 */
public interface IAeWSResourceValidationRuleRegistry
{
   /**
    * Returns a list of IAeWSResourceValidationRule instances for the
    * given web service resource type (wsdl, xsd, etc...).
    * 
    * @param aWSResourceType
    */
   public List getRules(String aWSResourceType);
}
