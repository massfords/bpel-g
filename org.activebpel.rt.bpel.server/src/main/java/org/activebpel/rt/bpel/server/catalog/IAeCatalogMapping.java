// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/IAeCatalogMapping.java,v 1.2 2006/08/04 17:57:53 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog;

import java.io.IOException;

import org.activebpel.rt.AeException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Interface for deploying resources to the catalog.
 */
public interface IAeCatalogMapping
{
   /** 
    * Location hint.  
    * Should be fully qualified url.
    */
   public String getLocationHint();
  
   /**
    * @return Returns the typeURI.
    */
   public String getTypeURI();
   
   /** 
    * Target namespace for this mapping entry, if it is WSDL or Schema it comes
    * from the targetNamespace attribute.  
    */
   public String getTargetNamespace();
   
   /**
    * Access resource as InputSource.
    * @throws IOException
    */
   public InputSource getInputSource() throws IOException;

   /**
    * Access resource as a Document.
    * @throws AeException
    */
   public Document getDocument() throws AeException;
   
   /**
    * Return true if this is a wsdl entry.
    */
   public boolean isWsdlEntry();
   
   /**
    * Return true if this is a schema entry.
    */
   public boolean isSchemaEntry();
}
