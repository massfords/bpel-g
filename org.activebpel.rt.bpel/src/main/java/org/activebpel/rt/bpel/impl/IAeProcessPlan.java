// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeProcessPlan.java,v 1.15 2008/01/11 19:31:16 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AePartnerLinkDefKey;
import org.activebpel.rt.bpel.def.AePartnerLinkOpKey;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.w3c.dom.Element;

/**
 * Describes some basic characteristics of a process that we need in order to
 * for the engine to dispatch a message to a specific process (or possibly create
 * a process instance).
 */
public interface IAeProcessPlan extends IAeContextWSDLProvider
{
   /**
    * Returns a flag indicating if this activity should create the process instance.
    * @param aPartnerLinkOpKey the partner link and operation key
    * @return boolean flag indicating if the operation is capable of creating the process
    */
   public boolean isCreateInstance(AePartnerLinkOpKey aPartnerLinkOpKey);

   /**
    * Returns a List of correlation properties for the given partnerLink, portType and operation.
    * If no list has been set an empty collection will be returned.
    * @param aPartnerLinkOpKey the partner link and operation key
    */
   public Collection getCorrelatedPropertyNames(AePartnerLinkOpKey aPartnerLinkOpKey);

   /**
    * Gets the process definition.
    */
   public AeProcessDef getProcessDef();

   /**
    * Return the port type for the my role and partner link.
    *
    * @param aPartnerLinkKey
    */
   public QName getMyRolePortType(AePartnerLinkDefKey aPartnerLinkKey);

   /**
    * Return true if the process should be suspended if it encounters
    * an uncaught fault.
    */
   public boolean isSuspendProcessOnUncaughtFaultEnabled();

   /**
    * Returns a list of MyRole policies for the given partner link.   
    * @param aPartnerLink The partner link we are looking for
    */
   public List getMyRolePolicies(AePartnerLink aPartnerLink);

   /**
    * Returns a list of PartnerRole policies for the given partner link.   
    * @param aPartnerLink The partner link we are looking for
    */
   public List getPartnerRolePolicies(AePartnerLink aPartnerLink);
   
   /**
    * Return <code>true</code> if and only if the process should be suspended if
    * it has a non-durable invoke pending during process recovery.
    */
   public boolean isSuspendProcessOnInvokeRecoveryEnabled();
   
   /**
    * Getter for the extensions
    */
   public Element getExtensions();
   
}