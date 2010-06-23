// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.exist/src/org/activebpel/rt/bpel/server/engine/storage/exist/AeExistConfig.java,v 1.2 2007/08/17 00:59:50 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.exist;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBConfig;

/**
 * This class encapsulates the Exist XQuery statements used by the Active BPEL Exist based persistence
 * layer. This class uses a SAX parser to first parse an xml file that contains the XQuery queries.
 */
public class AeExistConfig extends AeXMLDBConfig
{
   /**
    * Creates a Exist config object.
    */
   public AeExistConfig()
   {
      this(Collections.EMPTY_MAP);
   }

   /**
    * Creates a Exist config object with the given map of constant overrides.
    * 
    * @param aConstantOverrides
    */
   public AeExistConfig(Map aConstantOverrides)
   {
      super(aConstantOverrides);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.AeStorageConfig#getStatementConfigFilenames()
    */
   protected List getStatementConfigFilenames()
   {
      String fileName = "exist-queries.xml"; //$NON-NLS-1$

      List list = super.getStatementConfigFilenames();
      list.add(new AeFilenameClassTuple(fileName, AeExistConfig.class));
      
      return list;
   }
}
