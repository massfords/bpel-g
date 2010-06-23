//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/graph/IAeXmlDefGraphNodeAdapter.java,v 1.3 2008/02/13 01:45:55 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.graph;

import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.IAeAdapter;
import org.w3c.dom.Document;

/**
 * Extension adapter that is used by the process view graph and property
 * builder.
 */
public interface IAeXmlDefGraphNodeAdapter extends IAeAdapter
{
   /**
    * Returns icon image name or <code>null</code> if icon is not available.
    * @return icon image name.
    */
   public String getIcon();
   
   /**
    * Builds and returns a (sub)tree represented by this extension.
    * Return <code>null</code> if not supported.
    * @return subtree root node.
    */
   public IAeXmlDefGraphNode getTreeNode();
   
   /**
    * Returns list of properties.
    * @param aDef
    * @param aStateDoc
    */
   public IAeXmlDefGraphNodeProperty[] getProperties(AeBaseXmlDef aDef, Document aStateDoc, String aInstancePath);
}
