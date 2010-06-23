//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/handlers/AeXMLDBSetResponseHandler.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
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
import java.util.HashSet;

/**
 * Implements a base class for any query handler that returns a list of objects.
 */
public abstract class AeXMLDBSetResponseHandler extends AeXMLDBCollectionResponseHandler
{
   /**
    * Creates the List that will be used to tally the results.
    */
   protected Collection createCollection()
   {
      return new HashSet();
   }
}
