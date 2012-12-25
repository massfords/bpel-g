// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/fastdom/AeFastAttribute.java,v 1.1 2004/09/07 22:08:24 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.fastdom;

/**
 * Implements an element attribute in the fast, lightweight DOM.
 */
public class AeFastAttribute extends AeFastNode
{
   /** The attribute's name. */
   private final String mName;

   /** The attribute's value. */
   private final String mValue;

   /**
    * Constructs an attribute with the specified name and value.
    *
    * @param aName
    * @param aValue
    */
   public AeFastAttribute(String aName, String aValue)
   {
      mName = aName;
      mValue = aValue;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitable#accept(org.activebpel.rt.bpel.impl.fastdom.IAeVisitor)
    */
   public void accept(IAeVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * Returns this attribute's name.
    */
   public String getName()
   {
      return mName;
   }

   /**
    * Returns this attribute's value.
    */
   public String getValue()
   {
      return mValue;
   }
}
