//$Header$
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def; 

/**
 * Interface for the &lt;partnerLinks&gt; def and &lt;partners&gt; def, both of which
 * can contain partner link references. 
 */
public interface IAePartnerLinkParentDef
{
   /**
    * Adds the partner link to the parent.
    * 
    * @param aPartnerLink
    */
   public void addPartnerLinkDef(AePartnerLinkDef aPartnerLink);
}
 
