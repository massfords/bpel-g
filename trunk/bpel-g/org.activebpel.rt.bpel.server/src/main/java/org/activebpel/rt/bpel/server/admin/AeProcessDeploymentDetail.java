// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/AeProcessDeploymentDetail.java,v 1.6 2004/10/29 21:14:13 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin;

import javax.xml.namespace.QName;

/**
 * JavaBean for holding some data for a deployed process. It includes the
 * name of the process as well as any process ids for running instances.
 */
public class AeProcessDeploymentDetail
{
   /** Name of the deployed process */
   private QName mName;
   /** Deployment xml for this process */
   private String mSourceXml;
   /** The src bpel for the deployed process */
   private String mBpelSourceXml;
   
   /**
    * Default Ctor
    */
   public AeProcessDeploymentDetail()
   {
   }

   /**
    * Getter for the process name
    */
   public QName getName()
   {
      return mName;
   }
   
   /**
    * Accessor for process deployment qname local part.
    */
   public String getLocalName()
   {
      return getName().getLocalPart();
   }

   /**
    * Sets the name of the deployed process
    * @param aName
    */
   public void setName(QName aName)
   {
      mName = aName;
   }

   /**
    * Setter for the source xml
    * @param sourceXml
    */
   public void setSourceXml(String sourceXml)
   {
      mSourceXml = sourceXml;
   }

   /**
    * Getter for the source xml
    */
   public String getSourceXml()
   {
      return mSourceXml;
   }

   /**
    * @param aBpelSourceXml
    */
   public void setBpelSourceXml(String aBpelSourceXml)
   {
      mBpelSourceXml = aBpelSourceXml;
   }
   
   /**
    * Getter for bpel source xml.
    */
   public String getBpelSourceXml()
   {
      return mBpelSourceXml;
   }
}
