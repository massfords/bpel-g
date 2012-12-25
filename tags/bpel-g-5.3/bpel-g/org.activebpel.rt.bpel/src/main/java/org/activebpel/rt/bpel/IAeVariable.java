// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeVariable.java,v 1.29.4.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import org.activebpel.rt.attachment.IAeAttachmentContainer;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeInvalidVariableException;
import org.activebpel.rt.bpel.impl.activity.IAeVariableContainer;
import org.activebpel.rt.message.IAeMessageData;
import org.w3c.dom.Element;

/**
 * Describes the interface used for interacting with business process variables
 * these Business Process variables are associated with either messages or
 * standard schema described xml data.
 */
public interface IAeVariable extends IAeVariableView, IAeLocatableObject
{
   /**
    * Get the definition of the variable.
    */
   public AeVariableDef getDefinition();

   /**
    * Get the version number.
    */
   public int getVersionNumber();

   /**
    * Sets the message data associated with the variable.
    */
   public void setMessageData(IAeMessageData aMessage);

   /**
    * Get the attachment container associated with the variable, returns null if there are no attachments
    */
   public IAeAttachmentContainer getAttachmentData();

   /**
    * Sets the Attachment container associated with the variable.
    * @param aAttachmentContainer
    */
   public void setAttachmentData(IAeAttachmentContainer aAttachmentContainer);

   /**
    * Set the type data associated with this variable.
    */
   public void setTypeData(Object aTypeData);

   /**
    * Set the element data associated with this variable.
    */
   public void setElementData(Element aElement);

   /**
    * Performs validation against the data set for this variable. An
    * AeBusinessProcessException is thrown if the data is invalid.
    */
   public void validate() throws AeInvalidVariableException, AeBpelException;

   /**
    * Performs validation against the data set for this variable. An
    * AeBusinessProcessException is thrown if the data is invalid.
    * @param aAllowEmptyPartData set to True if an empty message should pass validation False otherwise
    */
   public void validate(boolean aAllowEmptyPartData) throws AeInvalidVariableException, AeBpelException;

   /**
    * Clears the value for the variable. Called from the variable's declaring
    * scope each time the scope is going to execute since the variable's state
    * is not preserved across invocations.
    */
   public void clear();

   /**
    * Returns <code>true</code> if and only if the variable has attachments.
    */
   public boolean hasAttachments();

   /**
    * Sets the version number.
    *
    * @param aVersionNumber
    */
   public void setVersionNumber(int aVersionNumber);

   /**
    * Increments the version number for the variable
    */
   public void incrementVersionNumber();

   /**
    * @return Returns the scope which owns this variable.
    */
   public IAeVariableContainer getParent();

   /**
    * Clones the variable
    */
   public Object clone();

   /**
    * Restores the variable's data from its clone
    *
    * @param aMyClone
    */
   public void restore(IAeVariable aMyClone);

   /**
    * Initializes the variable with its default value prior to being used in
    * an &lt;assign&gt;'s copy operation. If the variable (and part) has already been
    * initialized then this call has no effect.
    *
    * @param aPartName The part name of null if the variable is not a message.
    */
   public void initializeForAssign(String aPartName);
}
