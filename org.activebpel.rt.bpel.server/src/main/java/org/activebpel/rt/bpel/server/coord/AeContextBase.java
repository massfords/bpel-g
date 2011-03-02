//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/AeContextBase.java,v 1.1 2005/10/28 21:10:30 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord;

import java.io.Serializable;
import java.util.Properties;

import org.activebpel.rt.util.AeUtil;

/**
 * Base class for simple property based context implementations.
 */
public class AeContextBase implements Serializable
{
   /**
    * Properties.
    */
   private Properties mProperties = null;
   
   /**
    * Default constructor.
    *
    */
   public AeContextBase()
   {      
   }

   /**
    * Returns the property value.
    * @param aName name of property.
    * @return property value or null if not found.
    */
   public String getProperty(String aName)
   {
      String rVal = null;
      if (AeUtil.notNullOrEmpty(aName))
      {
         rVal = getProperties().getProperty(aName);
      }
      return rVal;
   }
   
   /**
    * Sets the property.
    * @param aName name of property.
    * @param aValue value of property. This should not be null.
    */
   public void setProperty(String aName, String aValue)
   {
      if (AeUtil.notNullOrEmpty(aName) && aValue != null)
      {
         getProperties().setProperty(aName, aValue.trim());
      }
   }
   
   /**
    * @return Returns the properties.
    */
   public Properties getProperties()
   {
      if (mProperties == null)
      {
         mProperties = new Properties();
      }
      return mProperties;
   }
   
}
