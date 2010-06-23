//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/graph/AeXmlDefGraphNode.java,v 1.3 2008/02/17 21:09:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Basic implementation of a xml def graph node.
 */
public class AeXmlDefGraphNode implements IAeXmlDefGraphNode
{
   /** Parent */
   private IAeXmlDefGraphNode mParent;
   /** List of children.**/
   private List mChildren;
   /** Display name. */
   private String mDisplayName;
   /** Icon image file name */
   private String mIconName;
   /** definition  */
   private AeBaseXmlDef mDef;
   /** Optional name */
   private String mName;
   /** Flag to display only on outline */
   private boolean mDisplayOutlineOnly;
   
   /**
    * Ctor.
    * @param aName def element name.
    * @param aDef element def
    * @param aDisplayName optional display name.
    * @param aIconName optional icon image name.
    * @param aDisplayOutlineOnly
    */
   public AeXmlDefGraphNode(String aName, AeBaseXmlDef aDef, String aDisplayName, String aIconName, boolean aDisplayOutlineOnly)
   {
      mName = aName;
      if (AeUtil.notNullOrEmpty(aDisplayName))
      {
         mDisplayName = aDisplayName;
      }
      else
      {
         aDisplayName = aName;
      }
      mDef = aDef;      
      mIconName = aIconName;
      mDisplayOutlineOnly = aDisplayOutlineOnly;
   }

   /**
    * C'tor
    * @param aName
    * @param aDef
    * @param aDisplayName
    */
   public AeXmlDefGraphNode(String aName, AeBaseXmlDef aDef, String aDisplayName)
   {
      mName = aName;
      if (AeUtil.notNullOrEmpty(aDisplayName))
      {
         mDisplayName = aDisplayName;
      }
      else
      {
         aDisplayName = aName;
      }
      mDef = aDef;      
   }
   
   /**
    * Adds a child node.
    * @param aChild
    */
   public void addChild(IAeXmlDefGraphNode aChild)
   {
      if (mChildren == null)
      {
         mChildren = new ArrayList();
      }
      if (aChild != null && !mChildren.contains(aChild) )
      {
         mChildren.add(aChild);
         aChild.setParent(this);
      }
   }

   /**
    * @see org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNode#getChildren()
    */
   public List getChildren()
   {
      if (mChildren != null)
      {
         return mChildren;
      }
      return Collections.EMPTY_LIST;
   }

   /**
    * @see org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNode#getDisplayName()
    */
   public String getDisplayName()
   {      
      return mDisplayName;
   }

   /**
    * @see org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNode#getIcon()
    */
   public String getIcon()
   {
      return mIconName;
   }

   /**
    * @see org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNode#getDef()
    */
   public AeBaseXmlDef getDef()
   {
      return mDef;
   }

   /**
    * @see org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNode#getName()
    */
   public String getName()
   {
      return mName;
   }

   /**
    * @see org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNode#getParent()
    */
   public IAeXmlDefGraphNode getParent()
   {
      return mParent;
   }

   /**
    * Sets the parent.
    * @param aParent
    */
   public void setParent(IAeXmlDefGraphNode aParent)
   {
      mParent = aParent;
   }

   /**
    * @see org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNode#isDisplayOutlineOnly()
    */
   public boolean isDisplayOutlineOnly()
   {
      return mDisplayOutlineOnly;
   }
}
