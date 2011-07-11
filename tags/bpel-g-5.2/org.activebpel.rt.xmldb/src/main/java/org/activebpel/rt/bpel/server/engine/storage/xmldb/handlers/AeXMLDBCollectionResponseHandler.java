//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/handlers/AeXMLDBCollectionResponseHandler.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
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
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse;
import org.activebpel.rt.xmldb.AeMessages;
import org.w3c.dom.Element;

/**
 * Implements a base class for any query handler that returns a collection of objects.
 */
public abstract class AeXMLDBCollectionResponseHandler extends AeXMLDBResponseHandler
{
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler#handleResponse(org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBXQueryResponse)
    */
   public Object handleResponse(IAeXMLDBXQueryResponse aResponse) throws AeXMLDBException
   {
      try
      {
         Collection coll = createCollection();
         handleIterator(aResponse, coll);
         return coll;
      }
      catch (Exception e)
      {
         throw new AeXMLDBException(AeMessages.getString("AeXMLDBListResponseHandler.ERROR_ITERATING_THROUGH_XMLDB_RESULT"), e); //$NON-NLS-1$
      }
   }
   
   /**
    * Handles the xml iterator.  This method will iterate through all items in the iterator, call
    * handleXMLObject on each item, and add the result to the given List.
    * 
    * @param aResponse
    * @param aCollection
    * @throws AeXMLDBException
    */
   protected void handleIterator(IAeXMLDBXQueryResponse aResponse, Collection aCollection)
         throws AeXMLDBException
   {
      while (aResponse.hasNextElement())
      {
         Element elem = aResponse.nextElement();
         aCollection.add(handleElement(elem));
      }
   }

   /**
    * Called to convert an Element (taken from a TXMLObject) to some other object that will be put in
    * the result List.
    * 
    * @param aElement
    */
   protected abstract Object handleElement(Element aElement) throws AeXMLDBException;
   
   /**
    * Creates the Collection that will be used to tally the results.
    */
   protected abstract Collection createCollection();
}
