// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/addressing/pdef/IAePartnerAddressingAdmin.java,v 1.4 2005/06/13 17:54:07 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.addressing.pdef;


/**
 *  Administration interface into the partner definition addressing layer.
 */
public interface IAePartnerAddressingAdmin
{
   
   /**
    * Return an array of the principals deployed to the provider.
    */
   public String[] getPrincipals();
   
   /**
    * Return the partner def info for the given principal.
    * @param aPrincipal
    */
   public IAePartnerDefInfo getPartnerInfo( String aPrincipal );

}
