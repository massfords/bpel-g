package org.activebpel.rt.bpel.xpath.ast;

/**
 * A simple node to represent a literal.
 */
public class AeXPathLiteralNode extends AeAbstractXPathNode
{
   /** The value of the literal. */
   private Object mValue;

   /**
    * Simple constructor.
    * 
    * @param aLiteralValue
    */
   public AeXPathLiteralNode(Object aLiteralValue)
   {
      super(AeAbstractXPathNode.NODE_TYPE_LITERAL);

      setValue(aLiteralValue);
   }

   /**
    * @return Returns the value.
    */
   public Object getValue()
   {
      return mValue;
   }

   /**
    * @param aValue The value to set.
    */
   protected void setValue(Object aValue)
   {
      mValue = aValue;
   }
   
   /**
    * @see org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode#accept(org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor)
    */
   public void accept(IAeXPathNodeVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}