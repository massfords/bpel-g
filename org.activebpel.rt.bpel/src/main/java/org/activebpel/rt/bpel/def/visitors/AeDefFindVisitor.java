//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDefFindVisitor.java,v 1.3 2008/02/29 18:20:46 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Visitor to find a definition given a specific path.
 */
public class AeDefFindVisitor extends AeAbstractDefVisitor
{
   /** the path to find. */
   private String mPath;
   
   /** The definition object which is not null if it has been found. */
   private AeBaseXmlDef mFoundDef = null;

   /** Visitor to find the definition for the passed path. */
   public AeDefFindVisitor(String aPath)
   {
      setPath(aPath);
      setTraversalVisitor( new AeTraversalVisitor( new AeDefTraverser(), this ) );
   }
   
   /**
    * Overrides method to see if the object is the path we are looking for and to stop traversal if
    * we have a mtach.  
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#traverse(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   protected void traverse(AeBaseXmlDef aDef)
   {
      if(getFoundDef() == null)
      {
         if(getPath().equals(aDef.getLocationPath()))
         {
            setFoundDef(aDef);
         }
         else
         {
            super.traverse(aDef);
         }
      }
   }

   /**
    * @return Returns the foundDef.
    */
   public AeBaseXmlDef getFoundDef()
   {
      return mFoundDef;
   }

   /**
    * @param aFoundDef The foundDef to set.
    */
   public void setFoundDef(AeBaseXmlDef aFoundDef)
   {
      mFoundDef = aFoundDef;
   }

   /**
    * @return Returns the path.
    */
   public String getPath()
   {
      return mPath;
   }

   /**
    * @param aPath The path to set.
    */
   public void setPath(String aPath)
   {
      mPath = aPath;
   }

}
