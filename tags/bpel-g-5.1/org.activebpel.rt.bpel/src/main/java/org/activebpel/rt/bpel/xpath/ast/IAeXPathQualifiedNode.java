// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/xpath/ast/IAeXPathQualifiedNode.java,v 1.1 2006/07/21 16:03:31 ewittmann Exp $
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
 * Interface implemented by xpath nodes that are qualified.
 */
public interface IAeXPathQualifiedNode
{
   /**
    * @return Returns the localName.
    */
   public String getLocalName();
   /**
    * @return Returns the namespace.
    */
   public String getNamespace();

   /**
    * @return Returns the prefix.
    */
   public String getPrefix();
}
