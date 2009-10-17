// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeMessageReceiverDetailWrapper.java,v 1.6 2007/03/06 15:48:57 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 * Wraps single AeMessageReceiver for bean access.
 */
public class AeMessageReceiverDetailWrapper
{
   /** AeMessageReceiver detail object. */
   protected AeMessageReceiver mDetail;
   
   /**
    * Constructor.
    * @param aReceiver The delegate AeMessageReceiver.
    */
   public AeMessageReceiverDetailWrapper(AeMessageReceiver aReceiver)
   {
      mDetail = aReceiver;
   }
   
   /**
    * Getter for partner link type name.
    */
   public String getPartnerLinkTypeName()
   {
      return mDetail.getPartnerLinkOperationKey().getPartnerLinkName();
   }
   
   /**
    * Getter for port type qname string.
    */
   public String getPortType()
   {
      return AeWebUtil.toString( mDetail.getPortType() );
   }
   
   /**
    * Getter for the local portion of the port type qname.
    */
   public String getPortTypeLocal()
   {
      return mDetail.getPortType().getLocalPart();
   }
   
   /**
    * Getter for operation string.
    */
   public String getOperation()
   {
      return mDetail.getOperation();
   }
   
   /**
    * Getter for process id.
    */
   public int getProcessId()
   {
      return (int)mDetail.getProcessId();
   }
   
   /**
    * Returns true if receiver contains correlation data.
    */
   public boolean isCorrelated()
   {
      return mDetail.getCorrelation() != null && 
         !mDetail.getCorrelation().isEmpty();
   }
   
   /**
    * Returns correlation data as a string.  Each line represents a name value pair.
    */
   public String getCorrelationData()
   {
      String data = AeWebUtil.escapeSingleQuotes( AeWebUtil.toString(mDetail.getCorrelation()) ); 
      return data;
   }
   
   /**
    * Returns the location path string.
    */
   public String getLocationPath()
   {
      try
      {
         // NOTE: May acquire process lock, not to be used on listing pages
         String path = AeEngineFactory.getEngineAdministration().getLocationPathById(getProcessId(), getLocationPathId()); 
         return path.replace('\'',' ');
      }
      catch (AeBusinessProcessException e)
      {
         // ignore
         return ""; //$NON-NLS-1$
      }
   }
   
   /**
    * Returns the message receiver location path id
    */
   public int getLocationPathId()
   {
      return mDetail.getMessageReceiverPathId();
   }
}