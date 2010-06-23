// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeProcessTransactionType.java,v 1.3 2006/06/13 21:06:23 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration class for process transaction type.
 */
public class AeProcessTransactionType
{
   public static final AeProcessTransactionType BEAN      = new AeProcessTransactionType(AeDeployConstants.TRANSACTION_TYPE_BEAN);
   public static final AeProcessTransactionType CONTAINER = new AeProcessTransactionType(AeDeployConstants.TRANSACTION_TYPE_CONTAINER);

   /** Array of all transaction types. */
   private static final AeProcessTransactionType[] sTypes = new AeProcessTransactionType[] { BEAN, CONTAINER };

   /** Maps type names to type instances. */
   private static Map sTypesMap;

   /** Name of transaction type. */
   private final String mName;
   
   /**
    * Constructs transaction type instance from name.
    *
    * @param aName
    */
   protected AeProcessTransactionType(String aName)
   {
      mName = aName;
   }
   
   /**
    * Returns transaction type instance for specified name.
    * If no matching type is found, returns the default transaction type (bean).
    */
   public static AeProcessTransactionType getTransactionType(String aName)
   {
      AeProcessTransactionType result = (AeProcessTransactionType) getTypesMap().get(aName);
      
      if (result == null)
      {
         result = BEAN;
      }

      return result;
   }
   
   
   /** 
    * Overrides method to compare the type name. 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aOther)
   {
      if (aOther instanceof AeProcessTransactionType)
      {
         return getName().equalsIgnoreCase(  ((AeProcessTransactionType) aOther).getName() );
      }
      else
      {
         return false;
      }
   }
   
   /**
    * Returns name of transaction type.
    */
   protected String getName()
   {
      return mName;
   }

   /**
    * Returns map from type names to type instances.
    */
   protected static Map getTypesMap()
   {
      if (sTypesMap == null)
      {
         Map map = new HashMap();

         for (int i = 0; i < sTypes.length; ++i)
         {
            map.put(sTypes[i].getName(), sTypes[i]);
         }

         sTypesMap = Collections.unmodifiableMap(map);
      }

      return sTypesMap;
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return getName();
   }
}
