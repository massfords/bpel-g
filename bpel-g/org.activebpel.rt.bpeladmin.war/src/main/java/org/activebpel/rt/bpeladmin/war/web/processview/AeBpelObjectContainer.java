//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeBpelObjectContainer.java,v 1.8 2008/02/17 21:43:07 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//                   PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Composite pattern container representing a BPEL definition element which may have children.
 * For example, the Process, Sequence, While contain other activities.
 */
public class AeBpelObjectContainer extends AeBpelObjectBase
{
   /** BPEL child model objects contained by this parent. */
   private List mChildren = new ArrayList();

   /**
    *
    * @param aTagName definition tag name
    * @param aDef definition
    */
   public AeBpelObjectContainer(String aTagName, AeBaseXmlDef aDef)
   {
      super(aTagName, aDef);
   }

   /**
    * Constructs a container with given tag, icon and underlying def.
    * @param aTagName
    * @param aIconName
    * @param aDef
    */
   public AeBpelObjectContainer(String aTagName, String aIconName, AeBaseXmlDef aDef)
   {
      super(aTagName, aIconName, aDef);
   }

   /**
    * Constructs a container with given tag, icon and underlying def.
    * @param aTagName
    * @param aIconName
    * @param aDef
    * @param aDisplayOutlineOnly
    */
   public AeBpelObjectContainer(String aTagName, String aIconName, AeBaseXmlDef aDef, boolean aDisplayOutlineOnly)
   {
      super(aTagName, aIconName, aDef, "", "", aDisplayOutlineOnly); //$NON-NLS-1$ //$NON-NLS-2$
   }

   
   /**
    * Adds the given object as a child of this parent.
    * @param aChild BPEL child object.
    */
   public void addChild(AeBpelObjectBase aChild)
   {
      if (!mChildren.contains(aChild))
      {
         mChildren.add(aChild);
         aChild.setParent(this);
      }
   }

   /**
    * Removes the child from the container.
    * @param aChild
    */
   public void removeChild(AeBpelObjectBase aChild)
   {
      if (mChildren.contains(aChild))
      {
         mChildren.remove(aChild);
         aChild.setParent(null);
      }
   }

   /**
    * @return List of children in this container.
    */
   public List getChildren()
   {
      return mChildren;
   }

   /**
    * Returns the number of children in the container.
    */
   public int size()
   {
      return getChildren().size();
   }

   /**
    * @return List of children in this container for the given type.
    */
   public List getChildren(String aBpelTagName)
   {
      List children =  getChildren();
      List rList = new ArrayList();
      Iterator iterator = children.iterator();
      while (iterator.hasNext())
      {
         AeBpelObjectBase child = (AeBpelObjectBase)iterator.next();
         if (child.getTagName().equalsIgnoreCase(aBpelTagName))
         {
            rList.add(child);
         }
      }
      return rList;
   }
}
