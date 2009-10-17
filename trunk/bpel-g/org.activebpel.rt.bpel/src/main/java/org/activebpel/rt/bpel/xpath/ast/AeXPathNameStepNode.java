// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/xpath/ast/AeXPathNameStepNode.java,v 1.1 2006/07/21 16:03:32 ewittmann Exp $
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.xpath.ast;

/**
 * An XPath node for name step.
 */
public class AeXPathNameStepNode extends AeAbstractXPathAxisNode implements IAeXPathQualifiedNode
{
   /** The prefix used by the node step. */
   private String mPrefix;
   /** The namespace. */
   private String mNamespace;
   /** The local name. */
   private String mLocalName;

   /**
    * Default c'tor.
    */
   public AeXPathNameStepNode(int aAxis, String aPrefix, String aNamespace, String aLocalName)
   {
      super(AeAbstractXPathNode.NODE_TYPE_NAME_STEP, aAxis);
      
      setNamespace(aNamespace);
      setPrefix(aPrefix);
      setLocalName(aLocalName);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathQualifiedNode#getLocalName()
    */
   public String getLocalName()
   {
      return mLocalName;
   }

   /**
    * @param aLocalName The localName to set.
    */
   protected void setLocalName(String aLocalName)
   {
      mLocalName = aLocalName;
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathQualifiedNode#getNamespace()
    */
   public String getNamespace()
   {
      return mNamespace;
   }

   /**
    * @param aNamespace The namespace to set.
    */
   protected void setNamespace(String aNamespace)
   {
      mNamespace = aNamespace;
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.IAeXPathQualifiedNode#getPrefix()
    */
   public String getPrefix()
   {
      return mPrefix;
   }

   /**
    * @param aPrefix The prefix to set.
    */
   public void setPrefix(String aPrefix)
   {
      mPrefix = aPrefix;
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode#accept(org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor)
    */
   public void accept(IAeXPathNodeVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
