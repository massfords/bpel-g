//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AePartnerLinksValidator.java,v 1.1 2006/08/16 22:07:27 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope; 

import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.def.AePartnerLinksDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * Model provides validation for the partnerlinks model
 */
public class AePartnerLinksValidator extends AeBaseValidator
{

   /**
    * ctor
    * @param aDef
    */
   public AePartnerLinksValidator(AePartnerLinksDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Gets the plink model by name
    * @param aName
    */
   public AePartnerLinkValidator getPartnerLinkModel(String aName)
   {
      List plinks = getChildren(AePartnerLinkValidator.class);
      for (Iterator iter = plinks.iterator(); iter.hasNext();)
      {
         AePartnerLinkValidator plinkModel = (AePartnerLinkValidator) iter.next();
         if (plinkModel.getName().equals(aName))
            return plinkModel;
      }
      return null;
   }
}
 