// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeToQueryVariableContext.java,v 1.6 2007/10/03 12:39:50 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.activity.assign.to;

import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext;

/**
 * Implements a Jaxen variable context using the copy operation context for
 * variable resolution.
 */
public class AeToQueryVariableContext extends AeFromQueryVariableContext
{
   /**
    * Creates a variable context for the to-query to-spec impl.
    * 
    * @param aCopyOpContext
    */
   public AeToQueryVariableContext(IAeCopyOperationContext aCopyOpContext)
   {
      super(aCopyOpContext);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.to.AeFromQueryVariableContext#getVariable(java.lang.String, java.lang.String)
    */
   protected IAeVariable getVariable(String aVariableName, String aPartName)
   {
      return getCopyOperationContext().getVariableForUpdate(aVariableName, aPartName);
   }
}
