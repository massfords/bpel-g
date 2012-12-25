//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/AeIdentityFactory.java,v 1.5 2008/02/17 21:54:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity;

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.config.AeConfigurationUtil;
import org.activebpel.rt.identity.provider.IAeIdentitySearchProvider;
import org.activebpel.rt.identity.search.IAeIdentitySearch;

/**
 * Factory used to create an identity search service implementation.
 */
public class AeIdentityFactory
{
   /**
    * Creates the identity search implementation given the configuration information.
    * @param aIdentitySearchConfig
    * @throws AeException
    */
   public static IAeIdentitySearch createIdentitySearch(Map aIdentitySearchConfig) throws AeException
   {
      Map providerConfig = (Map) aIdentitySearchConfig.get(AeIdentityConfig.IDENTITY_PROVIDER_ENTRY);
      IAeIdentitySearchProvider provider = (IAeIdentitySearchProvider) AeConfigurationUtil.createConfigSpecificClass(providerConfig);
      // Init provider
      provider.initialize();
      // Create search.
      IAeIdentitySearch search = (IAeIdentitySearch) AeConfigurationUtil.createConfigSpecificClass(aIdentitySearchConfig, provider, IAeIdentitySearchProvider.class);
      return search;
   }
}
