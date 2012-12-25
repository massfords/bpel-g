//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/AeIdentityServiceConfiguration.java,v 1.2 2008/02/17 21:54:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.config.AeConfiguration;
import org.activebpel.rt.config.AeConfigurationUtil;
import org.w3c.dom.Document;

/**
 * Configuration for the identity service.
 */
public class AeIdentityServiceConfiguration extends AeConfiguration
{
   /**
    * Creates a configuration based on the given xml configuration file.
    * @param aXmlConfigFile
    * @throws AeException
    */
   public AeIdentityServiceConfiguration(File aXmlConfigFile) throws AeException
   {
      try
      {
         Map entries = AeConfigurationUtil.loadConfig(new InputStreamReader(new FileInputStream(aXmlConfigFile)));
         setEntries( entries );
      }
      catch(IOException ioe)
      {
         throw new AeException(ioe);
      }
   }
   
   /**
    * Creates a configuration based on the given xml configuration reader.
    * @param aXmlConfigReader
    * @throws AeException
    */
   public AeIdentityServiceConfiguration(Reader aXmlConfigReader) throws AeException
   {
      Map entries = AeConfigurationUtil.loadConfig(aXmlConfigReader);
      setEntries( entries );
   }
   
   /**
    * Creates a configuration based on the given xml configuration document.
    * @param aXmlConfigDocument
    * @throws AeException
    */
   public AeIdentityServiceConfiguration(Document aXmlConfigDocument) throws AeException
   {
      Map entries = AeConfigurationUtil.loadConfig(aXmlConfigDocument);
      setEntries( entries );
   }   
}
