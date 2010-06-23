// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAePartnerLink.java,v 1.9 2006/10/26 14:01:36 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AePartnerLinkDef;

/**
 * Describes the interface used for interacting with business process partner 
 * links.
 */
public interface IAePartnerLink extends IAeLocatableObject
{
   /**
    * Gets the name of the partner link
    */
   public String getName();

   /**
    * Gets the type of the partner link
    */
   public QName getPartnerLinkType();

   /**
    * Get the version number.
    */
   public int getVersionNumber();

   /**
    * Returns the endpoint reference for "myRole" or null if not defined.
    */
   public IAeEndpointReference getMyReference();

   /**
    * Returns the endpoint reference for "partnerRole" or null if not defined.
    */
   public IAeEndpointReference getPartnerReference();

   /**
    * Returns the name of the role that the process is playing.
    */
   public String getMyRole();

   /**
    * Returns the name of the role that the partner is playing.
    */
   public String getPartnerRole();

   /**
    * Getter for the principal
    */
   public String getPrincipal();

   /**
    * Setter for the principal
    * @param aPrincipal
    * @throws AeBusinessProcessException If the principal was already set to 
    *         some value other than null.
    */
   public void setPrincipal(String aPrincipal) throws AeBusinessProcessException;

   /**
    * Sets the version number.
    *
    * @param aVersionNumber
    */
   public void setVersionNumber(int aVersionNumber);
   
   /**
    * Increments the version number for the variable 
    */
   public void incrementVersionNumber();

   /**
    * Clears the partner link value. Called from the partner link's declaring
    * scope each time the scope is going to execute since the partner link's 
    * state is not preserved across invocations.
    */
   public void clear();
   
   /**
    * Gets the definition for the plink
    */
   public AePartnerLinkDef getDefinition();
   
   /**
    * Gets the conversation id for engine managed correlation 
    */
   public String getConversationId();
   
}
