package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.def.activity.AeActivityForEachDef;
import org.activebpel.rt.xml.def.AeDocumentationDef;
import org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor;

/**
 * Customized traverser that skips over the child scope for parallel forEach
 * activities. These definitions will be visited during the activity's execution
 * or during its state restoration.
 */
public class AeImplementationTraverser extends AeDefTraverser
{
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityForEachDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      if (!aDef.isParallel())
      {
         super.traverse(aDef, aVisitor);
      }
      else
      {
         traverseSourceAndTargetLinks(aDef, aVisitor);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeDefTraverser#traverse(org.activebpel.rt.xml.def.AeDocumentationDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeDocumentationDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
   }
}