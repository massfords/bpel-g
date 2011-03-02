//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeServiceMap.java,v 1.4 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy; 

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Maps service names to their deployment information.
 */
public class AeServiceMap
{
   /** singleton instance */
   private static final AeServiceMap INSTANCE = new AeServiceMap();
   
   /** maps the service name to the service data */
   private Map mMap = new HashMap();
   
   /**
    * singleton getter
    */
   protected static final AeServiceMap getInstance()
   {
      return INSTANCE;
   }
   
   /**
    * Returns a list of <code>AeServiceDeploymentInfo</code> entries currently deployed in the engine. 
    */
   public static List getServiceEntries()
   {
      return new LinkedList(getInstance().mMap.values());
   }
   
   /**
    * Adds the service data to our cache. This is called when a plan is deployed
    * by the deployment handler. The handler is synchronized already so we don't
    * need to worry about synchronizing here.
    * 
    * @param aServiceData
    */
   public void addServiceData(IAeServiceDeploymentInfo[] aServiceData)
   {
      // work off of a copy of the map so we don't have to sync the reads.
      Map copy = new HashMap(mMap);
      for (int i = 0; i < aServiceData.length; i++)
      {
         copy.put(aServiceData[i].getServiceName(), aServiceData[i]);
      }
      mMap = copy;
   }
   
   /**
    * Gets the service data mapped to the service name
    * 
    * @param aServiceName
    */
   public IAeServiceDeploymentInfo getServiceData(String aServiceName)
   {
      return (IAeServiceDeploymentInfo) mMap.get(aServiceName);
   }
   
   /**
    * Called by the deployment provider when a plan is undeployed. The plans get
    * undeployed through the deployment handler which is already synchronized.
    * 
    * @param aProcessQName
    */
   public void processUndeployed(QName aProcessQName)
   {
      // work off of a copy of the map so we don't have to sync the reads.
      Map copy = new HashMap(mMap);
      for (Iterator iter = copy.entrySet().iterator(); iter.hasNext();)
      {
         Map.Entry entry = (Map.Entry) iter.next();
         IAeServiceDeploymentInfo data = (IAeServiceDeploymentInfo) entry.getValue();
         if (data.getProcessQName().equals(aProcessQName))
         {
            iter.remove();
         }
      }
      mMap = copy;
   }
}
 