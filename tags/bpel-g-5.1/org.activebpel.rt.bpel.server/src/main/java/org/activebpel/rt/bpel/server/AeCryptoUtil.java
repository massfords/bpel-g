//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/AeCryptoUtil.java,v 1.6 2008/02/17 21:38:47 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeJCEUtil;

/**
 *  Static utility methods supporting encryption, decryption, and keystore management using
 *  configuration settings from the engine configuration
 */
public class AeCryptoUtil
{
   /** JCE Util */
   private static AeJCEUtil sJCEUtil;
   
   /**
    * Initialize provider and load static key information
    */
   static
   {
      loadKey();
   }

   /**
    * Decrypts a string using our internal key
    * @param aInput String to decrypt
    * @return String decrypted
    */
   public static String decryptString(String aInput)
   {
      return sJCEUtil.decryptString(aInput);
   }

   /**
    * Encrypts a string using our internal key
    * 
    * @param aInput String to encrypt
    * @return decrypted string
    */
   public static String encryptString(String aInput)
   {
      return sJCEUtil.encryptString(aInput);
   }

   /**
    * Generates the internal key from the config entry
    */
   public static void loadKey()
   {
      try
      {
         // Get the string from the engine config to use to generate the key
         IAeEngineConfiguration config = AeEngineFactory.getEngineConfig();
         // Don't try anything if we can't get the config. If we are running in the designer, we will set the
         // key later
         if ( config == null )
            return;

         String password = config.getEntry("SharedSecret", "terces"); //$NON-NLS-1$ //$NON-NLS-2$
         loadKey(password);
      }
      catch (Exception e)
      {
         AeException.logWarning(AeMessages.getString("AeCryptoUtil.2")); //$NON-NLS-1$
      }
   }

   /**
    * Generates the internal key using the seed passed in
    */
   public static synchronized void loadKey(String aSeed)
   {
      sJCEUtil = AeJCEUtil.getInstance(aSeed);
   }
}
