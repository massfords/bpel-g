// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDefActivityInvokeVisitor.java,v 1.1 2007/12/27 15:28:57 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.visitors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;

/**
 * A def visitor that will find all invokes.
 */
public class AeDefActivityInvokeVisitor extends AeAbstractDefVisitor
{
   /** List of all the AeActivityInvokeDef objects found in the BPEL */
   private List mInvokes = new ArrayList(); 
   
   /**
    * Constructor
    */
   public AeDefActivityInvokeVisitor()
   {
      setTraversalVisitor( new AeTraversalVisitor( new AeDefTraverser(), this ) );
   }

   /**
    * 
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
    */
   public void visit(AeActivityInvokeDef aDef)
   {
      mInvokes.add(aDef);
      super.visit(aDef);
   }

   /**
    * Returns all invokes within a BPEL process
    * 
    * @return List of AeActivityInvokeDef objects
    */
   public List getInvokes()
   {
      return Collections.unmodifiableList(mInvokes);
   }
   
   /**
    * Returns all invokes within a BPEL process with a specified name
    * 
    * @param aName
    * @return List of AeActivityInvokeDef objects
    */
   public List getInvokes(String aName)
   {
      List retList = new ArrayList();
      
      for( Iterator iter = mInvokes.iterator(); iter.hasNext(); )
      {
         AeActivityInvokeDef def = (AeActivityInvokeDef) iter.next();
         if( def.getName().equals(aName) )
         {
            retList.add(def);
         }
         
      }
      
      return Collections.unmodifiableList(retList);
   }
   
}
