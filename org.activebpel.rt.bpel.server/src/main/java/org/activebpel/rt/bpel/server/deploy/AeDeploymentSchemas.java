// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeDeploymentSchemas.java,v 1.7 2007/11/28 17:37:29 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.activebpel.rt.wsdl.def.castor.AeSchemaParserUtil;
import org.exolab.castor.xml.schema.Schema;

/**
 * Helper class for loading schemas used for deploying bpel processes to the engine. 
 * todo (cck) do these schemas really need to be loaded statically (performance?)
 */
public class AeDeploymentSchemas
{
   /** set of the pdd schemas */
   private static final Set PDD_SCHEMAS = new HashSet();
   /** set of the pdef schemas */
   private static final Set PDEF_SCHEMAS = new HashSet();
   /** set of the catalog schemas */
   private static final Set CATALOG_SCHEMAS = new HashSet();

   static
   {
      // Load the PDD schemas
      PDD_SCHEMAS.add(loadSchema("/org/activebpel/rt/bpel/server/deploy/pdd.xsd")); //$NON-NLS-1$
      PDD_SCHEMAS.add(loadSchema("/org/activebpel/rt/bpel/server/deploy/pdd_2_1.xsd")); //$NON-NLS-1$      
      PDD_SCHEMAS.add(loadSchema("/org/activebpel/rt/bpel/server/deploy/pdd_1_0.xsd")); //$NON-NLS-1$
      PDD_SCHEMAS.add(loadSchema("/org/activebpel/rt/bpel/server/deploy/pdd_pre_1_0.xsd")); //$NON-NLS-1$

      // Load the PDEF schemas.
      PDEF_SCHEMAS.add(loadSchema("/org/activebpel/rt/bpel/server/deploy/pdef.xsd")); //$NON-NLS-1$
      
      // Load the PDD schemas
      CATALOG_SCHEMAS.add(loadSchema("/org/activebpel/rt/bpel/server/deploy/catalog.xsd")); //$NON-NLS-1$
      CATALOG_SCHEMAS.add(loadSchema("/org/activebpel/rt/bpel/server/deploy/wsdlCatalog.xsd")); //$NON-NLS-1$
   }
   
   /**
    * Gets an iterator containing the pdd schemas as castor schema objects
    */
   public static Iterator getPddSchemas()
   {
      return PDD_SCHEMAS.iterator();      
   }

   /**
    * Gets an iterator containing the pdef schemas as castor schema objects
    */
   public static Iterator getPdefSchemas()
   {
      return PDEF_SCHEMAS.iterator();      
   }

   /**
    * Gets an iterator containing the catalog schemas as castor schema objects
    */
   public static Iterator getCatalogSchemas()
   {
      return CATALOG_SCHEMAS.iterator();      
   }

   /**
    * Loads schemas relative to this class's packaging.
    * @param aPath
    */
   private static Schema loadSchema(String aPath)
   {
      return AeSchemaParserUtil.loadSchema(aPath, AeDeploymentSchemas.class);
   }
}
