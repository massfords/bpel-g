// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/impl/xquery/AeXQueryExpressionResult.java,v 1.1 2006/09/07 15:11:45 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.impl.xquery;

import org.w3c.dom.Document;

/**
 * This class wraps the Document that is produced when an XQuery expression is executed using 
 * Saxon.  The result is an XML Document that contains a root node and a sequence of nodes that
 * may be either atomic values or elements.
 */
public class AeXQueryExpressionResult
{
   /** The Saxon result document. */
   private Document mDocument;

   /**
    * Constructs the result.
    * 
    * @param aDocument
    */
   public AeXQueryExpressionResult(Document aDocument)
   {
      setDocument(aDocument);
   }

   /**
    * @return Returns the document.
    */
   public Document getDocument()
   {
      return mDocument;
   }

   /**
    * @param aDocument The document to set.
    */
   protected void setDocument(Document aDocument)
   {
      mDocument = aDocument;
   }
}
