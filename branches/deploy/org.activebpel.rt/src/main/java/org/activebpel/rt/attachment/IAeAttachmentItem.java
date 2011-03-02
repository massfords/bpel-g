// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/attachment/IAeAttachmentItem.java,v 1.3 2007/05/08 18:45:50 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.attachment;

import java.util.Map;

/**
 * Interface describing the access to an internal bpel attachment item
 */
public interface IAeAttachmentItem
{
   /**
    * @return <code>Map</code> of attachment headers
    */
   public Map getHeaders();
   
   /**
    * Returns the value of the header corresponding to the given header name.
    *
    * @param aHeaderName
    * @return the value of the specified header
    */
   public String getHeader(String aHeaderName);
   
   /**
    * Returns the attachment's id.
    */
   public long getAttachmentId();

   /**
    * Returns the id of the attachment's associated process.
    */
   public long getProcessId();
}
