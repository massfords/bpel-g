//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeDeploymentSummary.java,v 1.3 2005/02/01 19:56:30 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * This class contains all information about the deployment of a single BPR file.  Each
 * BPR may have multiple deployment units.  The result of the deployment of each unit 
 * will exist in a list.
 */
public class AeDeploymentSummary implements IAeDeploymentSummary, Serializable
{
   /** The root element name for the XML representation of this object. */
   private static final String DEPLOYMENT_SUMMARY_ELEM = "deploymentSummary"; //$NON-NLS-1$
   /** The global messages element name. */
   private static final String GLOBAL_MESSAGES_ELEM = "globalMessages"; //$NON-NLS-1$
   /** The attribute name that will hold the number of deployment errors. */
   private static final String NUM_ERRORS_ATTR         = "numErrors"; //$NON-NLS-1$
   /** The attribute name that will hold the number of deployment warnings. */
   private static final String NUM_WARNINGS_ATTR       = "numWarnings"; //$NON-NLS-1$

   /** The list of deployment info objects. */
   protected List mDeploymentInfos;
   /** The global messages found during deployment. */
   protected String mGlobalMessages;

   /**
    * Constructs a deployment summary given a list of deployment info objects.
    * 
    * @param aList
    */
   public AeDeploymentSummary(List aList, String aMessages)
   {
      mDeploymentInfos = aList;
      mGlobalMessages = aMessages;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSummary#getDeploymentInfoList()
    */
   public List getDeploymentInfoList()
   {
      return mDeploymentInfos;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSummary#getTotalWarnings()
    */
   public int getTotalWarnings()
   {
      int total = 0;
      Iterator iter = getDeploymentInfoList().iterator();
      while (iter.hasNext())
      {
         IAeDeploymentInfo depInfo = (IAeDeploymentInfo) iter.next();
         total += depInfo.getNumWarnings();
      }
      return total;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSummary#getTotalErrors()
    */
   public int getTotalErrors()
   {
      int total = 0;
      Iterator iter = getDeploymentInfoList().iterator();
      while (iter.hasNext())
      {
         IAeDeploymentInfo depInfo = (IAeDeploymentInfo) iter.next();
         total += depInfo.getNumErrors();
      }
      return total;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSummary#toDocument()
    */
   public Document toDocument()
   {
      Document dom = AeXmlUtil.newDocument();

      // Create the root element
      Element rootElem = dom.createElement(DEPLOYMENT_SUMMARY_ELEM);
      dom.appendChild(rootElem);
      rootElem.setAttribute(NUM_ERRORS_ATTR, "" + getTotalErrors()); //$NON-NLS-1$
      rootElem.setAttribute(NUM_WARNINGS_ATTR, "" + getTotalWarnings()); //$NON-NLS-1$

      // Create the global messages element
      Element globalMessages = dom.createElement(GLOBAL_MESSAGES_ELEM);
      globalMessages.appendChild(dom.createTextNode(mGlobalMessages));
      rootElem.appendChild(globalMessages);

      // Iterate through all of the deployment info objects and create elements for each one.
      Iterator iter = getDeploymentInfoList().iterator();
      while (iter.hasNext())
      {
         IAeDeploymentInfo depInfo = (IAeDeploymentInfo) iter.next();
         Element elem = depInfo.toElement(dom);
         rootElem.appendChild(elem);
      }

      return dom;
   }

}
