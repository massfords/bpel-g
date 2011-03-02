// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/AeBaseDef.java,v 1.38.4.1 2008/04/21 16:09:42 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor;

/**
 * Base definition object for bpel definitions
 */
public abstract class AeBaseDef extends AeBaseXmlDef implements IAeBPELConstants
{
   /**
    * Default constructor
    */
   public AeBaseDef()
   {
      super();
   }

   /**
    * <p>Get this instance's parent def.</p>
    * <p>
    * N.B.: the AeDefAssignParentVisitor must be used before assuming that
    * parent-child def relationships are valid.  These relationships are NOT
    * updated automatically (e.g., during modification by the process design user,
    * etc.).
    * </p>
    * @return AeBaseDef if parent exists, null otherwise.
    */
   public AeBaseDef getParent()
   {
      return (AeBaseDef) getParentXmlDef();
   }

   /**
    * Accepts a def visitor to perform an operation on the model.
    * @param aVisitor
    */
   public abstract void accept(IAeDefVisitor aVisitor);

   /**
    * @see org.activebpel.rt.xml.def.AeBaseXmlDef#accept(IAeBaseXmlDefVisitor)
    */
   public void accept(IAeBaseXmlDefVisitor aVisitor)
   {
      // This instanceof check is necessary in order to handle visitors that 
      // reference the object via its AeBaseDefXml type. If the visitor is 
      // actually a domain specific visitor then it'll dispatch accordingly.
      // Otherwise it'll use the standard visit method.
      if (aVisitor instanceof IAeDefVisitor)
      {
         accept((IAeDefVisitor)aVisitor);
      }
      else
      {
         aVisitor.visit(this);
      }
   }
}
