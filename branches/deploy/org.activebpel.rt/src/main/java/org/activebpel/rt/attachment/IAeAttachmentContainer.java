// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/attachment/IAeAttachmentContainer.java,v 1.3 2007/05/09 20:28:23 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.attachment;

import java.util.Iterator;
import java.util.List;

/**
 * Base interface required to be implemented by all internal attachment containers and wrappers. Basically
 * forces containers to extend List
 */
public interface IAeAttachmentContainer extends List
{
   /** Return iterator to attachment items */
   public Iterator getAttachmentItems();
    
   /**
    * @return true when there are attachment items, otherwise false
    */
   public boolean hasAttachments();
   
   /**
    * Copy attachments from another source 
    * @param aAttachmentSource
    */
   public void copy(IAeAttachmentContainer aAttachmentSource);
}
