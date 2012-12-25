//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/coord/handlers/AeCoordinatingListResponseHandler.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.coord.handlers;

import org.activebpel.rt.bpel.coord.IAeCoordinationManager;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler;
import org.w3c.dom.Element;

/**
 * A XMLDB response handler that returns a List of IAeCoordinating instances.
 */
public class AeCoordinatingListResponseHandler extends AeXMLDBListResponseHandler
{
   /** The coordination manager. */
   private IAeCoordinationManager mManager;

   /**
    * Default ctor.
    */
   public AeCoordinatingListResponseHandler(IAeCoordinationManager aManager)
   {
      setManager(aManager);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler#handleElement(org.w3c.dom.Element)
    */
   protected Object handleElement(Element aElement) throws AeXMLDBException
   {
      return AeCoordinatingResponseHandler.createCoordinatingFromElement(aElement, getManager());
   }

   /**
    * @return Returns the manager.
    */
   protected IAeCoordinationManager getManager()
   {
      return mManager;
   }
   
   /**
    * @param aManager The manager to set.
    */
   protected void setManager(IAeCoordinationManager aManager)
   {
      mManager = aManager;
   }
}
