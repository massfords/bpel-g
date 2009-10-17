// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityValidateImpl.java,v 1.5 2008/02/21 17:03:27 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.AeActivityValidateDef;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeInvalidVariableException;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.util.AeUtil;

/**
 * Implementation of the BPEL validate activity.
 */
public class AeActivityValidateImpl extends AeActivityImpl
{
   /**
    * Ctor accepts the def and parent
    * 
    * @param aValidate
    * @param aParent
    */
   public AeActivityValidateImpl(AeActivityValidateDef aValidate, IAeActivityParent aParent)
   {
      super(aValidate, aParent);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept(IAeImplVisitor aVisitor) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();
      try
      {
         List exceptions = null;
         // Loop through all variables and validate each one
         for (Iterator iter = ((AeActivityValidateDef) getDefinition()).getVariables(); iter.hasNext();)
         {
            try
            {
               IAeVariable var = findVariable(iter.next().toString());
               var.validate();
            }
            catch (AeInvalidVariableException invalidEx)
            {
               if (exceptions == null)
                  exceptions = new LinkedList();
               exceptions.add(invalidEx.getMessage());
            }
         }
         
         if (AeUtil.notNullOrEmpty(exceptions))
         {
            StringBuffer messages = new StringBuffer();
            String delim = ""; //$NON-NLS-1$
            for (Iterator it = exceptions.iterator(); it.hasNext();)
            {
               String message = (String) it.next();
               messages.append(delim);
               messages.append(message);
               delim = "\n"; //$NON-NLS-1$
            }
            throw new AeInvalidVariableException(getBPELNamespace(), messages.toString(), null);
         }

         objectCompleted();
      }
      catch(AeBpelException e)
      {
         objectCompletedWithFault(getFaultFactory().getInvalidVariables(e.getLocalizedMessage()));
      }
   }
} 