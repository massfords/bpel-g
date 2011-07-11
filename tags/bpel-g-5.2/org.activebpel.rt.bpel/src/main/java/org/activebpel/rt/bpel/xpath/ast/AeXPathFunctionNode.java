package org.activebpel.rt.bpel.xpath.ast;

import javax.xml.namespace.QName;


/**
 * A simple node to represent a function call.
 */
public class AeXPathFunctionNode extends AeAbstractXPathQualifiedNode
{
   /**
    * Simple Constructor.
    *
    * @param aPrefix
    * @param aNamespace
    * @param aFunctionName
    */
   public AeXPathFunctionNode(String aPrefix, String aNamespace, String aFunctionName)
   {
      super(AeAbstractXPathNode.NODE_TYPE_FUNCTION, aPrefix, aNamespace, aFunctionName);
   }

   /**
    * Gets the function's QName.
    */
   public QName getFunctionQName()
   {
      return new QName(getNamespace(), getLocalName());
   }

   /**
    * @see org.activebpel.rt.bpel.xpath.ast.AeAbstractXPathNode#accept(org.activebpel.rt.bpel.xpath.ast.IAeXPathNodeVisitor)
    */
   public void accept(IAeXPathNodeVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
