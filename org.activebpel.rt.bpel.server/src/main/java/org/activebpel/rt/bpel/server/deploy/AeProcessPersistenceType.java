// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeProcessPersistenceType.java,v 1.6 2006/06/13 21:06:23 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration class for process persistence type.
 */
public class AeProcessPersistenceType
{
   public static final AeProcessPersistenceType FULL  = new AeProcessPersistenceType(AeDeployConstants.PERSISTENCE_TYPE_FULL);
   public static final AeProcessPersistenceType NONE  = new AeProcessPersistenceType(AeDeployConstants.PERSISTENCE_TYPE_NONE);

   /** Array of all persistence types. */
   private static final AeProcessPersistenceType[] sTypes = new AeProcessPersistenceType[] { FULL, NONE };

   /** Maps type names to type instances. */
   private static Map sTypesMap;

   /** Name of persistence type. */
   private final String mName;
   
   /**
    * Constructs persistence type instance from name.
    *
    * @param aName
    */
   protected AeProcessPersistenceType(String aName)
   {
      mName = aName;
   }
   
   /**
    * Returns persistence type instance for specified name.
    * If no matching type is found, returns the default persistence type (full).
    */
   public static AeProcessPersistenceType getPersistenceType(String aName)
   {
      // KR removed "final" as a persistence type due to refactoring of persistence layer
      // to include journaling and more efficient writes.
      // We will treat any "final" persistence types as "full"
      AeProcessPersistenceType result = (AeProcessPersistenceType) getTypesMap().get(aName);
      
      if (result == null)
      {
         result = FULL;
      }

      return result;
   }
   
   /**
    * Returns name of persistence type.
    */
   public String getName()
   {
      return mName;
   }
   
   /** 
    * Overrides method to compare the type name. 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aOther)
   {
      if (aOther instanceof AeProcessPersistenceType)
      {
         return getName().equalsIgnoreCase(  ((AeProcessPersistenceType) aOther).getName() );
      }
      else
      {
         return false;
      }
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
