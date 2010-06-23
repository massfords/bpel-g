//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/graph/IAeXmlDefGraphNode.java,v 1.3 2008/02/17 21:09:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.graph;

import java.util.List;

import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Defines the interface to represent a graph node backed by a xml definition.
 */
public interface IAeXmlDefGraphNode
{
   /** 
    * Returns the definitions xml element name.  
    */
   public String getDisplayName();
   
   /**
    * Returns definition name.
    * @return definition name if available or <code>null</code> otherwise.
    */
   public String getName();
   
   /**
    * Returns the definition.
    * @return xml def.
    */
   public AeBaseXmlDef getDef();
         
   /**
    * Returns the icon image name.
    */
   public String getIcon();
   
   /**
    * Returns parent node if available or <code>null</code> if this is the root node.
    * @return parent node.
    */
   public IAeXmlDefGraphNode getParent();
   
   /**
    * Sets the parents.
    */
   public void setParent(IAeXmlDefGraphNode aParent);
   
   /**
    * Adds a child node.
    */
   public void addChild(IAeXmlDefGraphNode aChild);
   
       
   /**
    * Returns list of child <code>IAeXmlDefGraphNode</code> nodes.
    * @return list of child nodes.
    */
   public List getChildren();
   
   /**
    * Returns true if a node is meant to be displayed only on outline view
    */
   public boolean isDisplayOutlineOnly();
}
