// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeToVariableMessage.java,v 1.7 2007/05/24 00:50:32 KRoe Exp $
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
import org.activebpel.rt.bpel.impl.activity.assign.AeMessageVariableWrapper;

/**
 * Returns the message variable which will receive all of the parts of the src message 
 */
public class AeToVariableMessage extends AeToBase
{
   /**
    * Ctor accepts def and context
    * 
    * @param aToDef
    */
   public AeToVariableMessage(AeToDef aToDef)
   {
      super(aToDef);
   }
   
   /**
    * Ctor accepts variable 
    * 
    * @param aVariable
    */
   public AeToVariableMessage(String aVariable)
   {
      setVariableName(aVariable);
   }

   /**
    * Returns the message data for the variable that will get populated
    * 
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeTo#getTarget()
    */
   public Object getTarget() throws AeBpelException
   {
      IAeVariable variable = getVariable();
      return new AeMessageVariableWrapper(variable);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.to.AeToBase#getAttachmentsTarget()
    */
   public IAeAttachmentContainer getAttachmentsTarget()
   {
      IAeAttachmentContainer toContainer = getVariable().getAttachmentData();
      toContainer.clear();
      return toContainer;
   }
}
 