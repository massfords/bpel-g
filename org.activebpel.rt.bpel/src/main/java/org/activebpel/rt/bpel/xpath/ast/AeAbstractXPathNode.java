package org.activebpel.rt.bpel.xpath.ast;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.activebpel.rt.bpel.xpath.ast.visitors.AeXPathSerializeNodeVisitor;

/**
 * A simple 'node' inner class.
 */
public abstract class AeAbstractXPathNode
{
   /* Some statics for the node types. */
   public static final String NODE_TYPE_VARIABLE_REF = "VariableReference"; //$NON-NLS-1$
   public static final String NODE_TYPE_FUNCTION = "FunctionCall"; //$NON-NLS-1$
   public static final String NODE_TYPE_LITERAL = "Literal"; //$NON-NLS-1$
   public static final String NODE_TYPE_ABSOLUTE_LOCATION_PATH = "AbsoluteLocationPath"; //$NON-NLS-1$
   public static final String NODE_TYPE_ADDITIVE_EXPR = "AdditiveExpr"; //$NON-NLS-1$
   public static final String NODE_TYPE_ALL_NODE_STEP = "AllNodeStep"; //$NON-NLS-1$
   public static final String NODE_TYPE_AND_EXPR = "AndExpr"; //$NON-NLS-1$
   public static final String NODE_TYPE_EQUALITY_EXPR = "EqualityExpr"; //$NON-NLS-1$
   public static final String NODE_TYPE_COMMENT_NODE_STEP = "CommentNodeStep"; //$NON-NLS-1$
   public static final String NODE_TYPE_FILTER_EXPR = "FilterExpr"; //$NON-NLS-1$
   public static final String NODE_TYPE_OR_EXPR = "OrExpr"; //$NON-NLS-1$
   public static final String NODE_TYPE_NAME_STEP = "NameStep"; //$NON-NLS-1$
   public static final String NODE_TYPE_MULTIPLICATIVE_EXPR = "MultiplicativeExpr"; //$NON-NLS-1$
   public static final String NODE_TYPE_PATH_EXPR = "PathExpr"; //$NON-NLS-1$
   public static final String NODE_TYPE_PREDICATE = "Predicate"; //$NON-NLS-1$
   public static final String NODE_TYPE_PROCESSING_INSTRUCTION_NODE_STEP = "ProcessingInstructionNodeStep"; //$NON-NLS-1$
   public static final String NODE_TYPE_RELATIONAL_EXPR = "RelationalExpr"; //$NON-NLS-1$
   public static final String NODE_TYPE_RELATIVE_LOCATION_PATH = "RelativeLocationPath"; //$NON-NLS-1$
   public static final String NODE_TYPE_TEXT_NODE_STEP = "TextNodeStep"; //$NON-NLS-1$
   public static final String NODE_TYPE_UNARY_EXPR = "UnaryExpr"; //$NON-NLS-1$
   public static final String NODE_TYPE_UNION_EXPR = "UnionExpr"; //$NON-NLS-1$
   public static final String NODE_TYPE_XPATH = "XPath"; //$NON-NLS-1$

   /** The node's parent. */
   private AeAbstractXPathNode mParent;
   /** The node name. */
   private String mType;
   /** The node's children. */
   private List mChildren = new ArrayList();

   /**
    * Simple constructor.
    * 
    * @param aType
    */
   public AeAbstractXPathNode(String aType)
   {
      setType(aType);
   }

   /**
    * @return Returns the children.
    */
   public List getChildren()
   {
      return mChildren;
   }
   
   /**
    * Returns true if the node has children.
    */
   public boolean hasChildren()
   {
      return mChildren.size() > 0;
   }
   
   /**
    * Adds a child to the list of children.
    * 
    * @param aNode
    */
   public void addChild(AeAbstractXPathNode aNode)
   {
      getChildren().add(aNode);
   }
   
   /**
    * @param aChildren The children to set.
    */
   protected void setChildren(List aChildren)
   {
      mChildren = aChildren;
   }

   /**
    * @return Returns the type.
    */
   public String getType()
   {
      return mType;
   }

   /**
    * @param aType The type to set.
    */
   protected void setType(String aType)
   {
      mType = aType;
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return MessageFormat.format("Node Type: {0} \nNumChildren: {1}", new Object[] { getType(), new Integer(mChildren.size()) }); //$NON-NLS-1$
   }

   /**
    * @return Returns the parent.
    */
   public AeAbstractXPathNode getParent()
   {
      return mParent;
   }

   /**
    * @param aParent The parent to set.
    */
   public void setParent(AeAbstractXPathNode aParent)
   {
      mParent = aParent;
   }

   /**
    * Replaces the given old node with the given new node.  This method takes care of the
    * proper parenting of the new node.
    * 
    * @param aOldNode
    * @param aNewNode
    */
   public boolean replaceChild(AeAbstractXPathNode aOldNode, AeAbstractXPathNode aNewNode)
   {
      boolean replaced = false;
      for (ListIterator iter = getChildren().listIterator(); iter.hasNext(); )
      {
         AeAbstractXPathNode node = (AeAbstractXPathNode) iter.next();
         if (node == aOldNode)
         {
            iter.set(aNewNode);
            replaced = true;
         }
      }
      if (replaced)
      {
         aNewNode.setParent(this);
      }
      return replaced;
   }
   
   /**
    * Accept the visitor.
    * 
    * @param aVisitor
    */
   public abstract void accept(IAeXPathNodeVisitor aVisitor);

   /**
    * A version of normalize that throws out the current node if the current node only has 
    * a single child.  This gets called by boolean and operator nodes - of they do not 
    * have more than one child, then there is no point in keeping them around.
    */
   protected AeAbstractXPathNode normalizeOmitSelf()
   {
      if (getChildren().size() == 1)
      {
         AeAbstractXPathNode node = ((AeAbstractXPathNode) getChildren().get(0)).normalize();
         node.setParent(getParent());
         return node;
      }
      else
      {
         normalizeChildren();
         return this;
      }
   }

   /**
    * Default behavior for node normalization.  This implementation normalizes the node's 
    * children and leaves itself unchanged.
    */
   public AeAbstractXPathNode normalize()
   {
      normalizeChildren();
      return this;
   }
   
   /**
    * Simplifies this node's children.
    */
   protected void normalizeChildren()
   {
      List list = new ArrayList();
      for (Iterator iter = getChildren().iterator(); iter.hasNext(); )
      {
         AeAbstractXPathNode node = (AeAbstractXPathNode) iter.next();
         list.add(node.normalize());
      }
      setChildren(list);
   }
   
   /**
    * Serialize the XPath to a string.
    */
   public String serialize()
   {
      AeXPathSerializeNodeVisitor visitor = new AeXPathSerializeNodeVisitor();
      accept(visitor);
      return visitor.getSerializedString();
   }
}
