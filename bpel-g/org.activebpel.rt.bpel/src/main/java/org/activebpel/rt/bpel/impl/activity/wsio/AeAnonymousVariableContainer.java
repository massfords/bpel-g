// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/AeAnonymousVariableContainer.java,v 1.2 2006/09/22 19:52:39 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.wsio;

import java.util.Collections;
import java.util.Iterator;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.activity.IAeVariableContainer;

/**
 * Implements a variable container that holds a single anonymous variable.
 */
public class AeAnonymousVariableContainer implements IAeVariableContainer
{
   /** The anonymous variable. */
   IAeVariable mVariable;

   /**
    * Returns the anonymous variable.
    */
   protected IAeVariable getVariable()
   {
      return mVariable;
   }

   /*===========================================================================
    * IAeVariableContainer methods
    *===========================================================================
    */

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeVariableContainer#findVariable(java.lang.String)
    */
   public IAeVariable findVariable(String aVariableName)
   {
      return getVariable();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeVariableContainer#addVariable(org.activebpel.rt.bpel.IAeVariable)
    */
   public void addVariable(IAeVariable aVariable)
   {
      if (getVariable() != null)
      {
         throw new IllegalStateException(AeMessages.getString("AeAnonymousMessageDataVariable.ERROR_AddVariable")); //$NON-NLS-1$
      }

      mVariable = aVariable;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeVariableContainer#iterator()
    */
   public Iterator iterator()
   {
      return Collections.singleton(getVariable()).iterator();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeVariableContainer#initialize()
    */
   public void initialize() throws AeBpelException
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeVariableContainer#getParent()
    */
   public IAeBpelObject getParent()
   {
      throw new UnsupportedOperationException();
   }
}