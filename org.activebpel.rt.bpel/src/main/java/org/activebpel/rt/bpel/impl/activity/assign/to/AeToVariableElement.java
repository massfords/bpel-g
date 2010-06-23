// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeToVariableElement.java,v 1.8 2008/02/11 16:59:34 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.to; 

import org.activebpel.rt.attachment.IAeAttachmentContainer;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.impl.AeBpelException;

/**
 * Selects an element variable to assign to
 */
public class AeToVariableElement extends AeToBase
{
   /** variable */
   private IAeVariable mVariable;

   /**
    * Ctor accepts def 
    * 
    * @param aToDef
    */
   public AeToVariableElement(AeToDef aToDef)
   {
      super(aToDef);
   }
   
   /**
    * Ctor accepts variable name
    * 
    * @param aVariableName
    */
   public AeToVariableElement(String aVariableName)
   {
      setVariableName(aVariableName);
   }

   /**
    * Ctor accepts variable
    * 
    * @param aVariable
    */
   public AeToVariableElement(IAeVariable aVariable)
   {
      setVariable(aVariable);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeTo#getTarget()
    */
   public Object getTarget() throws AeBpelException
   {
      return new AeVariableElementDataWrapper(getVariable());
   }

   /**
    * Overrides method to return variable set by {@link #setVariable(IAeVariable)} if defined.
    * 
    * @see org.activebpel.rt.bpel.impl.activity.assign.to.AeToBase#getVariable()
    */
   protected IAeVariable getVariable()
   {
      return (mVariable != null) ? mVariable : super.getVariable();
   }

   /**
    * @param aVariable The variable to set.
    */
   public void setVariable(IAeVariable aVariable)
   {
      mVariable = aVariable;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.to.AeToBase#getAttachmentsTarget()
    */
   public IAeAttachmentContainer getAttachmentsTarget()
   {
      // fixme (MF-attachments) why do we clear the attachments container when copying to it?
      IAeAttachmentContainer toContainer = getVariable().getAttachmentData();
      toContainer.clear();
      return toContainer;
   }
}
 