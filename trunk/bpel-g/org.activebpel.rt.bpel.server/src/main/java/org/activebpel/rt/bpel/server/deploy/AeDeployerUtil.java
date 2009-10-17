// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeDeployerUtil.java,v 1.4 2005/02/08 15:35:59 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.server.AeMessages;

/**
 * Utility class for creating deployers from config map.
 */
public class AeDeployerUtil
{
   // Error message constants.
   private static final String MISSING_DEPLOYMENT_PARAMS = AeMessages.getString("AeDeployerUtil.0"); //$NON-NLS-1$
   private static final String MISSING_CLASS_ENTRY = AeMessages.getString("AeDeployerUtil.1"); //$NON-NLS-1$
   private static final String CLASS_NOT_FOUND_ERROR = AeMessages.getString("AeDeployerUtil.2"); //$NON-NLS-1$
   private static final String INSTANTIATION_ERROR = AeMessages.getString("AeDeployerUtil.3"); //$NON-NLS-1$
   
   /**
    * Create a deployer impl instance.
    * @param aDeployerKey The key to the deployer params map.
    * @param aConfig The engine config map.
    * @throws AeException
    */
   public static Object createDeployer( String aDeployerKey, Map aConfig )
   throws AeException
   {
      Map deployerParams = (Map)aConfig.get(aDeployerKey);

      if( deployerParams == null )
      {
         throw new AeException( MessageFormat.format(MISSING_DEPLOYMENT_PARAMS, new Object[] {aDeployerKey}) );
      }
      
      Class deployerClass = loadDeployerClass( aDeployerKey, deployerParams );
      
      try
      {
         return createDeployer( deployerClass, deployerParams );
      }
      catch( NoSuchMethodException ne )
      {
         return createDeployer( deployerClass );
      }
   }
   
   /**
    * Create the deployer impl class.
    * @param aDeployerKey
    * @param aConfig
    * @throws AeException
    */
   protected static Class loadDeployerClass( String aDeployerKey, Map aConfig )
   throws AeException
   {
      String className = (String) aConfig.get( IAeEngineConfiguration.CLASS_ENTRY );

      if (className == null)
      {
         throw new AeException( MessageFormat.format( MISSING_CLASS_ENTRY, aDeployerKey ) ); 
      }
      
      try
      {
         return Class.forName( className );
      }
      catch (ClassNotFoundException e)
      {
         throw new AeException( MessageFormat.format(CLASS_NOT_FOUND_ERROR, className, aDeployerKey), e );
      }
   }
   
   /**
    * Try and create the deployer impl by looking for a constructor
    * that accepts a map.
    * @param aDeployerClass
    * @param aConfig
    * @throws NoSuchMethodException
    * @throws AeException
    */
   protected static Object createDeployer( Class aDeployerClass, Map aConfig )
   throws NoSuchMethodException, AeException
   {
      Constructor cons = aDeployerClass.getConstructor(new Class[] { Map.class });
      try
      {
         return cons.newInstance(new Object[] { aConfig });
      }
      catch (Throwable e)
      {
         throw new AeException( MessageFormat.format(INSTANTIATION_ERROR, aDeployerClass.getName()), e);
      }
   }
   
   /**
    * Create a deployer impl instance from its default constructor.
    * @param aDeployerClass
    * @throws AeException
    */
   protected static Object createDeployer( Class aDeployerClass )
   throws AeException
   {
      try
      {
         return aDeployerClass.newInstance();
      }
      catch (Throwable e)
      {
         throw new AeException( MessageFormat.format(INSTANTIATION_ERROR, aDeployerClass.getName()), e);
      }
   }
}
