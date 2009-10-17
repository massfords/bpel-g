// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/AeCatalogMappings.java,v 1.2 2006/08/04 17:57:53 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog;

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.def.IAeBPELExtendedWSDLConst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Wraps the details of catalog xml file.  It creates a <code>AeCatalogBprMapping</code>
 * object for every entry in the catalog document.  It also validates that the documents exist 
 * in the BPR file.
 */
public class AeCatalogMappings
{
   /** wsdl entry tag */
   private static final String WSDL_ENTRY_TAG = "wsdlEntry"; //$NON-NLS-1$
   /** schema entry tag */
   private static final String SCHEMA_ENTRY_TAG = "schemaEntry"; //$NON-NLS-1$
   /** other entry tag */
   private static final String OTHER_ENTRY_TAG = "otherEntry"; //$NON-NLS-1$
   /** location attribute */
   private static final String ATTR_LOCATION = "location"; //$NON-NLS-1$
   /** classpath attribute */
   private static final String ATTR_CLASS_PATH = "classpath"; //$NON-NLS-1$
   /** type uri attribute */
   private static final String ATTR_TYPE_URI = "typeURI"; //$NON-NLS-1$
   /** replace wsdl attribute */
   private static final String ATTR_REPLACE = IAeEngineConfiguration.REPLACE_EXISTING_ENTRY;
   /** The bpr file containing the catalog. */
   private IAeBpr mBpr;
   /** Store the AeCatalogBprMapping mappings. */
   private Map mResources = new HashMap();
   /** Store the AeCatalogBprMapping of missing resources. */
   private Map mMissingResources = new HashMap();
   /** Replace any existing resource entries. */
   private boolean mReplaceExistingResource;
   
   /**
    * Constructor.
    * @param aBpr The Bpr containing the catalog.
    * @throws AeException 
    */
   public AeCatalogMappings( IAeBpr aBpr ) throws AeException
   {
      mBpr = aBpr;
      parse( getBpr().getCatalogDocument() );               
   }
   
   /**
    * Constructor, which allows overriding of catalog overwrite flag.
    * @param aBpr The Bpr containing the catalog.
    * @param aReplaceExistingResource the flag to use for replace existing resources.
    * @throws AeException 
    */
   public AeCatalogMappings( IAeBpr aBpr, boolean aReplaceExistingResource) throws AeException
   {
      this(aBpr);
      mReplaceExistingResource = aReplaceExistingResource;
   }
   
   /**
    * Populate the wsdl and schema maps.
    * @param aCatalogDocument
    */
   protected void parse( Document aCatalogDocument )
   {
      if( aCatalogDocument != null )
      {
         addEntries(aCatalogDocument, WSDL_ENTRY_TAG, IAeBPELExtendedWSDLConst.WSDL_NAMESPACE );
         addEntries(aCatalogDocument, SCHEMA_ENTRY_TAG, IAeConstants.W3C_XML_SCHEMA);
         addEntries(aCatalogDocument, OTHER_ENTRY_TAG, null);
         initReplaceResourceFlag( aCatalogDocument );
      }
   }
   
   /**
    * Initializes the "replace resource file" flag. Uses the attribute in the catalog if it
    * is there, or the value in the engine configuration otherwise.
    * 
    * @param aCatalogDocument
    */
   protected void initReplaceResourceFlag( Document aCatalogDocument )
   {
      String domAttribute = aCatalogDocument.getDocumentElement().getAttribute( ATTR_REPLACE );
      // if the "replace-existing" attribute is specified in the catalog
      // then respect it's value - otherwise, consult the engine config for the
      // global setting
      if( AeUtil.notNullOrEmpty(domAttribute) )
      {
         mReplaceExistingResource = AeUtil.toBoolean( domAttribute );
      }
      else
      {
         mReplaceExistingResource = AeEngineFactory.getEngineConfig().isResourceReplaceEnabled();
      }
   }
   
   /**
    * Return true if any existing resource entries should be replaced.
    */
   public boolean replaceExistingResource()
   {
      return mReplaceExistingResource;
   }
   
   /**
    * Look for the nodeset by tag name and add the entries to the 
    * given map.
    * @param aCatalogDocument The catalog document.
    * @param aTagName The tag name to look for.
    * @param aDefaultTypeURI the default type uri or null if one must be in element
    */
   protected void addEntries( Document aCatalogDocument, String aTagName, String aDefaultTypeURI )
   {
      NodeList entries = 
         aCatalogDocument.getDocumentElement().getElementsByTagNameNS("*",aTagName); //$NON-NLS-1$

      int max = entries.getLength();
      for( int i = 0; i < max; i++ )
      {
         Element entryElement = (Element)entries.item(i);
         String urlKey = makeKey(entryElement.getAttribute(ATTR_LOCATION));
         String classPathValue = entryElement.getAttribute(ATTR_CLASS_PATH);
         if(aDefaultTypeURI == null)
            aDefaultTypeURI = entryElement.getAttribute(ATTR_TYPE_URI);
         AeCatalogBprMapping entry = new AeCatalogBprMapping(getBpr(), urlKey, aDefaultTypeURI, classPathValue );
         if(entry.exists())
            getResources().put( urlKey, entry );
         else
            getMissingResources().put( urlKey, entry );
      }
   }
   
   /**
    * @return Returns the Mappings which are of type <code>AeCatalogBprMapping</code> these are mapping that exist in the BPR.
    */
   public Map getResources()
   {
      return mResources;
   }

   /**
    * @return Returns the missingMappings map which are of type <code>AeCatalogBprMapping</code>.
    */
   public Map getMissingResources()
   {
      return mMissingResources;
   }
   
   /**
    * @return Returns the bpr.
    */
   public IAeBpr getBpr()
   {
      return mBpr;
   }
   
   /**
    * Construct a key for resource location hints.
    * @param aLocation
    */
   public static String makeKey( String aLocation )
   {
      return aLocation.replace('\\','/');
   }
}
