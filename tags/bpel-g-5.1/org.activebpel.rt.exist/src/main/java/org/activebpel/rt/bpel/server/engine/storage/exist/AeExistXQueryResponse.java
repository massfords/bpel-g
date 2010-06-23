// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.exist/src/org/activebpel/rt/bpel/server/engine/storage/exist/AeExistXQueryResponse.java,v 1.1 2007/08/17 00:59:50 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.exist;

import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse;
import org.w3c.dom.Element;

/**
 * An eXist response from an XQuery.
 */
public class AeExistXQueryResponse implements IAeXMLDBXQueryResponse
{
   /** Iterator over the results. */
   private Iterator mIterator;

   /**
    * C'tor.
    * 
    * @param aElements
    */
   public AeExistXQueryResponse(List aElements)
   {
      setIterator(aElements.iterator());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse#hasNextElement()
    */
   public boolean hasNextElement()
   {
      return getIterator().hasNext();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse#nextElement()
    */
   public Element nextElement()
   {
      return (Element) getIterator().next();
   }

   /**
    * @return Returns the iterator.
    */
   protected Iterator getIterator()
   {
      return mIterator;
   }

   /**
    * @param aIterator the iterator to set
    */
   protected void setIterator(Iterator aIterator)
   {
      mIterator = aIterator;
   }

}
