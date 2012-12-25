package org.activebpel.rt.bpel.xpath.ast;

import javax.xml.namespace.QName;

/**
 * A node type that identifies as a variable reference.
 */
public class AeXPathVariableNode extends AeAbstractXPathQualifiedNode
{
   /**
    * Simple constructor.
    *
    * @param aPrefix
    * @param aNamespace
    * @param aVariableName
    */
   public AeXPathVariableNode(String aPrefix, String aNamespace, String aVariableName)
   {
      super(AeAbstractXPathNode.NODE_TYPE_VARIABLE_REF, aPrefix, aNamespace, aVariableName);
   }

   /**
    * Returns the variable's QName.
    */
   public QName getVariableQName()
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
