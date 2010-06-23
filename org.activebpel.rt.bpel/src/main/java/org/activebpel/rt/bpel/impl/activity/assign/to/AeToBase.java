// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeToBase.java,v 1.6.12.1 2008/04/21 16:09:44 ppatruni Exp $
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
import org.activebpel.rt.bpel.impl.activity.assign.AeCopyOperationComponentBase;
import org.activebpel.rt.bpel.impl.activity.assign.IAeTo;

/**
 * Base class for the objects implementing the &lt;to&gt; behavior in a copy operation 
 */
public abstract class AeToBase extends AeCopyOperationComponentBase implements IAeTo
{
   /**
    * Ctor accepts def and context
    * 
    * @param aToDef
    */
   public AeToBase(AeToDef aToDef)
   {
      super();
      setVariableName(aToDef.getVariable());
   }
   
   /**
    * Ctor
    */
   protected AeToBase()
   {
   }
   
   /**
    * Gets the variable (for update) from the copy operation context.
    */
   protected IAeVariable getVariable()
   {
      IAeVariable var = getCopyOperation().getContext().getVariableForUpdate(getVariableName(), getPartName());
      return var;
   }
   
   /**
    * Returns the part name for the assign or null if the strategy isn't dealing
    * with a message variable part.
    */
   protected String getPartName()
   {
      return null;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeTo#getAttachmentsTarget()
    */
   public IAeAttachmentContainer getAttachmentsTarget()
   {
      // Default behaviour
      return null;
   }
}
 