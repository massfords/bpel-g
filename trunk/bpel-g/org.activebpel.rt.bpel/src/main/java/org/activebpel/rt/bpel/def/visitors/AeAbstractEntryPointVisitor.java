// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeAbstractEntryPointVisitor.java,v 1.9 2008/01/21 21:43:01 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import java.util.Iterator;

import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.activity.AeActivityPickDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.IAeReceiveActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;

/**
 * Abstract visitor for visiting entry point def objects
 * in an AeProcessDef object.
 * <br />
 * Looks for receive activities and pick activities with
 * onMessage children.
 */
abstract public class AeAbstractEntryPointVisitor extends AeAbstractDefVisitor
{
   /** holds onto the process def in case subclasses need access */
   private AeProcessDef mProcessDef;
   
   /**
    * Constructor.
    */
   protected AeAbstractEntryPointVisitor()
   {
      setTraversalVisitor( new AeTraversalVisitor( new AeDefTraverser(), this ) );
   }
   
   /**
    * Getter for the process def
    */
   protected AeProcessDef getProcessDef()
   {
      return mProcessDef;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      mProcessDef = aDef;
      super.visit(aDef);
   }

   /**
    * If the accept method return true call processReceive.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef aDef)
   {
      if( accept( aDef ) )
      {
         processEntryPoint( aDef );
      }
      super.visit(aDef);
   }
   
   /**
    * Returns a boolean indicating whether or not the receive should
    * be processed
    * @param aDef the receive
    * @return true if this receive should be processed further
    */
   protected boolean accept( AeActivityReceiveDef aDef )
   {
      return aDef.isCreateInstance();
   }

   /**
    * If the pick is acceptable, iterate over its onMessage children
    * and call process for each one.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityPickDef)
    */
   public void visit(AeActivityPickDef aDef)
   {
      // NOTE: a pick with a createInstance attribute that 
      // evaluates to true must have at least one onMessage
      // create keys for each onMessage
      if( accept( aDef ) )
      {
         for( Iterator iter = aDef.getOnMessageDefs(); iter.hasNext(); )
         {
            processEntryPoint( (AeOnMessageDef)iter.next() );
         }
      }
      super.visit(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      if( accept( aDef ) )
      {
         processEntryPoint( aDef );
      }
      super.visit(aDef);
   }

   /**
    * Determine if this pick should be processed further.
    * @param aDef
    * @return true if this pick should be processed further
    */
   protected boolean accept( AeActivityPickDef aDef )
   {
      return aDef.isCreateInstance();
   }
   
   /**
    * Determine if this event should be processed further.
    * 
    * @param aDef
    * @return true if this event should be processed further
    */
   protected boolean accept (AeOnEventDef aDef)
   {
      return false;
   }

   /**
    * Subclasses will do something useful.
    * @param aDef an acceptable onMessage
    */
   abstract protected void processEntryPoint( IAeReceiveActivityDef aDef );
}
