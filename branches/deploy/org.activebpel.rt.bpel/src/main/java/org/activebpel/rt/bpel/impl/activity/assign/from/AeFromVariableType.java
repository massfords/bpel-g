// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/from/AeFromVariableType.java,v 1.5 2007/05/24 00:50:32 KRoe Exp $
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
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;

/**
 * Handles selecting data from a variable type.
 */
public class AeFromVariableType extends AeFromBase
{
   /**
    * Ctor takes def
    * 
    * @param aFromDef
    */
   public AeFromVariableType(AeFromDef aFromDef)
   {
      super(aFromDef);
   }
   
   /**
    * Ctor accepts variable name
    * @param aVariableName
    */
   public AeFromVariableType(String aVariableName)
   {
      setVariableName(aVariableName);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeFrom#getFromData()
    */
   public Object getFromData() throws AeBusinessProcessException
   {
      return getVariable().getTypeData();
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.from.AeFromBase#getAttachmentsSource()
    */
   public IAeAttachmentContainer getAttachmentsSource()
   {
      return getVariable().getAttachmentData();
   }
}
 