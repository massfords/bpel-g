//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/from/AeFromVariableMessagePartWithAttachments.java,v 1.1 2007/06/09 01:07:37 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.from; 

import java.util.Iterator;

import org.activebpel.rt.AeException;
import org.activebpel.rt.attachment.IAeAttachmentContainer;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.impl.AeUninitializedVariableException;

/**
 * Handles selecting data from a variable part with attachments. The data will either be a xsd simple type,
 * element, or complex type.
 */
public class AeFromVariableMessagePartWithAttachments extends AeFromVariableMessagePart
{

   /**
    * Ctor accepts the def object
    * 
    * @param aFromDef
    */
   public AeFromVariableMessagePartWithAttachments(AeFromDef aFromDef)
   {
      super(aFromDef);
   }
   
   /**
    * Ctor accepts variable name and part name
    * @param aVariableName
    * @param aPartName
    */
   public AeFromVariableMessagePartWithAttachments(String aVariableName, String aPartName)
   {
     super(aVariableName,aPartName);
   }

   /**
    * Ctor accepts variable and part name
    * @param aVariable
    * @param aPartName
    */
   public AeFromVariableMessagePartWithAttachments(IAeVariable aVariable, String aPartName)
   {
     super( aVariable, aPartName);
   }

   /**
    * Return an attachment source only for the first part of the variable. This is consistent with Requirement 160
    * @see org.activebpel.rt.bpel.impl.activity.assign.from.AeFromBase#getAttachmentsSource()
    */
   public IAeAttachmentContainer getAttachmentsSource()
   {
      try
      {
         for (Iterator itr = getVariable().getMessageData().getPartNames(); itr.hasNext();)
         {
            if(getPart().equals(itr.next()))
            {
               return getVariable().getAttachmentData();
            }
            break;
         }
      }
      catch (AeUninitializedVariableException ex)
      {
         // Should never happen
         AeException.logError(ex);
      }
      return null;
   }

}
