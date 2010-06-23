//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/support/AeForEachBranchesDef.java,v 1.2 2006/06/26 16:50:32 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity.support; 

import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Branches def resides under the optional completionCondition for the
 * serial and parallel forEach. It contains an expression that determines the 
 * number of iterations of the loop required in order for the loop to complete
 * without an error.
 */
public class AeForEachBranchesDef extends AeExpressionBaseDef
{
   /** true limits the completion count to scopes that completed normally */
   private boolean mCountCompletedBranchesOnly = false;
   
   /**
    * Default c'tor.
    */
   public AeForEachBranchesDef()
   {
      super();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
   
   /**
    * @return Returns the countCompletedBranchesOnly.
    */
   public boolean isCountCompletedBranchesOnly()
   {
      return mCountCompletedBranchesOnly;
   }
   
   /**
    * @param aCountCompletedBranchesOnly The countCompletedBranchesOnly to set.
    */
   public void setCountCompletedBranchesOnly(boolean aCountCompletedBranchesOnly)
   {
      mCountCompletedBranchesOnly = aCountCompletedBranchesOnly;
   }
}
 