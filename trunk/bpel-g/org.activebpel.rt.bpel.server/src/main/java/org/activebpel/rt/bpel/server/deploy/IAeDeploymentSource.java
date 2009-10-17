// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAeDeploymentSource.java,v 1.21 2007/11/21 03:26:02 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.Collection;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AeProcessDef;
import org.w3c.dom.Element;

/**
 * Interface for deploying bpel process to the engine.
 */
public interface IAeDeploymentSource
{
   /**
    * Gets the plan in id for this deployment source. Only applies when versioning is enabled.
    */
   public int getPlanId();

   /**
    * Get the name of the current pdd resource.
    * @return name of pdd resource
    */
   public String getPddLocation();

   /**
    * Accessor for bpel dom resource location.
    * @return bpel resource path
    */
   public String getBpelSourceLocation();
   
   /**
    * QName for the bpel process
    * @return bpel process QName
    */
   public QName getProcessName();
   
   /**
    * AeProcessDef for the bpel process.
    */
   public AeProcessDef getProcessDef();
   
   /**
    * Accessor for the dom process element.
    * @return dom process element
    */
   public Element getProcessSourceElement();
   
   /**
    * Set of keys for resource imports associated with this deployment.
    * @return Set of AeResourceKey objects.
    */
   public Set getResourceKeys();
   
   /**
    * Return the collection of partner link descriptors.
    */
   public Collection getPartnerLinkDescriptors();

   /**
    * Returns persistence type.
    */
   public AeProcessPersistenceType getPersistenceType();

   /**
    * Returns transaction type.
    */
   public AeProcessTransactionType getTransactionType();
   
   /**
    * Return the process exception management type.
    */
   public AeExceptionManagementType getExceptionManagementType();
   
   /**
    * Gets the services for the plan
    */
   public IAeServiceDeploymentInfo[] getServices() throws AeDeploymentException;

   /**
    * Returns the process-level {@link AeInvokeRecoveryType}.
    */
   public AeInvokeRecoveryType getInvokeRecoveryType();
   
   /**
    * Map of extension element QName to extension
    */
   public Element getExtensions();
}
