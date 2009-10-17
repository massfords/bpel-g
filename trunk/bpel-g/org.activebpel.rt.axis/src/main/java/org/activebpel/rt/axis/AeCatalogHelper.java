// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/AeCatalogHelper.java,v 1.9 2008/02/21 18:14:06 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.list.AeCatalogItemDetail;
import org.activebpel.rt.bpel.server.catalog.resource.AeResourceKey;
import org.activebpel.rt.bpel.server.catalog.resource.IAeResourceKey;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.IAeBPELExtendedWSDLConst;

/**
 * Servlet designed to provide the WSDL which is part of a BPR deployment.
 */
public class AeCatalogHelper
{
   /**
    * Returns the wsdl file loaded in a string based on the passed wsdl entry name. It uses the
    * passed request url to fixup any imported other catalog entries.
    * @param aEntry The wsdl catalog entry.
    * @param aRequestURL The url the entry is being requested via for fixup purposes.
    */
   public static String getCatalogWsdl(String aEntry, String aRequestURL) throws AeException
   {
      try
      {
         // Get the WSDL and make a copy before modfiying to make this thread safe
         IAeResourceKey key = new AeResourceKey(aEntry, IAeBPELExtendedWSDLConst.WSDL_NAMESPACE);
         AeBPELExtendedWSDLDef def = (AeBPELExtendedWSDLDef)AeEngineFactory.getCatalog().getResourceCache().getResource( key );
         AeBPELExtendedWSDLDef defCopy = new AeBPELExtendedWSDLDef(def);
         AeWsdlImportFixup.fixupImportReferences(aRequestURL, defCopy);
         return defCopy.toString();
      }
      catch (AeException ex)
      {
         ex.logError();
         throw ex;
      } 
   }

   /**
    * Returns the schema file loaded in a string based on the passed schema entry name. It uses the
    * passed request url to fixup any imported other catalog entries.
    * @param aEntry The entry name in the catalog
    * @param aRequestURL The url the catalog is being requested via for fixups.
    * @return The schema as a string.
    */
   public static String getCatalogSchema(String aEntry, String aRequestURL) throws AeException
   {
      AeCatalogItemDetail detail = AeEngineFactory.getEngineAdministration().getCatalogAdmin().getCatalogItemDetail(aEntry);
      if (detail != null)
      {
         return detail.getText();
      }
      else
      {
         // item detail not found for the given entry/location hint.
         return null;
      }
   }
}