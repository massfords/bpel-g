//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/IAeFrom.java,v 1.4.4.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign; 

import org.activebpel.rt.attachment.IAeAttachmentContainer;
import org.activebpel.rt.bpel.AeBusinessProcessException;


/**
 * Interface for an impl object that knows how to extract data from an AeFromDef 
 */
public interface IAeFrom
{
   /**
    * Extracts the data for the copy operation based on the info in the &lt;from&gt; definition
    * @throws AeBusinessProcessException
    */
   public Object getFromData() throws AeBusinessProcessException;
   
   /**
    * Setter for the copy operation that is the parent of this &lt;from&gt;
    * @param aCopyOperation
    */
   public void setCopyOperation(IAeCopyOperation aCopyOperation);
   
   /**
    * Returns the attachment container associated with the from data
    */
   public IAeAttachmentContainer getAttachmentsSource();
}
 