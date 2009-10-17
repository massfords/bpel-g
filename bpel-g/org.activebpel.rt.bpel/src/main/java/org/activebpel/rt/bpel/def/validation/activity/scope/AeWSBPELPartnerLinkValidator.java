// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AeWSBPELPartnerLinkValidator.java,v 1.2 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope;

import org.activebpel.rt.bpel.def.AePartnerLinkDef;

/**
 * Extends base class to check that import exists for the PartnerLinkType namespace. 
 */
public class AeWSBPELPartnerLinkValidator extends AePartnerLinkValidator
{
   /**
    * Constructs a validator for the passed def.
    * @param aDef
    */
   public AeWSBPELPartnerLinkValidator(AePartnerLinkDef aDef)
   {
      super(aDef);
   }

   /**
    * Extends method to check that the import exists for the partnerlinktype. 
    * @see org.activebpel.rt.bpel.def.validation.activity.scope.AePartnerLinkValidator#validate()
    */
   public void validate()
   {
      super.validate();

      // if we found the PartnerLinkType definition make sure it is a directly imported namespace
      if(getPartnerLinkType() != null)
      {
         // check that the namespace for the partnerlink type was imported if this is a WS-BPEL 2.0 process
         String namespaceURI = getDef().getPartnerLinkTypeName().getNamespaceURI();
         if (getProcessDef().findImportDef(namespaceURI) == null)
         {
            String name = getDef().getPartnerLinkTypeName().getLocalPart();
            getReporter().reportProblem(WSBPEL_PARTNERLINK_MISSING_IMPORT_CODE,
                                       WARNING_MISSING_IMPORT,
                                       new String[] { namespaceURI, name },
                                       getDef() );
         }
      }
   }
}
