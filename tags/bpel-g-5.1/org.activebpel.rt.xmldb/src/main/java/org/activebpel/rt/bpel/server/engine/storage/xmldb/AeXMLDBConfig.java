// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeXMLDBConfig.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageConfig;

/**
 * This class encapsulates the XMLDB XQuery statements used by the Active BPEL XMLDB based persistence
 * layer. This class uses a SAX parser to first parse an xml file that contains the XQuery queries.
 * 
 * FIXMEQ (xmldb) parameterize the xmldb config (list of config files to load) and pull that info from engine config (only need 1 config impl)
 */
public class AeXMLDBConfig extends AeStorageConfig
{
   private static final String XQUERY_NAME = "xquery"; //$NON-NLS-1$
   private static final String XQUERY_STATEMENT_NAME = "xquery-statement"; //$NON-NLS-1$

   /**
    * Creates a XMLDB config object.
    */
   public AeXMLDBConfig()
   {
      this(Collections.EMPTY_MAP);
   }

   /**
    * Creates a XMLDB config object with the given map of constant overrides.
    * 
    * @param aConstantOverrides
    */
   public AeXMLDBConfig(Map aConstantOverrides)
   {
      super(XQUERY_STATEMENT_NAME, XQUERY_NAME, aConstantOverrides);
   }

   /**
    * Gets a statement given a key (the name of the statement as configured in the file).
    * 
    * @param aKey A key that references a statement in the config file.
    * @return A statement.
    */
   public String getXQueryStatement(String aKey)
   {
      return (String) getStatement(aKey);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.AeStorageConfig#getStatementConfigFilenames()
    */
   protected List getStatementConfigFilenames()
   {
      String fileName = "xmldb-queries.xml"; //$NON-NLS-1$

      List list = new ArrayList();
      list.add(new AeFilenameClassTuple(fileName, AeXMLDBConfig.class));
      
      return list;
   }

   /**
    * Gets the xquery statement element name.
    */
   protected String getStatementElementName()
   {
      return XQUERY_STATEMENT_NAME; 
   }
   
   /**
    * Gets the xquery element name.
    */
   protected String getStatementValueElementName()
   {
      return XQUERY_NAME; 
   }
}
