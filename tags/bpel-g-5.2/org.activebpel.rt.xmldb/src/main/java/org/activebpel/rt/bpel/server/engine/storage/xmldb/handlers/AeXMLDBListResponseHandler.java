//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/handlers/AeXMLDBListResponseHandler.java,v 1.2 2007/08/23 21:20:57 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implements a base class for any query handler that returns a list of objects.
 */
public abstract class AeXMLDBListResponseHandler extends AeXMLDBCollectionResponseHandler
{
   /**
    * Creates the List that will be used to tally the results.
    */
   protected Collection createCollection()
   {
      return new ArrayList();
   }
}
