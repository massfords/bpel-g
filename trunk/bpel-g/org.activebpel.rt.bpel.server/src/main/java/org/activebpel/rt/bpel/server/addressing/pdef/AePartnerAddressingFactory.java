// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/addressing/pdef/AePartnerAddressingFactory.java,v 1.4 2005/02/08 15:36:04 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.addressing.pdef;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;

/**
 *  Base class for accessing the parnter addressing factory impls.
 */
abstract public class AePartnerAddressingFactory implements IAePartnerAddressingFactory
{

   /**
    * Creation method for a new factory instance.
    * Delegates to the findFactoryClassMethod to determine
    * the actual factory impl.
    * @return a new factory instance.
    * @throws AePartnerAddressingException
    */
   public static IAePartnerAddressingFactory newInstance() 
   throws AePartnerAddressingException
   {
      String factoryClassName = findFactoryClassName();
      return newInstance( factoryClassName );
   }
   
   /**
    * Looks for the factory class name in AeEngineConfig via AeEngineFactory.
    */
   protected static String findFactoryClassName()
   {
      return AeEngineFactory.getEngineConfig().getPartnerAddressingFactoryClassName();
   }
   
   /**
    * Creation method for instantiating a new factory instance
    * based on the factory class name arg.
    * @param aFactoryClassName
    * @throws AePartnerAddressingException
    */
   public static IAePartnerAddressingFactory newInstance( String aFactoryClassName )
   throws AePartnerAddressingException
   {
      if( !AeUtil.isNullOrEmpty( aFactoryClassName ) )
      {
         try
         {
            Class factoryClass = Class.forName( aFactoryClassName );
            return (IAePartnerAddressingFactory)factoryClass.newInstance();
         }
         catch (Exception e)
         {
            throw new AePartnerAddressingException(AeMessages.getString("AePartnerAddressingFactory.ERROR_0") + aFactoryClassName, e ); //$NON-NLS-1$
         }
      }
      else
      {
         throw new AePartnerAddressingException(AeMessages.getString("AePartnerAddressingFactory.ERROR_1")); //$NON-NLS-1$
      }
   }
}
