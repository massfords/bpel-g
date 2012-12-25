// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/fastdom/AeFastText.java,v 1.2 2004/09/23 16:37:43 KRoe Exp $
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
 * Implements a text node in the fast, lightweight DOM.
 */
public class AeFastText extends AeFastNode
{
   /** The text value. */
   private final String mValue;

   /**
    * Constructs a text node with the specified value.
    *
    * @param aValue
    */
   public AeFastText(String aValue)
   {
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
    * Returns this text node's value.
    */
   public String getValue()
   {
      return mValue;
   }
}
