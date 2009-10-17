//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/reply/AeDurableReplyFactory.java,v 1.4.4.1 2008/04/21 16:12:44 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.reply;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.reply.AeMissingReplyReceiver;
import org.activebpel.rt.bpel.impl.reply.IAeDurableReplyFactory;
import org.activebpel.rt.bpel.impl.reply.IAeDurableReplyInfo;
import org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver;
import org.activebpel.rt.bpel.impl.reply.IAeReplyReceiverFactory;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;

/**
 * Default implementation of the durable reply factory.
 */
public class AeDurableReplyFactory implements IAeDurableReplyFactory
{   
   /**
    * Map containing <code>IAeReplyReceiver</code> factories, keyed by the factory prototype.
    */
   private Map replyFactoryMap;
   
   public AeDurableReplyFactory(Map aConfig) throws AeException
   {    
      replyFactoryMap = new HashMap();
      init(aConfig);
   }
   
   /**
    * Creates factory objects based on the configuration.
    * @param aConfig
    * @throws AeException
    */
   protected void init(Map aConfig) throws AeException
   {
      Iterator it = aConfig.keySet().iterator();
      while (it.hasNext() )
      {
         String protoType = (String) it.next(); 
         if (AeUtil.notNullOrEmpty(protoType))
         {
            Map factoryConfig = (Map) aConfig.get(protoType);
            IAeReplyReceiverFactory factory = (IAeReplyReceiverFactory) AeEngineFactory.createConfigSpecificClass(factoryConfig);
            getReplyFactoryMap().put( protoType.toLowerCase().trim(), factory) ;
         }
      }      
   }
     
   /**
    * @return Returns the replyFactoryMap.
    */
   protected Map getReplyFactoryMap()
   {
      return replyFactoryMap;
   }
   
   /**
    * Returns a <code>IAeReplyReceiverFactory</code> given the type.
    * @param aProtoType
    *
    */
   protected IAeReplyReceiverFactory getFactory(String aProtoType)
   {
      IAeReplyReceiverFactory rVal = null;
      if ( AeUtil.notNullOrEmpty( aProtoType ) )
      {
         rVal = (IAeReplyReceiverFactory) getReplyFactoryMap().get( aProtoType.toLowerCase().trim() );
      }
      return rVal;
   }

   /**  
    * Overrides method to method to create appropriate reply receiver.
    * Returns an AeMissingReplyReceiver if the reply type is not supported. 
    * @see org.activebpel.rt.bpel.impl.reply.IAeDurableReplyFactory#createReplyReceiver(long, org.activebpel.rt.bpel.impl.reply.IAeDurableReplyInfo)
    */
   public IAeReplyReceiver createReplyReceiver(long aReplyId, IAeDurableReplyInfo aInfo)
         throws AeBusinessProcessException
   {      
      IAeReplyReceiverFactory factory = null;
      if (aInfo != null && AeUtil.notNullOrEmpty(aInfo.getType()) )
      {
         factory = getFactory( aInfo.getType() );
      }      
      if (factory != null)
      {
         try
         {
            return factory.createReplyReceiver(aInfo.getProperties());
         }
         catch(AeException e)
         {
            throw new AeBusinessProcessException(e.getLocalizedMessage(), e);
         }
      }
      else
      {
         return createMissingReplyReceiver(aReplyId);
      }
   }

   /** 
    * Overrides method to AeMissingReplyReceiverInstance.
    * @see org.activebpel.rt.bpel.impl.reply.IAeDurableReplyFactory#createMissingReplyReceiver(long)
    */
   public IAeReplyReceiver createMissingReplyReceiver(long aReplyId)
   {
      return new AeMissingReplyReceiver(aReplyId);
   }
}
