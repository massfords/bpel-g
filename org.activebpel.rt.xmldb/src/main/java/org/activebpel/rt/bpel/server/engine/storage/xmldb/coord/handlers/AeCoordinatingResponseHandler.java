//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/coord/handlers/AeCoordinatingResponseHandler.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.coord.handlers;

import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinationManager;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageUtil;
import org.activebpel.rt.bpel.server.engine.storage.sql.IAeCoordinationColumns;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.coord.IAeCoordinationElements;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implements a XMLDB response handler that returns a single instance of IAeCoordinating.
 */
public class AeCoordinatingResponseHandler extends AeXMLDBSingleObjectResponseHandler
{
   /** The coordination manager. */
   private IAeCoordinationManager mManager;

   /**
    * Default ctor.
    */
   public AeCoordinatingResponseHandler(IAeCoordinationManager aManager)
   {
      super();

      setManager(aManager);
   }

   /**
    * Creates an IAeCoordinating instance from the given XMLDB response element.
    * 
    * @param aElement
    * @param aManager
    * @throws AeXMLDBException
    */
   public static IAeCoordinating createCoordinatingFromElement(Element aElement,
         IAeCoordinationManager aManager) throws AeXMLDBException
   {
      int coordRole = getIntFromElement(aElement, IAeCoordinationElements.COORDINATION_ROLE).intValue();
      String coordId = getStringFromElement(aElement, IAeCoordinationElements.COORDINATION_ID);
      String state = getStringFromElement(aElement, IAeCoordinationElements.STATE);
      long processId = getLongFromElement(aElement, IAeCoordinationElements.PROCESS_ID).longValue();
      Document contextDocument = getDocumentFromElement(aElement, IAeCoordinationColumns.COORDINATION_DOC);

      try
      {
         return AeStorageUtil.createCoordinating(processId, coordId, state, coordRole, contextDocument, aManager);
      }
      catch (AeStorageException ex)
      {
         throw new AeXMLDBException(ex);
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler#handleElement(org.w3c.dom.Element)
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
