//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/handlers/AeXMLDBFilteredListResponseHandler.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers;

import java.util.Collection;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBQueryBuilder;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;

/**
 * This is a filtered list query handler that can be used to retrieve a portion of a result.  The 
 * filtered list includes the result # to start with and the # of results to return.  The handler
 * attempts to figure out how many results there are but stops counting at 500.
 */
public abstract class AeXMLDBFilteredListResponseHandler extends AeXMLDBListResponseHandler
{
   /** The number of rows in the <code>Response</code>. */
   private int mRowCount = 0;
   /** A flag indicating if the row count is complete or cut off. */
   private boolean mCompleteRowCount = false;

   /**
    * Constructor.
    */
   public AeXMLDBFilteredListResponseHandler()
   {
      super();
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBCollectionResponseHandler#handleIterator(org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse, java.util.Collection)
    */
   protected void handleIterator(IAeXMLDBXQueryResponse aResponse, Collection aCollection) throws AeXMLDBException
   {
      // The first item in the response will be the count of items found.  So get that number
      // before processing the rest of the result set as per normal.
      if (aResponse.hasNextElement())
      {
         Element elem = aResponse.nextElement();
         String countStr = AeXmlUtil.getText(elem);
         Integer count = new Integer(countStr);
         setRowCount(count.intValue());
      }

      // Now handle the rest of the items normally.
      super.handleIterator(aResponse, aCollection);
   }
   
   /**
    * Returns the number of rows.
    */
   public int getRowCount()
   {
      return mRowCount;
   }

   /**
    * Sets the number of rows.
    */
   protected void setRowCount(int aRowCount)
   {
      if (aRowCount > AeXMLDBQueryBuilder.LIMIT)
      {
         mRowCount = AeXMLDBQueryBuilder.LIMIT;
         setCompleteRowCount(false);
      }
      else
      {
         mRowCount = aRowCount;
         setCompleteRowCount(true);
      }
   }

   /**
    * @return Returns the completeRowCount.
    */
   public boolean isCompleteRowCount()
   {
      return mCompleteRowCount;
   }

   /**
    * @param aCompleteRowCount The completeRowCount to set.
    */
   protected void setCompleteRowCount(boolean aCompleteRowCount)
   {
      mCompleteRowCount = aCompleteRowCount;
   }
}
