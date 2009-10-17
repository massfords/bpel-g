//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeIdentityServiceBean.java,v 1.2.4.1 2008/04/28 21:56:06 vvelusamy Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import java.io.File;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig;
import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.bpeladmin.war.web.AeAbstractAdminBean;
import org.activebpel.rt.identity.AeIdentityConfig;
import org.activebpel.rt.identity.provider.AeIdentityFileConfig;
import org.activebpel.rt.util.AeUtil;

/**
 * Bean for accessing and updating identity service
 * configuration settings.
 */
public class AeIdentityServiceBean extends AeAbstractAdminBean
{
   /** ldif provider type. */
   public static final String LDIF_TYPE = "ldif"; //$NON-NLS-1$
   /** xml file provider type. */
   public static final String TOMCAT_TYPE = "tomcat"; //$NON-NLS-1$
   /** file config map. */
   protected AeIdentityFileConfig mFileConfig;
   /** Username or principal name to test the configuration. */
   private String mTestPrincipalName = ""; //$NON-NLS-1$


   /**
    * Convenience method to get theupdatable versioned config.
    */
   protected IAeUpdatableEngineConfig getUpdatableConfig()
   {
      return getAdmin().getEngineConfig().getUpdatableEngineConfig();
   }

   /**
    * @return Config wrapper for identity service File settings.
    */
   protected AeIdentityFileConfig getFileConfig()
   {
      if (mFileConfig == null)
      {
         mFileConfig = AeIdentityFileConfig.getFileConfigFromConfig( getUpdatableConfig() );
      }
      return mFileConfig;
   }

   /**
    * Saves the current config settings to the database.
    */
   protected void saveChanges()
   {
      AeIdentityConfig.setOnConfig( getFileConfig(), getUpdatableConfig() );
      getUpdatableConfig().update();
   }

   /**
    * Tests the current configuration.
    */
   protected void testConfiguration(String aPrincipalName)
   {
      try
      {
         internalTestConfiguration(aPrincipalName);
         addStatusDetail(AeMessages.getString("AeIdentityServiceBean.ValidConfigSettings")); //$NON-NLS-1$
      }
      catch(AeException aex)
      {
         addStatusDetail(AeMessages.getString("AeIdentityServiceBean.InvalidConfigSettings")); //$NON-NLS-1$
         addStatusDetail(aex.getInfo());
         setErrorDetail(true);
      }
   }
   
   /**
    * Internal test method which throws on failure.
    * @param aPrincipalName
    * @throws AeException
    */
   protected void internalTestConfiguration(String aPrincipalName) throws AeException
   {
      AeIdentityConfig.testConfiguration(getFileConfig(), aPrincipalName);
   }   
   
   
   /**
    * Returns the enabled flag.
    */
   public boolean isEnabled()
   {
      return getFileConfig().isEnabled();
   }
   
   /**
    * Returns true if configured for ldif.
    */
   public boolean isLdif()
   {
      return getFileConfig().isLdif();
   }

   /**
    * Returns true if configured for tomcat.
    */
   public boolean isTomcat()
   {
      return getFileConfig().isTomcat();
   }

   /**
    * Setter for enabled flag.
    * @param aEnabled
    */
   public void setEnabled(boolean aEnabled)
   {
      getFileConfig().setEnabled(aEnabled);
   }

   /**
    * Returns File server FileName name.
    * @return File FileName name
    */
   public String getFileName()
   {
      return getFileConfig().getFileName();
   }

   /**
    * Sets the File server FileName name.
    * @param aFileName FileNamename.
    */
   public void setFileName(String aFileName)
   {
      getFileConfig().setFileName( AeUtil.getSafeString(aFileName).trim() );
   }


   /**
    * Sets the current provider type based on the passed string.
    * @param aProviderType
    */
   public void setProviderType( String aProviderType )
   {
      String provider = null;
      if(LDIF_TYPE.equals(aProviderType))
         provider = AeIdentityFileConfig.LDIF_PROVIDER;
      else if(TOMCAT_TYPE.equals(aProviderType))
         provider = AeIdentityFileConfig.TOMCAT_PROVIDER;

      if(provider != null)
      {
         getFileConfig().setProvider(provider);
      }
   }


   /**
    * @return Username or principal name to test configuration.
    */
   public String getTestPrincipalName()
   {
      return mTestPrincipalName;
   }

   /**
    * Sets the username or principal to test the configuration.
    * @param aTestPrincipalName
    */
   public void setTestPrincipalName(String aTestPrincipalName)
   {
      mTestPrincipalName = aTestPrincipalName;
   }

   /**
    * Indicates the update button has been pressed and all changes will be persisted
    * to the database.
    * @param aValue
    */
   public void setUpdate( String aValue )
   {
      if (AeUtil.isNullOrEmpty(aValue))
      {
         return;
      }
      processFormData(false);
      if ( !isErrorDetail() )
      {
         saveChanges();
      }
   }

   /**
    * Invoked by the JSP when the test configuration submit button is pressed.
    * @param aValue
    */
   public void setTestConfiguration( String aValue )
   {
      if (AeUtil.isNullOrEmpty(aValue))
      {
         return;
      }

      if (AeUtil.isNullOrEmpty( getTestPrincipalName() ))
      {
         addError("testconfig.principal", getTestPrincipalName(), AeMessages.getString("AeIdentityServiceBean.ConfigTest_Principal_Required")); //$NON-NLS-1$ //$NON-NLS-2$
      }
      else
      {
         processFormData(true);
      }

      if (!isErrorDetail())
      {
         testConfiguration(getTestPrincipalName());
      }
   }

   /**
    * Processes and validates the form data.
    * @param aForTesting true if the test button was pressed.
    */
   protected void processFormData(boolean aForTesting)
   {
      if( isEnabled())
      {
         // LDIF/XML type.
         // check to see if the file name exists
         if ( AeUtil.isNullOrEmpty( getFileConfig().getFileName() ) )
         {
            addError("fileName", "", AeMessages.getString("AeIdentityServiceBean.FileName_Required")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
         }
         else if (!validateFileLocation(getFileConfig().getFileName()))
         {
            addError("fileName", "", AeMessages.getString("AeIdentityServiceBean.Invalid_FileName")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
         }
      }
   }


   /**
    * Checks to see if the file identified by the location path exists.
    * @param aFileLocationPath
    * @return true if file exists.
    */
   protected boolean validateFileLocation(String aFileLocationPath)
   {
      String fileLocationPath = AeUtil.replaceAntStyleParams(aFileLocationPath, null);
      
      try
      {
         File file = new File(fileLocationPath);
         return file.isFile();
      }
      catch(Exception e)
      {
         AeException.logWarning(e.getMessage());
         return false;
      }
   }
}
