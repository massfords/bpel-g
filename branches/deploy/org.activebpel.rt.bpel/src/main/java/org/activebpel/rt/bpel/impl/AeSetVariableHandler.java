//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeSetVariableHandler.java,v 1.4 2007/06/28 22:00:45 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.attachment.IAeAttachmentContainer;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcessEngine;

/**
 *  This class is used to set the new value of a variable from 
 * user/console input.
 */
public class AeSetVariableHandler extends AeVariableDeserializer
{
   /** Validate document when 'true' */
   private boolean mValidate;
   
   /**
    * Constructor.
    * @param aEngine
    */
   public AeSetVariableHandler(IAeBusinessProcessEngine aEngine, boolean aValidate)
   {
      super(aEngine);
      mValidate = aValidate;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.AeVariableDeserializer#hasData()
    */
   protected boolean hasData() throws AeBusinessProcessException
   {
      // TODO (MF) can user set variable back to uninitialized state?
      return true;
   }
   
   /**
    * Return validate mode
    */
   public boolean isValidate()
   {
      return mValidate;
   }
   
   /**
    * Restores the variable from the serialized variable document.
    *
    * @throws AeBusinessProcessException
    */
   public void restoreVariable() throws AeBusinessProcessException
   {
      IAeAttachmentContainer preserveAttachments = null; 
      if (getVariable().hasAttachments())   
         preserveAttachments = getVariable().getAttachmentData();
      
      super.restoreVariable();
      
      if (preserveAttachments != null)
         getVariable().setAttachmentData(preserveAttachments);
 
      if (isValidate())
         getVariable().validate(true);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.AeVariableDeserializer#restoreVersionNumber()
    */
   protected void restoreVersionNumber() throws AeBusinessProcessException
   {
      // no op here - let the version number get updated every time the variable data is set
   }
}
