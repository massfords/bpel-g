// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeToVariableType.java,v 1.6 2007/05/24 00:50:32 KRoe Exp $
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
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.util.AeXmlUtil;
import org.exolab.castor.xml.schema.XMLType;

/**
 * Gets the type value to receive the data  
 */
public class AeToVariableType extends AeToBase
{
   /**
    * Ctor accepts def 
    * 
    * @param aToDef
    */
   public AeToVariableType(AeToDef aToDef)
   {
      super(aToDef);
   }
   
   /**
    * Ctor accepts variable type
    * 
    * @param aVariable
    */
   public AeToVariableType(String aVariable)
   {
      setVariableName(aVariable);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeTo#getTarget()
    */
   public Object getTarget() throws AeBpelException
   {
      XMLType varType = getVariable().getDefinition().getXMLType();
      if (AeXmlUtil.isComplexOrAny(varType))
      {
         return new AeVariableComplexTypeDataWrapper(getVariable());
      }
      else
      {
         return new AeVariableSimpleTypeDataWrapper(getVariable());
      }
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
 