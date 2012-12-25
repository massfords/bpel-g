//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/adapter/IAeCorePreprocessingAdapter.java,v 1.2 2007/11/15 02:24:40 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.adapter;

import org.activebpel.rt.AeException;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.IAeAdapter;

/**
 * All extension implementations that participate in core preprocessing need to implement this interface
 */
public interface IAeCorePreprocessingAdapter extends IAeAdapter
{
   /**
    * Execute the adapter logic. The context passed is the Def object
    * @param aDef
    */
   public void preprocessForCore(AeBaseXmlDef aDef) throws AeException;
}
