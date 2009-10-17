// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AePartnerDefsBean.java,v 1.1 2004/08/19 16:19:23 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 * Wraps the partner definitions listing.
 */
public class AePartnerDefsBean
{
   /** Wrapper principal names. */
   protected AeJavaTypesWrapper[] mPartners;
   
   /**
    * Constructor.  Initializes the wrapped details.
    */
   public AePartnerDefsBean()
   {
      String[] partners = 
            AeEngineFactory.getEngineAdministration().getPartnerAddressingAdmin().getPrincipals();

      if( partners != null )
      {
         mPartners = AeJavaTypesWrapper.wrap( partners );            
      }
   }
   
   /**
    * Returns true if there are no details to display.
    */
   public boolean isEmpty()
   {
      return mPartners == null || mPartners.length == 0;
   }
   
   /**
    * Indexed property accessor for the wrapped principal.
    * @param aIndex
    * @return AeJavaTypesWrapper Wraps the string name.
    */
   public AeJavaTypesWrapper getPrincipal( int aIndex )
   {
      return mPartners[aIndex];
   }
   
   /**
    * Returns the size of the indexed property array.
    */
   public int getPrincipalSize()
   {
      if( mPartners == null )
      {
         return 0;
      }
      return mPartners.length;
   }
}
