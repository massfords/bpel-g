//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/invoke/AeFileBasedURNStorage.java,v 1.3 2005/12/03 01:06:07 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.invoke; 

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

import org.activebpel.rt.bpel.server.deploy.scanner.AeDeploymentFileInfo;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeURNStorage;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;

/**
 * File based implementation of <code>IAeURNStorage</code>. The file is kept up 
 * to date with changes.
 */
public class AeFileBasedURNStorage implements IAeURNStorage
{
   // TODO (MF) we should watch the file for changes like we do for the aeEngineConfig.xml file. 
   
   /** source for the props */
   private File mFile;
   
   /**
    * Map constructor required for construction through engine factory
    */
   public AeFileBasedURNStorage(Map aMap)
   {
      String filename = (String) aMap.get("File"); //$NON-NLS-1$
      if (AeUtil.isNullOrEmpty(filename))
      {
         filename = "urn.properties"; //$NON-NLS-1$
      }
      File file = new File(AeDeploymentFileInfo.getDeploymentDirectory(), filename);
      setFile(file);
   }
   
   /**
    * Creates the storage with the file as the src for the properties.
    * 
    * @param aFile
    */
   public AeFileBasedURNStorage(File aFile)
   {
      mFile = aFile;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeURNStorage#getMappings()
    */
   public synchronized Map getMappings() throws AeStorageException
   {
      // Defect #1225 fix: alway return a properties object instead of an immutable collection
      // (java.util.Collections.EMPTY_MAP) since it does not implement Map::put(key,value).
      // The AeFileBaseURNStorage::addMapping(..) method invokes a put() on the map return
      // by this method - hence the map implementation must be mutable.
      
      Properties props = new Properties();      
      if (getFile() != null  && getFile().exists() && getFile().isFile())
      {
         InputStream in = null;
         try
         {
            in = new FileInputStream(getFile());
            props.load(in);
         }
         catch(Exception e)
         {
            throw new AeStorageException(e);
         }
         finally
         {
            AeCloser.close(in);
         }
      }
      return props;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeURNStorage#addMapping(java.lang.String, java.lang.String)
    */
   public synchronized void addMapping(String aURN, String aURL) throws AeStorageException
   {
      Map map = getMappings();
      map.put(aURN, aURL);
      saveMappings(map);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeURNStorage#removeMappings(java.lang.String[])
    */
   public synchronized void removeMappings(String[] aURNArray) throws AeStorageException
   {
      Map map = getMappings();
      for (int i = 0; i < aURNArray.length; i++)
      {
         map.remove(aURNArray[i]);
      }
      saveMappings(map);
   }

   /**
    * Saves the mappings to the file.
    * 
    * @param aMap
    */
   protected synchronized void saveMappings(Map aMap) throws AeStorageException
   {
      Properties props = new Properties();
      props.putAll(aMap);
      
      OutputStream out = null;
      try
      {
         out = new FileOutputStream(getFile());
         props.store(out, ""); //$NON-NLS-1$
      }
      catch(Exception e)
      {
         throw new AeStorageException(e);
      }
      finally
      {
         AeCloser.close(out);
      }
   }

   /**
    * Getter for the file.
    */
   protected File getFile()
   {
      return mFile;
   }
   
   /**
    * Setter for the file
    * 
    * @param aFile
    */
   protected void setFile(File aFile)
   {
      mFile = aFile;
   }
}
 