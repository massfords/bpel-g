//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/transreceive/AeNoopTransmissionTracker.java,v 1.5 2008/03/11 03:09:29 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.transreceive;

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.impl.reply.IAeDurableReplyFactory;
import org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker;
import org.activebpel.rt.bpel.server.engine.reply.AeDurableReplyFactory;
import org.activebpel.rt.util.AeLongSet;
import org.activebpel.wsio.invoke.IAeTransmission;

/**
 * Transmissiond Id tracker used in the in memory configuration.
 */
public class AeNoopTransmissionTracker implements IAeTransmissionTracker
{
   /**  Durable reply factory. */
   private IAeDurableReplyFactory mDurableReplyFactory;
   
   /**
    * Default ctor.
    * @param aConfig
    * @throws AeException
    */
   public AeNoopTransmissionTracker(Map aConfig) throws AeException
   {
      init(aConfig);
   }

   /**
    * Initialize the <code>IAeDurableReplyFactory</code> instance.
    * @param aConfig
    * @throws AeException
    */
   protected void init( Map aConfig ) throws AeException
   {
      Map factoryListConfig = (Map) aConfig.get( IAeEngineConfiguration.DURABLE_REPLY_FACTORIES );
      setDurableReplyFactory(new AeDurableReplyFactory(factoryListConfig));
   }  
   
   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#getDurableReplyFactory()
    */
   public IAeDurableReplyFactory getDurableReplyFactory()
   {
      return mDurableReplyFactory;
   }

   /** 
    * Overrides method to return 1 
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#getNextId()
    */
   public long getNextId()
   {
      return 1;
   }

   /**
    *  
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#add(long, java.lang.String, int)
    */
   public void add(long aTransmissionId, String aMessageId, int aState) throws AeException
   {
   }

   /**
    * 
    * Overrides method to return false.
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#exists(long)
    */
   public boolean exists(long aTxId) throws AeException
   {
      return false;
   }

   /** 
    * Overrides method to return false. 
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#exists(long, int)
    */
   public boolean exists(long aTxId, int aState) throws AeException
   {
      return false;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#update(long, int)
    */
   public void update(long aTxId, int aState) throws AeException
   {
   }

   /** 
    * Overrides method to return NULL_STATE. 
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#getState(long)
    */
   public int getState(long aTxId) throws AeException
   {
      return NULL_STATE;
   }

   /** 
    * Overrides method to return null. 
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#getMessageId(long)
    */
   public String getMessageId(long aTxId) throws AeException
   {
      return null;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#remove(long)
    */
   public void remove(long aTxId) throws AeException
   {
   }

   /** 
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#remove(org.activebpel.rt.util.AeLongSet)
    */
   public void remove(AeLongSet aTransmissionIds) throws AeException
   {      
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#isTransmitted(long)
    */
   public boolean isTransmitted(long aTxId) throws AeException
   {
      return false;
   }


   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#assignTransmissionId(org.activebpel.wsio.invoke.IAeTransmission, long, int)
    */
   public void assignTransmissionId(IAeTransmission aTransmission,
         long aProcessId, int aLocationId) throws AeException
   {
   }

   /**
    * @param aDurableReplyFactory the durableReplyFactory to set
    */
   protected void setDurableReplyFactory(IAeDurableReplyFactory aDurableReplyFactory)
   {
      mDurableReplyFactory = aDurableReplyFactory;
   }
}
