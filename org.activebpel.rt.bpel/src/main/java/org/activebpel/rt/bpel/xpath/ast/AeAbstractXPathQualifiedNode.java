// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/xpath/ast/AeAbstractXPathQualifiedNode.java,v 1.3 2006/09/07 15:06:26 EWittmann Exp $
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
 * A simple base class for XPath nodes that are potentially namespace qualified.
 */
public abstract class AeAbstractXPathQualifiedNode extends AeAbstractXPathNode implements IAeXPathQualifiedNode
{
   /** The prefix used to resolve the namespace. */
   private String mPrefix;
   /** The namespace. */
   private String mNamespace;
   /** The local name. */
   private String mLocalName;

   /**
    * Simple constructor.
    * 
    * @param aType
    * @param aPrefix
    * @param aNamespace
    * @param aLocalName
    */
   public AeAbstractXPathQualifiedNode(String aType, String aPrefix, String aNamespace, String aLocalName)
   {
      super(aType);
      setPrefix(aPrefix);
      setNamespace(aNamespace);
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
   public void setLocalName(String aLocalName)
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
   public void setNamespace(String aNamespace)
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
}
