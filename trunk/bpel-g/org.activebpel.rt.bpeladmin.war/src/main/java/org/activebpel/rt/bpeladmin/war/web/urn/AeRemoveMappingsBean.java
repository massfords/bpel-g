//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/urn/AeRemoveMappingsBean.java,v 1.1 2005/06/22 17:17:33 MFord Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.urn; 

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.bpeladmin.war.web.AeAbstractAdminBean;

/**
 * Removes all of the checked mappings from the resolver. 
 */
public class AeRemoveMappingsBean extends AeAbstractAdminBean
{
   /** key to look for in request param mappings */
   private String mDeleteKey;
   
   /**
    * Setter for the delete key
    * @param aKey
    */
   public void setDeleteKey(String aKey)
   {
      mDeleteKey = aKey;
   }
   
   /**
    * Getter for the delete key
    */
   public String getDeleteKey()
   {
      return mDeleteKey;
   }
   
   /**
    * Sets the form post data on the bean and triggers the removal of the urn mappings.
    * 
    * @param aFormData
    */
   public void setFormData(Map aFormData)
   {
      String[] values = (String[]) aFormData.get(getDeleteKey());
      if (values != null && values.length > 0)
      {
         // run URLDecoding on the URN values
         try
         {
            for (int i = 0; i < values.length; i++)
            {
               values[i] = URLDecoder.decode(values[i], "UTF8"); //$NON-NLS-1$
            }
         }
         catch(UnsupportedEncodingException ex)
         {
            // ignore, we should have UTF8 or there are bigger problems.
         }
         
         
         getAdmin().getURNAddressResolver().removeMappings(values);
         setStatusDetail(AeMessages.getString("AeRemoveMappingsBean.MAPPING_REMOVED")); //$NON-NLS-1$
      }
      else
      {
         setStatusDetail(AeMessages.getString("AeRemoveMappingsBean.NO_MAPPINGS_REMOVED")); //$NON-NLS-1$
      }
   }
}
 