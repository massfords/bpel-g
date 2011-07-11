//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeJCEUtil.java,v 1.4 2008/02/17 21:09:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.base64.Base64;

/**
 *  Static utility methods supporting JCE encryption, decryption
 */
public class AeJCEUtil
{
   /** Default seed value */
   private static final String DEFAULT_KEY_SEED = "terces"; //$NON-NLS-1$
   
   /** Algorithm name */
   private static final String DES_EDE = "DESede"; //$NON-NLS-1$
   
   /** System property used to set the des key seed */
   public static final String KEY_GEN_PROPERTY = "org.activebpel.rt.jce.SharedSecret"; //$NON-NLS-1$

   /** Keystore password property */
   private static final String KEYSTORE_PASSWORD_PROPERTY = "org.apache.ws.security.crypto.merlin.keystore.password"; //$NON-NLS-1$

   /** Crypto properties file */
   private static final String CRYPTO_PROP_FILE = "crypto.properties"; //$NON-NLS-1$

   /** Default instance */
   private static AeJCEUtil sDefaultInstance = new AeJCEUtil();   
   
   /** Internal key */
   private SecretKey internalDesKey;
   
   /** Keystore password */
   private static String sKeystorePassword;

   /**
    * Private constructor that checks the system property for DES key seed
    */
   private AeJCEUtil()
   {
      String seed = System.getProperty(KEY_GEN_PROPERTY);
      if (AeUtil.isNullOrEmpty(seed))
      {
         seed = DEFAULT_KEY_SEED;
      }
      loadKey(seed);
      loadKeystorePassword(CRYPTO_PROP_FILE);
   }
   
   /**
    * Private constructor with seed
    * @param aSeed
    */
   private AeJCEUtil(String aSeed)
   {
      loadKey(aSeed);
   }

   /**
    * @return instance with default seed
    */
   public static AeJCEUtil getDefaultInstance()
   {
      return sDefaultInstance;
   }
   
   /**
    * Returns an instance with the key generated from the given seed 
    * @param aSeed
    */
   public static AeJCEUtil getInstance(String aSeed)
   {
      return new AeJCEUtil(aSeed);
   }
   
   /**
    * Generates a key for use with xml security key wrap algorithms
    * @return SecretKey DESede for xml key wrap of embedded key
    * @throws Exception
    */
   public SecretKey generateKeyEncryptionKey() throws Exception
   {
      String jceAlgorithmName = DES_EDE;
      KeyGenerator keyGenerator = KeyGenerator.getInstance(jceAlgorithmName);
      SecretKey kek = keyGenerator.generateKey();

      return kek;
   }

   /**
    * generate a key for data encryption
    * @return SecretKey the encryption key
    * @throws Exception
    */
   public SecretKey generateDataEncryptionKey() throws Exception
   {

      String jceAlgorithmName = DES_EDE;
      KeyGenerator keyGenerator = KeyGenerator.getInstance(jceAlgorithmName);
      return keyGenerator.generateKey();
   }

   /**
    * Creates a DESede key and stores it in a file for use as the internal key for password encryption
    * @param filename
    * @throws Exception
    */
   public void createEncryptionKeyFile(String filename) throws Exception
   {
      FileOutputStream f = null;
      try
      {
         SecretKey key = generateDataEncryptionKey();
         byte[] keyBytes = key.getEncoded();

         // Store as a file
         File keyFile = new File(filename);
         f = new FileOutputStream(keyFile);
         f.write(keyBytes);
      }
      finally
      {
         if ( f != null )
            f.close();
      }
   }

   /**
    * Encrypts a string
    * @param input String to encrypt
    * @param key Key to use
    * @return String encrypted string
    */
   public String encryptString(String input, Key key)
   {
      try
      {
         Cipher cipher = Cipher.getInstance(key.getAlgorithm());
         cipher.init(Cipher.ENCRYPT_MODE, key);
         byte[] encrypted = cipher.doFinal(input.getBytes());
         return Base64.encodeBytes(encrypted);
      }
      catch (Exception e)
      {
         RuntimeException re = new IllegalStateException(e.getLocalizedMessage());
         re.initCause(e);
         throw re;
      }
   }

   /**
    * Decrypts a string
    * @param input String to encrypt
    * @param key Key to use
    * @return String decrypted string
    */
   public String decryptString(String input, Key key)
   {
      try
      {
         Cipher cipher = Cipher.getInstance(key.getAlgorithm());
         cipher.init(Cipher.DECRYPT_MODE, key);
         byte[] original = cipher.doFinal(Base64.decode(input));
         return new String(original);
      }
      catch (Exception e)
      {
         RuntimeException re = new IllegalStateException(e.getLocalizedMessage());
         re.initCause(e);
         throw re;
      }
   }

   /**
    * Decrypts a string using our internal key
    * @param aInput String to decrypt
    * @return String decrypted
    */
   public String decryptString(String aInput)
   {
      if ( internalDesKey == null )
         loadKey(DEFAULT_KEY_SEED);
      return decryptString(aInput, internalDesKey);
   }

   /**
    * Encrypts a string using our internal key
    * 
    * @param aInput String to encrypt
    * @return decrypted string
    */
   public String encryptString(String aInput)
   {
      if ( internalDesKey == null )
         loadKey(DEFAULT_KEY_SEED);

      return encryptString(aInput, internalDesKey);
   }

   /**
    * Generates the internal key using the seed passed in
    */
   public void loadKey(String seed)
   {
      try
      {
         String password = seed;

         MessageDigest digest = MessageDigest.getInstance("SHA"); //$NON-NLS-1$
         digest.update(password.getBytes());
         digest.update(password.getBytes());
         digest.update(password.getBytes());
         digest.update(password.getBytes());
         byte[] keyBytes = new byte[24];
         digest.digest(keyBytes, 0, 24);

         DESedeKeySpec keySpec = new DESedeKeySpec(keyBytes);
         SecretKeyFactory skf = SecretKeyFactory.getInstance(DES_EDE);
         internalDesKey = skf.generateSecret(keySpec);
      }
      catch (Exception e)
      {
         RuntimeException re = new IllegalStateException(e.getLocalizedMessage());
         re.initCause(e);
         throw re;
      }
   }
   
   /**
    * Loads the keystore password from properties file
    * @param propfile
    */
   public void loadKeystorePassword(String propfile)
   {
      InputStream is = null;
      try
      {
         // Set the default
         sKeystorePassword = "security"; //$NON-NLS-1$

         URL url = AeUtil.findOnClasspath(propfile, propfile.getClass());
         // don't bother if we can't find it
         if ( url == null )
            return;

         is = url.openStream();

         Properties cryptoProperties = new Properties();
         cryptoProperties.load(is);
         sKeystorePassword = cryptoProperties.getProperty(KEYSTORE_PASSWORD_PROPERTY);
      }
      catch (Exception e)
      {
         AeException.logWarning(AeMessages.getString("AeCryptoUtil.2")); //$NON-NLS-1$
      }
      finally
      {
         AeCloser.close(is);
      }
   }
   
   /**
    * get the static keystore password
    */
   public String getKeystorePassword()
   {
      return sKeystorePassword;
   }
   
   /**
    * Command line util to encrypt a string. First argument is the string to
    * encrypt. Optional second argument is the seed value to use for the 
    * encryption. Encrypted string is printed to standard out.
    * @param args
    */
   public static void main(String[] args) throws Exception
   {
      if (args.length == 1)
      {
         // gen default key
         System.out.println( AeJCEUtil.getDefaultInstance().encryptString(args[0]) );
      }
      else if (args.length == 2)
      {
         // gen using key provided
         System.out.println( AeJCEUtil.getInstance(args[1]).encryptString(args[0]) );
      }
      else
      {
         System.out.println( AeMessages.getString("AeJCEUtil.usage") ); //$NON-NLS-1$
      }
   }
}
