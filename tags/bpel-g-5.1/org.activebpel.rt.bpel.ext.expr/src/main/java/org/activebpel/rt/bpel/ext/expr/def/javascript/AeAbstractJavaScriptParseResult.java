// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/javascript/AeAbstractJavaScriptParseResult.java,v 1.4 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.def.javascript;

import java.util.LinkedHashSet;
import java.util.Set;

import org.activebpel.rt.bpel.def.expr.AeAbstractExpressionParseResult;
import org.activebpel.rt.expr.def.AeScriptFuncDef;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ScriptOrFnNode;
import org.mozilla.javascript.Token;

/**
 * A base class for javascript parse results.
 */
public abstract class AeAbstractJavaScriptParseResult extends AeAbstractExpressionParseResult
{
   /** The root of the javascript parse tree. */
   private ScriptOrFnNode mRootNode;
   /** The cached list of functions. */
   private Set mFunctions;
   /** The cached list of variables. */
   private Set mVariables;

   /**
    * Constructor.
    * 
    * @param aExpression
    * @param aRootNode
    * @param aParserContext
    */
   public AeAbstractJavaScriptParseResult(String aExpression, ScriptOrFnNode aRootNode,
         IAeExpressionParserContext aParserContext)
   {
      super(aExpression, aParserContext);
      setRootNode(aRootNode);
   }

   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionParseResult#getFunctions()
    */
   public Set getFunctions()
   {
      if (mFunctions == null)
      {
         mFunctions = extractFunctions(getRootNode(), null);
      }

      return mFunctions;
   }
   
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionParseResult#getVariableReferences()
    */
   public Set getVariableReferences()
   {
      if (mVariables == null)
         mVariables = extractVariables(getRootNode());

      return mVariables;
   }

   
   /**
    * Extracts a function def object from the given JavaScript (Rhino) Node.  The Node is actually
    * a parse tree. This parse tree will be navigated and nodes of type CALL will be extracted into
    * AeScriptFuncDef objects.  A list of these objects will then be returned.
    * 
    * @param aNode
    * @param aParentFunc
    */
   protected Set extractFunctions(Node aNode, AeScriptFuncDef aParentFunc)
   {
      Set set = new LinkedHashSet();
      AeScriptFuncDef parentFunc = aParentFunc;

      // If the Node is a Function Call, extract it and add it to the list.
      if (aNode.getType() == Token.CALL)
      {
         AeScriptFuncDef funcDef = AeJavaScriptParseUtil.extractFunction(getParserContext().getNamespaceContext(), aNode);
         funcDef.setParent(parentFunc);
         AeJavaScriptParseUtil.extractArgsIntoFunction(aNode, funcDef);
         set.add(funcDef);
         parentFunc = funcDef;
      }

      // Now process all of the node's children.
      Node child = aNode.getFirstChild();
      while (child != null)
      {
         set.addAll(extractFunctions(child, parentFunc));
         child = child.getNext();
      }

      return set;
   }

   /**
    * Extracts a variable def object from the given JavaScript (Rhino) Node.  The Node is actually
    * a parse tree. This parse tree will be navigated and nodes of type VAR will be extracted into
    * AeScriptVarDef objects.  A set of these objects will then be returned.
    * 
    * @param aNode
    */
   protected Set extractVariables(Node aNode)
   {
      Set set = new LinkedHashSet();

      // Now process all of the node's children.
      Node child = aNode.getFirstChild();
      while (child != null)
      {
         set.addAll(extractVariables(child));
         child = child.getNext();
      }

      return set;
   }

   /**
    * @return Returns the rootNode.
    */
   public ScriptOrFnNode getRootNode()
   {
      return mRootNode;
   }

   /**
    * @param aRootNode The rootNode to set.
    */
   protected void setRootNode(ScriptOrFnNode aRootNode)
   {
      mRootNode = aRootNode;
   }
}
