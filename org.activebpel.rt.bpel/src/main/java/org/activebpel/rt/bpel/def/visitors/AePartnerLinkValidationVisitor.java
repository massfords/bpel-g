//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AePartnerLinkValidationVisitor.java,v 1.2 2007/09/12 02:48:11 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;

/**
 * Visitor used to determine if a partner link name is being used in an invoke.
 */
public class AePartnerLinkValidationVisitor extends AeAbstractSearchVisitor
{
   /** The partner link name being validated */
   private String mPartnerLinkName;
   /** Flag indicating if invoke reference was found */
   private boolean mReferenceFound;

   /**
    * Constructor.
    * @param aPartnerLinkName The partner link name we are validating
    */
   public AePartnerLinkValidationVisitor(String aPartnerLinkName)
   {
      mPartnerLinkName = aPartnerLinkName;

      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));      
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
    */
   public void visit(AeActivityInvokeDef aDef)
   {
      // fixme (MF) seems like this should be comparing location paths, not just the plink name. This isn't safe for BPEL 2.0 since plinks can be defined within scopes. 
      if (aDef.getPartnerLinkDef() != null && mPartnerLinkName.equals(aDef.getPartnerLinkDef().getName()))
         mReferenceFound = true;
      
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractSearchVisitor#isFound()
    */
   public boolean isFound()
   {
      return mReferenceFound;
   }
}