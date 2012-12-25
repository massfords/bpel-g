//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/IAeIdentityServiceConstants.java,v 1.2 2007/04/16 17:09:18 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity;

/**
 * WSDL and Schema NS constants used by the identity service.
 *
 */
public interface IAeIdentityServiceConstants
{
   /** WSDL Document NS */
   public static final String IDENTITY_SERVICE_NS = "http://docs.active-endpoints/wsdl/identity/2007/03/identity.wsdl"; //$NON-NLS-1$
   /** WSDL NS prefix */
   public static final String IDENTITY_SERVICE_PREFIX = "aeidsvc"; //$NON-NLS-1$   
   /** Schema type NS */
   public static final String IDENTITY_TYPES_NS = "http://schemas.active-endpoints.com/identity/2007/01/identity.xsd"; //$NON-NLS-1$
   /** Schema type NS prefix */
   public static final String IDENTITY_TYPES_PREFIX = "aeid"; //$NON-NLS-1$      
   /** Name of the Identity Service custom manager. */
   public static final String MANAGER_NAME = "IdentityServiceManager"; //$NON-NLS-1$

}
