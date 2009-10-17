//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeWSBPELImplicitVariableVisitor.java,v 1.4 2008/01/15 18:18:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import java.util.Iterator;

import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.def.IAeExtensionObject;

/**
 * Adds the implicit variable declaration where needed in 2.0
 */
public class AeWSBPELImplicitVariableVisitor extends AeImplicitVariableVisitor
{
   /**
    * protected ctor to force usage of visitor factory 
    */
   protected AeWSBPELImplicitVariableVisitor()
   {
      super();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      AeActivityScopeDef scopeDef = aDef.getChildScope();
      if (scopeDef != null)
      {
         String varName = aDef.getVariable();
         if (AeUtil.notNullOrEmpty(varName))
         {
            AeVariableDef varDef = addVariableToScope(varName, scopeDef);
            if (varDef != null)
            {
               if (aDef.getElement() != null)
               {
                  varDef.setElement(aDef.getElement());
               }
               
               if (aDef.getMessageType() != null)
               {
                  varDef.setMessageType(aDef.getMessageType());
               }
            }
         }
         else if (aDef.getFromPartsDef() != null)
         {
            // Create untyped implicit variables for the fromPart variables. The
            // variable types will be added later by a separate visitor that has
            // access to a WSDL provider (see AeDefOnEventVariableTypeVisitor,
            // which is called by AeProcessDef's preProcessForExecution()).
            for (Iterator i = aDef.getFromPartDefs(); i.hasNext(); )
            {
               AeFromPartDef fromPartDef = (AeFromPartDef) i.next();
               varName = fromPartDef.getToVariable();
               addVariableToScope(varName, scopeDef);
            }
         }
      }
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef)
    */
   public void visit(AeChildExtensionActivityDef aDef)
   {
      IAeExtensionObject extObject = aDef.getExtensionObject();
      if (extObject != null)
      {
         IAeImplicitVariablesAdapter implicitVariablesAdapter = (IAeImplicitVariablesAdapter) extObject.getAdapter(IAeImplicitVariablesAdapter.class);
         if (implicitVariablesAdapter != null)
            implicitVariablesAdapter.createImplicitVariables(aDef);
      }
      
      super.visit(aDef);
   }
   
   
}
 