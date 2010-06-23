//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeImplicitVariableVisitor.java,v 1.7 2006/09/22 19:52:37 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.AeVariablesDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.IAeVariablesParentDef;
import org.activebpel.rt.bpel.def.activity.AeActivityForEachDef;

/**
 * A visitor responsible for creating implicit variables on def objects. 
 */
public class AeImplicitVariableVisitor extends AeAbstractDefVisitor
{
   /**
    * No arg ctor
    */
   protected AeImplicitVariableVisitor()
   {
      setTraversalVisitor( new AeTraversalVisitor( new AeDefTraverser(), this ) );
   }
   
   /**
    * Adds an implicit variable to the scope.
    * 
    * @param aVarName
    * @param aVariablesParentDef
    * @return AeVariableDef or null if there was already a variable with that name on the scope
    */
   protected AeVariableDef addVariableToScope(String aVarName, IAeVariablesParentDef aVariablesParentDef)
   {
      // get or create the variables def
      AeVariablesDef variablesDef = aVariablesParentDef.getVariablesDef();
      if (variablesDef == null)
      {
         variablesDef = new AeVariablesDef();
         aVariablesParentDef.setVariablesDef(variablesDef);
      }
      
      // check to see if there's already an implicit variable present
      // if we're coming from bpep, then it's possible that we'll have already
      // created the defs that we need.
      AeVariableDef varDef = variablesDef.getVariableDef(aVarName);
      if (varDef != null && varDef.isImplicit())
      {
         return null;
      }
      
      varDef = new AeVariableDef();
      varDef.setImplicit(true);
      varDef.setName(aVarName);
      variablesDef.addVariableDef(varDef);
      return varDef;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef)
    */
   public void visit(AeActivityForEachDef aDef)
   {
      if(aDef.getChildScope() != null)
      {
         String varName = aDef.getCounterName();
         AeVariableDef varDef = addVariableToScope(varName, aDef.getChildScope().getScopeDef());
         if (varDef != null)
            varDef.setType(new QName(IAeBPELConstants.W3C_XML_SCHEMA, "unsignedInt")); //$NON-NLS-1$
      }
      
      super.visit(aDef);
   }
}
 