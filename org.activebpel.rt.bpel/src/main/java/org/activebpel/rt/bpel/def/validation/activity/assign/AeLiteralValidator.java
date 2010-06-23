//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/assign/AeLiteralValidator.java,v 1.4 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.assign; 

import java.util.List;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.def.activity.support.AeLiteralDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.bpel.def.validation.IAeValidationDefs;
import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Node;

/**
 * model provides validation for the literal def
 */
public class AeLiteralValidator extends AeBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeLiteralValidator(AeLiteralDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Type aware getter.
    */
   protected AeLiteralDef getDef()
   {
      return (AeLiteralDef) getDefinition();
   }
   
   /**
    * Returns the literal Node for the copy variable.
    * @return Node the literal node
    */
   public Node getLiteral()
   {
      List nodes = getDef().getChildNodes();
      if (nodes.size() > 0)
      {
         return (Node) nodes.get(0);
      }

      return null;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      int numChildNodes = getDef().getChildNodes().size();
      
      // Def i/o layer guarantees that getLiteral() returns a Node - even if
      // that node is simply an empty TII - so no need to check for null.
      Node node = getLiteral();
      if (numChildNodes > 1 || (node.getNodeType() != Node.ELEMENT_NODE
            && node.getNodeType() != Node.TEXT_NODE && node.getNodeType() != Node.CDATA_SECTION_NODE))
      {
         getReporter().reportProblem(BPEL_INVALID_LITERAL_CODE,IAeValidationDefs.ERROR_INVALID_LITERAL, null, getDefinition());
      }

      try
      {
         // Make sure that there aren't any xsi:schemaLocation attributes 
         // declared in the literal.
         DOMXPath xpath = new DOMXPath("descendant-or-self::*[@xsi:schemaLocation]"); //$NON-NLS-1$
         xpath.setNamespaceContext(new NamespaceContext()
         {
            public String translateNamespacePrefixToUri(String aPrefix)
            {
               return IAeConstants.W3C_XML_SCHEMA_INSTANCE;
            }
         });
         List nodes = (List) xpath.evaluate(node);
         if (nodes.size() > 0)
         {
            getReporter().reportProblem(BPEL_SCHEMA_LOCATION_IN_LITERAL_CODE, IAeValidationDefs.WARNING_SCHEMA_LOCATION_IN_LITERAL, null, getDefinition());
         }
      }
      catch (JaxenException e)
      {
         AeException.logError(e);
      }
   }
}
