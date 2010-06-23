// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/fastdom/IAeVisitor.java,v 1.1 2004/09/07 22:08:24 KRoe Exp $
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
 * Defines the interface for objects that can visit nodes in the fast,
 * lightweight DOM.
 */
public interface IAeVisitor
{
   /**
    * Visits the specified attribute.
    *
    * @param aAttribute
    */
   public void visit(AeFastAttribute aAttribute);

   /**
    * Visits the specified document.
    *
    * @param aDocument
    */
   public void visit(AeFastDocument aDocument);

   /**
    * Visits the specified element.
    *
    * @param aElement
    */
   public void visit(AeFastElement aElement);

   /**
    * Visits the specified text node.
    *
    * @param aText
    */
   public void visit(AeFastText aText);

   /**
    * Visits the specified foreign node reference
    *
    * @param aForeignNode
    */
   public void visit(AeForeignNode aForeignNode);
}
