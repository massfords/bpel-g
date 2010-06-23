//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/reply/AeDurableReplyInfo.java,v 1.1 2006/05/24 23:07:01 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.reply;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic implementation of a durable reply info.
 */
public class AeDurableReplyInfo implements IAeDurableReplyInfo
{
   /** Durable reply type. */
   private String mType;
   /** Map containing reply information. */
   private Map mProperties;
   
   /**
    * Constructs a reply info for the given type and properties.
    * @param aType durable reply type.
    * @param aProperties reply properties.
    */
   public AeDurableReplyInfo(String aType, Map aProperties)
   {
      mType = aType;
      mProperties = aProperties;
   }
   
   /**
    * Constructs a reply info for the given type..
    * @param aType durable reply type.
    */
   public AeDurableReplyInfo(String aType)
   {
      this(aType, null);
   }
   
   /** 
    * Overrides method to return reply type. 
    * @see org.activebpel.rt.bpel.impl.reply.IAeDurableReplyInfo#getType()
    */
   public String getType()
   {
      return mType;
   }

   /** 
    * Overrides method to reply context properties. 
    * @see org.activebpel.rt.bpel.impl.reply.IAeDurableReplyInfo#getProperties()
    */
   public Map getProperties()
   {
      if (mProperties == null)
      {
         mProperties = new HashMap();
      }
      return mProperties;
   }

}
