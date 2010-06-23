// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/addressing/pdef/IAePartnerAddressBook.java,v 1.2 2004/07/08 13:10:03 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.addressing.pdef;

import org.activebpel.rt.bpel.IAeEndpointReference;

import javax.xml.namespace.QName;

/**
 * Wraps all of the parnter definitions for a given principal.
 * NOTE: this can be an aggregate of multiple deployments as the
 * principal serves as the primary key.
 */
public interface IAePartnerAddressBook
{

   /**
    * The accessor for the principal.
    */
   public String getPrincipal();
   
   /**
    * Returns the IAeEndpointReference mapped to the given partner link type and role.
    * @param aPartnerLinkType
    * @param aRole
    */
   public IAeEndpointReference getEndpointReference( QName aPartnerLinkType, String aRole );

}
