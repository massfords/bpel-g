//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDefPartnerLinkTypeVisitor.java,v 1.5 2008/01/11 19:31:16 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.IAePartnerLinkType;

/**
 * Sets the IAePartnerLinkType reference on each of the partner link def objects. 
 */
public class AeDefPartnerLinkTypeVisitor extends AeAbstractDefVisitor
{
   /** wsdl provider used to find partner link type definition elements */
   private IAeContextWSDLProvider mProvider;
   
   /**
    * Creates the visitor with the default traverser 
    */
   public AeDefPartnerLinkTypeVisitor(IAeContextWSDLProvider aProvider)
   {
      setTraversalVisitor( new AeTraversalVisitor(new AeDefTraverser(), this));
      mProvider = aProvider;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinkDef)
    */
   public void visit(AePartnerLinkDef aDef)
   {
      AeBPELExtendedWSDLDef wsdlDef = AeWSDLDefHelper.getWSDLDefinitionForPLT(mProvider, aDef.getPartnerLinkTypeName());
      // Note: if the wsdlDef is null then the plink is unresolved. This is caught during static analysis.
      if (wsdlDef != null)
      {
         IAePartnerLinkType plType = wsdlDef.getPartnerLinkType(aDef.getPartnerLinkTypeName().getLocalPart());
         // Note: if the plType is null then the plink is unresolved. This is caught during static analysis.
         aDef.setPartnerLinkType(plType);
      }
      super.visit(aDef);
   }
}
 