// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/IAeValidationAdapter.java,v 1.2 2008/02/27 20:48:28 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation;

import java.util.Set;

import org.activebpel.rt.xml.def.IAeAdapter;

/**
 * Adapter interface for extension objects that validate Human interaction contents 
 * using the rules based validation.
 */
public interface IAeValidationAdapter extends IAeAdapter
{
   /**
    * Perform the validation.
    * 
    * @param aValidationContext
    */
   public void validate(IAeValidationContext aValidationContext);

   /**
    * Gets the set of variables referenced.  Returns a <code>Set</code>
    * of <code>AeVariableReference</code> objects.
    * 
    * @param aAeValidationContext 
    */
   public Set getVariableUsage(IAeValidationContext aAeValidationContext);
}
