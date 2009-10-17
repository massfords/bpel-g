// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/addressing/pdef/IAePartnerAddressingProvider.java,v 1.3 2004/10/05 23:00:41 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.addressing.pdef;

import org.activebpel.rt.bpel.server.deploy.IAeDeploymentId;

/**
 * Interface for accessing, modifying the partner address definition layer.
 */
public interface IAePartnerAddressingProvider extends IAePartnerAddressingAdmin
{
   /**
    * Merges the partner def information with any existing information.
    * @param aDeployment used to identify the deployment and maintain a reference count
    * @param aDeploymentLocation Url string for the deployment location.
    * @param aInfo The definition object.
    */
   public void addAddresses( IAeDeploymentId aDeployment, String aDeploymentLocation, IAePartnerDefInfo aInfo );
   
   /**
    * Accessor for the partner address book.
    * @param aPrincipal the key into the partner mappings
    * @return address book of all partner link types mapped to the principal or null if none is found
    */
   public IAePartnerAddressBook getAddressBook( String aPrincipal );
   
   /**
    * Attempt to remove any addresses mapped to the deployment context.
    * NOTE: these mappings will only be removed (at the partner link type level)
    * if the reference count is equal to zero
    * @param aDeploymentId
    */
   public void removeAddresses( IAeDeploymentId aDeploymentId );

}
