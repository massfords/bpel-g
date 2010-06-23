// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/addressing/pdef/AeDefaultPartnerAddressingFactory.java,v 1.2 2004/07/08 13:10:02 ewittmann Exp $
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
 * Returns the AeDefaultPartnerAddressingProvider.
 */
public class AeDefaultPartnerAddressingFactory extends AePartnerAddressingFactory
{
   
   /** default partner addressing provider */
   private static final AeDefaultPartnerAddressingProvider PROVIDER =
      new AeDefaultPartnerAddressingProvider();

   /**
    * @see org.activebpel.rt.bpel.server.addressing.pdef.IAePartnerAddressingFactory#getProvider()
    */
   public IAePartnerAddressingProvider getProvider()
   {
      return PROVIDER;
   }

}
