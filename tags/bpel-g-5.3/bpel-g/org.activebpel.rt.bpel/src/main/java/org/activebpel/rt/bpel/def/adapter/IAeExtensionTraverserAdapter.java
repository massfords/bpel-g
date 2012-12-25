//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/adapter/IAeExtensionTraverserAdapter.java,v 1.2 2008/02/17 21:37:11 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.adapter;

import org.activebpel.rt.bpel.def.visitors.IAeDefTraverser;
import org.activebpel.rt.xml.def.IAeAdapter;

/**
 * Child Extension Activity Traversers adapters implement this interface
 */
public interface IAeExtensionTraverserAdapter extends IAeAdapter
{

   /**
    * Create and return a custom traverser
    */
   public IAeDefTraverser createTraverser();
}
