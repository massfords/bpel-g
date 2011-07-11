// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeManagerVisitor.java,v 1.1 2004/08/25 22:28:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

/**
 * Visitor interface for managers.  
 */
public interface IAeManagerVisitor
{
   /** Visitor that calls the create method */
   public static final IAeManagerVisitor CREATE = new IAeManagerVisitor()
   {
      public void visit(IAeManager aManager) throws Exception
      {
         aManager.create(); 
      }
   };

   /** Visitor that calls the prepareToStart method */
   public static final IAeManagerVisitor PREPARE = new IAeManagerVisitor()
   {
      public void visit(IAeManager aManager) throws Exception
      {
         aManager.prepareToStart(); 
      }
   };

   /** Visitor that calls the start method */
   public static final IAeManagerVisitor START = new IAeManagerVisitor()
   {
      public void visit(IAeManager aManager) throws Exception
      {
         aManager.start(); 
      }
   };
   
   /** Visitor that calls the stop method */
   public static final IAeManagerVisitor STOP = new IAeManagerVisitor()
   {
      public void visit(IAeManager aManager) throws Exception
      {
         aManager.stop(); 
      }
   };
   
   /** Visitor that calls the destroy method */
   public static final IAeManagerVisitor DESTROY = new IAeManagerVisitor()
   {
      public void visit(IAeManager aManager) throws Exception
      {
         aManager.destroy(); 
      }
   };

   /**
    * Visitor method for managers.
    * @param aManager
    */
   public void visit(IAeManager aManager) throws Exception;
}
