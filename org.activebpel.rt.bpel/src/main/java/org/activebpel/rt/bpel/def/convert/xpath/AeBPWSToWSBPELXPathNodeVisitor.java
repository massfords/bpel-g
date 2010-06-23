// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/convert/xpath/AeBPWSToWSBPELXPathNodeVisitor.java,v 1.4 2006/09/27 19:58:42 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.convert.xpath;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathAST;
import org.activebpel.rt.bpel.xpath.ast.AeXPathAbsLocPathNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathFunctionNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathLiteralNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathPathExprNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathRelativeLocPathNode;
import org.activebpel.rt.bpel.xpath.ast.AeXPathVariableNode;
import org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractTraversingXPathNodeVisitor;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.IAeMutableNamespaceContext;

/**
 * This visitor walks the XPath AST and makes changes to it in order to convert it from a 
 * BPEL 1.1 compatible expression to a BPEL 2.0 compatible expression.
 */
public class AeBPWSToWSBPELXPathNodeVisitor extends AeAbstractTraversingXPathNodeVisitor
{
   /** The namespace context. */
   private IAeMutableNamespaceContext mNamespaceContext;
   /** The root xpath node. */
   private AeAbstractXPathNode mRootNode;

   /**
    * Default c'tor.
    */
   public AeBPWSToWSBPELXPathNodeVisitor(AeAbstractXPathNode aRootNode, IAeMutableNamespaceContext aNamespaceContext)
   {
      super();
      setNamespaceContext(aNamespaceContext);
      setRootNode(aRootNode);
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.visitors.AeAbstractXPathNodeVisitor#visit(org.activebpel.rt.bpel.xpath.ast.AeXPathFunctionNode)
    */
   public void visit(AeXPathFunctionNode aNode)
   {
      if (IAeBPELConstants.BPWS_NAMESPACE_URI.equals(aNode.getNamespace()))
      {
         if ("getVariableData".equals(aNode.getLocalName())) //$NON-NLS-1$
         {
            convertGetVariableDataFunction(aNode);
         }
         else if ("getLinkStatus".equals(aNode.getLocalName())) //$NON-NLS-1$
         {
            convertGetLinkStatusFunction(aNode);
         }
         else
         {
            String prefix = getNamespaceContext().getOrCreatePrefixForNamespace(
                  IAeBPELConstants.WSBPEL_2_0_PREFIX, IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI);
            aNode.setPrefix(prefix);
            aNode.setNamespace(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI);
         }
      }

      super.visit(aNode);
   }

   /**
    * Converts a getVariableData function node to a $varName node.
    * 
    * @param aFunctionNode
    */
   protected void convertGetVariableDataFunction(AeXPathFunctionNode aFunctionNode)
   {
      // For now, only handle the 1 and 3 arg forms of getVariableData - in order to handle the 
      // 2 arg form of getVariableData we need to be able to resolve the variable.
      if (aFunctionNode.getChildren().size() == 1)
      {
         convertGetVariableDataFunctionOneArg(aFunctionNode);
      }
      else if (aFunctionNode.getChildren().size() == 2)
      {
         convertGetVariableDataFunctionTwoArgs(aFunctionNode);
      }
      else if (aFunctionNode.getChildren().size() == 3)
      {
         convertGetVariableDataFunctionThreeArgs(aFunctionNode);
      }
   }

   /**
    * Converts the two argument form of getVariableData to a BPEL 2.0 XPath construct.  The
    * BPEL 2.0 XPath construct is either a simple VariableReference (varName + partName) or
    * it is a PathExpr with two children:  a VariableReference and a RelativePathExpr (varName +
    * query).
    * 
    * @param aFunctionNode
    */
   protected void convertGetVariableDataFunctionTwoArgs(AeXPathFunctionNode aFunctionNode)
   {
      AeAbstractXPathNode varNameNode = (AeAbstractXPathNode) aFunctionNode.getChildren().get(0);
      AeAbstractXPathNode partOrQueryNode = (AeAbstractXPathNode) aFunctionNode.getChildren().get(1);
      // All children must be literals for this to work.
      if (varNameNode instanceof AeXPathLiteralNode && partOrQueryNode instanceof AeXPathLiteralNode)
      {
         String varName = (String) ((AeXPathLiteralNode) varNameNode).getValue();
         String partNameOrQuery = (String) ((AeXPathLiteralNode) partOrQueryNode).getValue();
         if (isPartName(partNameOrQuery))
         {
            String partName = partNameOrQuery;
            AeXPathVariableNode newNode = new AeXPathVariableNode(null, null, varName + "." + partName); //$NON-NLS-1$
            replaceNode(aFunctionNode, newNode);
         }
         else
         {
            String query = partNameOrQuery;

            // Create the PathExpr node.
            AeAbstractXPathNode newNode = createPathExpr(varName, null, query);
            if (newNode != null)
            {
               replaceNode(aFunctionNode, newNode);
            }
         }
      }
   }

   /**
    * Converts the three argument form of getVariableData to a BPEL 2.0 XPath construct.  The
    * BPEL 2.0 XPath construct is a PathExpr with two children:  a VariableReference and a
    * RelativePathExpr.
    * 
    * @param aFunctionNode
    */
   protected void convertGetVariableDataFunctionThreeArgs(AeXPathFunctionNode aFunctionNode)
   {
      AeAbstractXPathNode varNameNode = (AeAbstractXPathNode) aFunctionNode.getChildren().get(0);
      AeAbstractXPathNode partNameNode = (AeAbstractXPathNode) aFunctionNode.getChildren().get(1);
      AeAbstractXPathNode queryNode = (AeAbstractXPathNode) aFunctionNode.getChildren().get(2);
      // All children must be literals for this to work.
      if (varNameNode instanceof AeXPathLiteralNode && partNameNode instanceof AeXPathLiteralNode
            && queryNode instanceof AeXPathLiteralNode)
      {
         String varName = (String) ((AeXPathLiteralNode) varNameNode).getValue();
         String partName = (String) ((AeXPathLiteralNode) partNameNode).getValue();
         String query = (String) ((AeXPathLiteralNode) queryNode).getValue();

         // Create the PathExpr node.
         AeAbstractXPathNode newNode = createPathExpr(varName, partName, query);
         if (newNode != null)
         {
            replaceNode(aFunctionNode, newNode);
         }
      }
   }

   /**
    * Converts the single argument form of getVariableData to a BPEL 2.0 XPath construct.  The
    * BPEL 2.0 XPath construct is a single Variable Reference node.
    * 
    * @param aFunctionNode
    */
   protected void convertGetVariableDataFunctionOneArg(AeXPathFunctionNode aFunctionNode)
   {
      AeAbstractXPathNode node = (AeAbstractXPathNode) aFunctionNode.getChildren().get(0);
      // Must be a literal child for this to work.
      if (node instanceof AeXPathLiteralNode)
      {
         String varName = (String) ((AeXPathLiteralNode) node).getValue();
         AeXPathVariableNode newNode = new AeXPathVariableNode(null, null, varName);
         replaceNode(aFunctionNode, newNode);
      }
   }
   
   /**
    * Given a String found in argument 3 (or ae-argument 2) of bpws:getVariableData, this
    * method will return an AeAbstractXPathNode.  If the query evaluated to an absolute
    * location path, then it will be transformed into a BPEL 2.0 compliant relative path
    * prior to being returned.  Otherwise it is returned as-is (e.g. the query evaluated to
    * a function call).
    * 
    * @param aQuery
    */
   protected AeAbstractXPathNode createGetVarDataQueryNode(String aQuery)
   {
      try
      {
         AeXPathAST queryAST = AeXPathAST.createXPathAST(aQuery, getNamespaceContext());
         AeAbstractXPathNode rootNode = queryAST.getRootNode();
         if (rootNode instanceof AeXPathAbsLocPathNode)
         {
            return AeBPWSToWSBPELXPathConverter.convertToRelativeXPath((AeXPathAbsLocPathNode) rootNode);
         }
         else if (rootNode instanceof AeXPathFunctionNode)
         {
            return rootNode;
         }
      }
      catch (AeException ex)
      {
         // Eat the exception - won't get converted and will show an error in validation.
      }
      return null;
   }

   /**
    * Converts the getLinkStatus() function call to a BPEL 2.0 XPath construct.  The
    * BPEL 2.0 XPath construct is a single Variable Reference node.
    * 
    * @param aFunctionNode
    */
   protected void convertGetLinkStatusFunction(AeXPathFunctionNode aFunctionNode)
   {
      AeAbstractXPathNode node = (AeAbstractXPathNode) aFunctionNode.getChildren().get(0);
      // Must be a literal child for this to work.
      if (node instanceof AeXPathLiteralNode)
      {
         String varName = (String) ((AeXPathLiteralNode) node).getValue();
         AeXPathVariableNode newNode = new AeXPathVariableNode(null, null, varName);
         replaceNode(aFunctionNode, newNode);
      }
   }

   /**
    * This method is called to create a xpath node node given a variable name, part name, and 
    * query.  The result depends on the value of the query.  This method returns null if it did
    * not understand the query param.
    * 
    * @param aVariableName
    * @param aPartName
    * @param aQuery
    */
   protected AeAbstractXPathNode createPathExpr(String aVariableName, String aPartName, String aQuery)
   {
      String varName = aVariableName;
      if (AeUtil.notNullOrEmpty(aPartName))
      {
         varName = varName + "." + aPartName; //$NON-NLS-1$
      }

      AeXPathPathExprNode pathExprNode = new AeXPathPathExprNode();
      
      // Now get/create the children (VarRef + RelativePathExpr)
      //  First the variable ref
      AeXPathVariableNode variableRefNode = new AeXPathVariableNode(null, null, varName);
      variableRefNode.setParent(pathExprNode);
      pathExprNode.addChild(variableRefNode);
      // Now the query RelativePathExpr
      AeAbstractXPathNode queryNode = createGetVarDataQueryNode(aQuery);
      // Handle the simple case:  getVariableData('var', 'part', '/part/ns1:data')
      // Should transform to: $var.part/ns1:data
      if (queryNode instanceof AeXPathRelativeLocPathNode)
      {
         if (((AeXPathRelativeLocPathNode) queryNode).hasChildren())
         {
            queryNode.setParent(pathExprNode);
            pathExprNode.addChild(queryNode);
         }
         return pathExprNode;
      }
      // Handle the case:  getVariableData('var', 'part', 'count(/part/ns1:data)')
      // Should transform to:  count($var.part/ns1:data)
      else if (queryNode instanceof AeXPathFunctionNode && queryNode.getChildren().size() == 1)
      {
         AeXPathFunctionNode funcNode = (AeXPathFunctionNode) queryNode;
         if (queryNode.getChildren().get(0) instanceof AeXPathAbsLocPathNode)
         {
            // 1) create relative path expr
            // 2) relative path expr becomes child of "pathExprNode"
            // 3) function gets a new child - "pathExprNode"
            // 4) function gets returned
            AeXPathAbsLocPathNode absLocPath = (AeXPathAbsLocPathNode) queryNode.getChildren().get(0);
            AeXPathRelativeLocPathNode relLocPath = AeBPWSToWSBPELXPathConverter.convertToRelativeXPath(absLocPath);
            if (relLocPath != null)
            {
               relLocPath.setParent(pathExprNode);
               pathExprNode.addChild(relLocPath);
            }
            funcNode.replaceChild(absLocPath, pathExprNode);
            return funcNode;
         }
      }
      return null;
   }

   /**
    * Returns true if the given String indicates the name of a part (rather than an XPath query).
    * 
    * @param aPartNameOrQuery
    */
   protected boolean isPartName(String aPartNameOrQuery)
   {
      // TODO (EPW) this should be smarter (lookup the variable to determine if it's a message or an element)
      return AeXmlUtil.isValidNCName(aPartNameOrQuery);
   }

   /**
    * Replaces the given old node with some newly constructed node.
    * 
    * @param aOldNode
    * @param aNewNode
    */
   protected void replaceNode(AeAbstractXPathNode aOldNode, AeAbstractXPathNode aNewNode)
   {
      if (aOldNode.getParent() != null)
      {
         aOldNode.getParent().replaceChild(aOldNode, aNewNode);
      }
      else
      {
         setRootNode(aNewNode);
      }
   }

   /**
    * @return Returns the namespaceContext.
    */
   protected IAeMutableNamespaceContext getNamespaceContext()
   {
      return mNamespaceContext;
   }

   /**
    * @param aNamespaceContext The namespaceContext to set.
    */
   protected void setNamespaceContext(IAeMutableNamespaceContext aNamespaceContext)
   {
      mNamespaceContext = aNamespaceContext;
   }

   /**
    * @return Returns the rootNode.
    */
   public AeAbstractXPathNode getRootNode()
   {
      return mRootNode;
   }

   /**
    * @param aRootNode The rootNode to set.
    */
   protected void setRootNode(AeAbstractXPathNode aRootNode)
   {
      mRootNode = aRootNode;
   }
}
