// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/from/AeFromBase.java,v 1.5.12.1 2008/04/21 16:09:44 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.from; 

import org.activebpel.rt.attachment.IAeAttachmentContainer;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.impl.activity.assign.AeCopyOperationComponentBase;
import org.activebpel.rt.bpel.impl.activity.assign.IAeFrom;

/**
 * Base class for objects implementing the selection of data for a &lt;from&gt; construct 
 */
public abstract class AeFromBase extends AeCopyOperationComponentBase implements IAeFrom
{
   /**
    * Ctor for the base accepts def
    */
   public AeFromBase(AeFromDef aDef)
   {
      setVariableName(aDef.getVariable());
   }
   
   /**
    * No arg ctor 
    */
   protected AeFromBase()
   {
   }

   /**
    * Getter for the variable
    */
   protected IAeVariable getVariable()
   {
      return getCopyOperation().getContext().getVariable(getVariableName());
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeFrom#getAttachmentsSource()
    */
   public IAeAttachmentContainer getAttachmentsSource()
   {
      // Default behaviour
      return null;
   }
}
 