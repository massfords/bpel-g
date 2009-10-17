//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/urn/AeAddMappingBean.java,v 1.1 2005/06/22 17:17:33 MFord Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.urn; 

import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.bpeladmin.war.web.AeAbstractAdminBean;
import org.activebpel.rt.util.AeUtil;

/**
 * Adds a URN to URL mapping 
 */
public class AeAddMappingBean extends AeAbstractAdminBean
{
   /** urn used as the key */
   private String mURN;
   /** url that it maps to */
   private String mURL;
   
   /**
    * Trigger for the bean to add the mapping info to the resolver
    * @param aBool
    */
   public void setFinished(boolean aBool)
   {
      if (aBool)
      {
         if (isValid())
         {
            getAdmin().getURNAddressResolver().addMapping(getURN(), getURL());
            setStatusDetail(AeMessages.getString("AeAddMappingBean.MAPPING_ADDED")); //$NON-NLS-1$
         }
      }
   }
   
   /**
    * Mapping is valid if both values are non null.
    */
   protected boolean isValid()
   {
      boolean valid = AeUtil.notNullOrEmpty(mURN) && AeUtil.notNullOrEmpty(mURL);
      if (!valid)
      {
         setStatusDetail(AeMessages.getString("AeAddMappingBean.MAPPING_INVALID")); //$NON-NLS-1$
         setErrorDetail(true);
      }
      return valid;
   }
   
   /**
    * @return Returns the uRL.
    */
   public String getURL()
   {
      return mURL;
   }
   
   /**
    * @param aUrl The uRL to set.
    */
   public void setURL(String aUrl)
   {
      mURL = aUrl;
   }
   
   /**
    * @return Returns the uRN.
    */
   public String getURN()
   {
      return mURN;
   }
   
   /**
    * @param aUrn The uRN to set.
    */
   public void setURN(String aUrn)
   {
      mURN = aUrn;
   }
}
 