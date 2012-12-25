// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/AeWSBPELVariableValidator.java,v 1.2 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation;

import org.activebpel.rt.bpel.def.AeVariableDef;

/**
 * Extens base validtor to check that import exists for message type variables.
 */
public class AeWSBPELVariableValidator extends AeVariableValidator
{

   /**
    * Constructs validator for passed def.
    * @param aDef
    */
   public AeWSBPELVariableValidator(AeVariableDef aDef)
   {
      super(aDef);
   }

   /**
    * Extends method to check that the import exists for the namespace. 
    * @see org.activebpel.rt.bpel.def.validation.AeVariableValidator#validateMessageType()
    */
   protected void validateMessageType()
   {
      super.validateMessageType();
      
      // if the wsdl was found make sure it is in the imports
      if ( getDef().getMessageType() != null && getWsdlDef() != null )
      {
         // check that the namespace for the partnerlink type was imported if this is a WS-BPEL 2.0 process
         String namespaceURI = getDef().getMessageType().getNamespaceURI();
         if (getProcessDef().findImportDef(namespaceURI) == null)
         {
            String name = getDef().getMessageType().getLocalPart();
            getReporter().reportProblem(WSBPEL_VARIABLE_MISSING_IMPORT_CODE,
                                     WARNING_MISSING_IMPORT,
                                     new String[] { namespaceURI, name },
                                     getDef() );
         }
         
      }
   }

}
