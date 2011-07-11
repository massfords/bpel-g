//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/IAeXMLDBResponseHandler.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb;


/**
 * This interface defines a way in which XMLDB query results are handled and returned as a List.
 */
public interface IAeXMLDBResponseHandler
{
   /**
    * This method is called to convert a XMLDB response object into some kind of
    * result. This result could be a List of objects or it could simply be an
    * Integer.  The response object will be database specific.
    * 
    * @param aResponse
    */
   public Object handleResponse(IAeXMLDBXQueryResponse aResponse) throws AeXMLDBException;
}
