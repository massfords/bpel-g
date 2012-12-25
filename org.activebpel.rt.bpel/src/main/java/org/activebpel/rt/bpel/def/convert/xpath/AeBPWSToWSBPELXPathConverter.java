// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/convert/xpath/AeBPWSToWSBPELXPathConverter.java,v 1.4 2006/10/06 20:59:33 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.convert.xpath;

import java.util.Iterator;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathAST;
import org.activebpel.rt.bpel.xpath.ast.AeXPathAbsLocPathNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathRelativeLocPathNode;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.IAeMutableNamespaceContext;

/**
 * Class used to convert BPEL 1.1 compatible xpath expressions into BPEL 2.0 compatible 
 * expressions.
 */
public class AeBPWSToWSBPELXPathConverter
{
   /**
    * Default c'tor.
    */
   private AeBPWSToWSBPELXPathConverter()
   {
   }
   
   /**
    * Converts a BPEL 1.1 compatible xpath expression to a BPEL 2.0 compatible format.
    * 
    * @param aExpression
    * @param aNamespaceContext
    */
   public static String convertExpression(String aExpression, IAeMutableNamespaceContext aNamespaceContext)
   {
      if (AeUtil.isNullOrEmpty(aExpression))
         return aExpression;

      try
      {
         AeXPathAST ast = AeXPathAST.createXPathAST(aExpression, aNamespaceContext);
         AeAbstractXPathNode xpathNode = ast.getRootNode();
         AeBPWSToWSBPELXPathNodeVisitor visitor = new AeBPWSToWSBPELXPathNodeVisitor(xpathNode, aNamespaceContext);
         xpathNode.accept(visitor);
         return visitor.getRootNode().serialize();
      }
      catch (AeException ex)
      {
         ex.logError();
         return aExpression;
      }
   }
   
   /**
    * Converts a query from BPEL 1.1 syntax to BPEL 2.0 syntax (effectively drops the first step
    * in the absolute path).  If there is only one step, then this will return null.
    * 
    * @param aQuery
    * @param aNamespaceContext
    */
   public static String convertQuery(String aQuery, IAeMutableNamespaceContext aNamespaceContext)
   {
      if (AeUtil.isNullOrEmpty(aQuery))
         return aQuery;

      try
      {
         // First, do the normal expression conversion which will convert getVariableData() into $varName.
         String originalQuery = convertExpression(aQuery, aNamespaceContext);
         // Next, convert from an absolute path expression to a relative one.
         AeXPathAST ast = AeXPathAST.createXPathAST(originalQuery, aNamespaceContext);
         AeAbstractXPathNode rootNode = ast.getRootNode();
         if (rootNode instanceof AeXPathAbsLocPathNode)
         {
            AeXPathRelativeLocPathNode relLocNode = AeBPWSToWSBPELXPathConverter.convertToRelativeXPath((AeXPathAbsLocPathNode) rootNode);
            if (relLocNode.hasChildren())
               return relLocNode.serialize();
            else
               return null;
         }
         else
         {
            return originalQuery;
         }
      }
      catch (AeException ex)
      {
         // Eat the exception - won't get converted and will show an error in validation.
      }
      return aQuery;
   }
   
   /**
    * Convenience method for converting an absolute loc path node to a relative loc path node.  This
    * method effectively removes the first step in the absolute path.
    * 
    * @param aAbsLocPathNode
    */
   public static AeXPathRelativeLocPathNode convertToRelativeXPath(AeXPathAbsLocPathNode aAbsLocPathNode)
   {
      AeXPathRelativeLocPathNode relativePathExprNode = new AeXPathRelativeLocPathNode();
      // Skip the first child (abs loc path -> rel loc path)
      Iterator iter = aAbsLocPathNode.getChildren().iterator();
      if (iter.hasNext())
         iter.next();
      while (iter.hasNext())
      {
         AeAbstractXPathNode childNode = (AeAbstractXPathNode) iter.next();
         childNode.setParent(relativePathExprNode);
         relativePathExprNode.addChild(childNode);
      }
      return relativePathExprNode;
   }
}
