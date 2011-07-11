// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDefOnEventVariableTypeVisitor.java,v 1.4 2008/01/11 19:31:16 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import java.util.Iterator;

import javax.wsdl.Message;
import javax.wsdl.Part;

import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;

/**
 * Assigns variable types to implicit variables created for <code>onEvent</code>
 * <code>fromPart</code> variable references by
 * {@link org.activebpel.rt.bpel.def.visitors.AeWSBPELImplicitVariableVisitor}.
 */
public class AeDefOnEventVariableTypeVisitor extends AeAbstractDefVisitor
{
   /** The WSDL provider set during visitor creation. */
   private final IAeContextWSDLProvider mWSDLProvider;

   /**
    * Constructs the visitor with the given WSDL provider.
    *
    * @param aWSDLProvider
    */
   public AeDefOnEventVariableTypeVisitor(IAeContextWSDLProvider aWSDLProvider)
   {
      mWSDLProvider = aWSDLProvider;

      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
   }

   /**
    * Returns the WSDL provider.
    */
   protected IAeContextWSDLProvider getWSDLProvider()
   {
      return mWSDLProvider;
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      if (aDef.getFromPartsDef() != null)
      {
         AeActivityScopeDef scopeDef = aDef.getChildScope();
         if (scopeDef != null)
         {
            Message message = AeWSDLDefHelper.getInputMessage(getWSDLProvider(), aDef.getPortType(), aDef.getOperation());
            if (message != null)
            {
               for (Iterator i = aDef.getFromPartDefs(); i.hasNext(); )
               {
                  AeFromPartDef fromPartDef = (AeFromPartDef) i.next();
                  String variableName = fromPartDef.getToVariable();
                  String partName = fromPartDef.getPart();

                  if ((variableName != null) && (partName != null))
                  {
                     AeVariableDef variableDef = scopeDef.getVariableDef(variableName);
                     Part part = message.getPart(partName);
   
                     if ((variableDef != null) && (part != null))
                     {
                        variableDef.setElement(part.getElementName());
                        variableDef.setType(part.getTypeName());
                     }
                  }
               }
            }
         }
      }

      super.visit(aDef);
   }
}
