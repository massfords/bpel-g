// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/IAeXMLDBXQueryResponse.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb;

import org.w3c.dom.Element;

/**
 * Represents an XQuery response from an XML database.  Allows iteration over
 * the result set.
 */
public interface IAeXMLDBXQueryResponse
{
   /**
    * Returns true if the response has another unfetched element.
    */
   public boolean hasNextElement();

   /**
    * Returns the next element in the result set.
    */
   public Element nextElement();
}
