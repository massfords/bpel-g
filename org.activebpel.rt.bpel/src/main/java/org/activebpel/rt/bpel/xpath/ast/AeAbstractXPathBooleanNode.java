// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/xpath/ast/AeAbstractXPathBooleanNode.java,v 1.1 2006/07/21 16:03:31 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.xpath.ast;

/**
 * A base class that boolean xpath nodes extend (or, and, union, unary).
 */
public abstract class AeAbstractXPathBooleanNode extends AeAbstractXPathNode
{
   /** The create flag. */
   private boolean mCreate;

   /**
    * Constructor.
    * 
    * @param aType
    */
   public AeAbstractXPathBooleanNode(String aType)
   {
      super(aType);
   }

   /**
    * @return Returns the create.
    */
   public boolean isCreate()
   {
      return mCreate;
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode#normalize()
    */
   public AeAbstractXPathNode normalize()
   {
      return normalizeOmitSelf();
   }

   /**
    * @param aCreate The create to set.
    */
   public void setCreate(boolean aCreate)
   {
      mCreate = aCreate;
   }
}
