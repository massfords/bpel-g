//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAeServiceDeploymentInfo.java,v 1.1 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AePartnerLinkDefKey;
import org.w3c.dom.Element;

/**
 * Models service data for persistence.
 */
public interface IAeServiceDeploymentInfo
{

   /**
    * Accessor for service name.
    */
   public String getServiceName();

   /**
    * Accessor for the partner link name.
    */
   public String getPartnerLinkName();

   /**
    * Gets the partner link def key reference.
    */
   public AePartnerLinkDefKey getPartnerLinkDefKey();

   /**
    * Accessor for binding string.
    */
   public String getBinding();

   /**
    * Returns true if the binding is MSG.
    */
   public boolean isMessageService();

   /**
    * Returns true if the binding is RPC
    */
   public boolean isRPCEncoded();

   /**
    * Returns true if the binding is RPC literal
    */
   public boolean isRPCLiteral();

   /**
    * Returns true if the binding is EXTERNAL
    */
   public boolean isExternalService();

   /**
    * Returns true if the binding is policy driven
    */
   public boolean isPolicyService();

   /**
    * Add a role to the service data.
    */
   public void addRole(String aRole);

   /**
    * Accessor for comma delimited allowed roles.  May be null.
    */
   public String getAllowedRolesAsString();

   /**
    * Accessor for allowed roles as a set. May be empty.
    */
   public Set getAllowedRoles();

   /**
    * Accessor for process qname.
    */
   public QName getProcessQName();

   /**
    * Accessor for process name.
    */
   public String getProcessName();

   /**
    * Add a policy to the list of policies.
    */
   public void addPolicy(Element policy);

   /**
    * Accessor for list of policies.
    */
   public List getPolicies();

}