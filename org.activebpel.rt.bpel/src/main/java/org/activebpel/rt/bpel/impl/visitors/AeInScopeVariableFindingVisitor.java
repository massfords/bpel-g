// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/visitors/AeInScopeVariableFindingVisitor.java,v 1.3 2007/11/27 02:49:47 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.visitors;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.impl.AeBusinessProcess;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.activity.IAeVariableContainer;

/**
 * A visitor that finds all variables in scope.
 */
public class AeInScopeVariableFindingVisitor extends AeImplReverseTraversingVisitor
{
   /** A map of variable name -> IAeVariable. */
   private Map mInScopeVariables;
   /** A set of variables that should be excluded from the search. */
   private Set mExcludedVariables;

   /**
    * C'tor.
    */
   public AeInScopeVariableFindingVisitor()
   {
      setInScopeVariables(new HashMap());
      setExcludedVariables(new HashSet());
   }

   /**
    * C'tor.
    */
   public AeInScopeVariableFindingVisitor(Set aExcludedVariables)
   {
      this();
      if (aExcludedVariables != null)
      {
          setExcludedVariables(aExcludedVariables);
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeAbstractImplVisitor#visitScope(org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl)
    */
   protected void visitScope(AeActivityScopeImpl aImpl)
         throws AeBusinessProcessException
   {
      findInScopeVariables(aImpl);
      super.visitScope(aImpl);
   }

   /**
   * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.AeBusinessProcess)
   */
   public void visit(AeBusinessProcess aImpl) throws AeBusinessProcessException
   {
      findInScopeVariables(aImpl);
      
      super.visit(aImpl);
   }

   /**
    * @param aImpl
    */
   private void findInScopeVariables(AeActivityScopeImpl aImpl)
   {
      
      IAeVariableContainer variableContainer = aImpl.getVariableContainer();
      if (variableContainer != null)
      {
         for (Iterator iter = variableContainer.iterator(); iter.hasNext(); )
         {
            IAeVariable variable = (IAeVariable) iter.next();
            // If it's not already included, and it's not excluded, then add it to
            // the map of variables that we've found.
            if (!getInScopeVariables().containsKey(variable.getName())
                  && !getExcludedVariables().contains(variable.getName()))
            {
               getInScopeVariables().put(variable.getName(), variable);
            }
         }
      }
   }

   /**
    * Gets the collection of variables found.
    */
   public Collection getVariables()
   {
      return getInScopeVariables().values();
   }
   
   /**
    * @return Returns the inScopeVariables.
    */
   protected Map getInScopeVariables()
   {
      return mInScopeVariables;
   }

   /**
    * @param aInScopeVariables the inScopeVariables to set
    */
   protected void setInScopeVariables(Map aInScopeVariables)
   {
      mInScopeVariables = aInScopeVariables;
   }

   /**
    * @return Returns the excludedVariables.
    */
   protected Set getExcludedVariables()
   {
      return mExcludedVariables;
   }

   /**
    * @param aExcludedVariables the excludedVariables to set
    */
   protected void setExcludedVariables(Set aExcludedVariables)
   {
      mExcludedVariables = aExcludedVariables;
   }
}
