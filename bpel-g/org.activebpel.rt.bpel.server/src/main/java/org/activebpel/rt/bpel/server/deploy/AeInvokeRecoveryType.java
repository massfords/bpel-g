// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeInvokeRecoveryType.java,v 1.2 2007/09/28 19:48:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration class for invoke recovery types.
 */
public class AeInvokeRecoveryType
{
   /** Maps type names to type instances. */
   private static final Map sTypes = new HashMap();

   /** Defer to engine configuration setting for invoke recovery. */
   public static final AeInvokeRecoveryType ENGINE  = new AeInvokeRecoveryType(AeDeployConstants.INVOKE_RECOVERY_TYPE_ENGINE);

   /** Recover invokes normally. */
   public static final AeInvokeRecoveryType NORMAL  = new AeInvokeRecoveryType(AeDeployConstants.INVOKE_RECOVERY_TYPE_NORMAL);
   
   /** Always suspend on invoke recovery. */
   public static final AeInvokeRecoveryType SUSPEND = new AeInvokeRecoveryType(AeDeployConstants.INVOKE_RECOVERY_TYPE_SUSPEND);

   /** Name of the class sans package names for {@link #toString()}. */
   private static String sBareClassName;

   /** Invoke recovery type name. */
   private final String mName;
   
   /**
    * Constructor.
    *
    * @param aName
    */
   private AeInvokeRecoveryType(String aName)
   {
      mName = aName;
      sTypes.put(aName, this);
   }

   /**
    * @return invoke recovery type name
    */
   public String getName()
   {
      return mName;
   }
   
   /**
    * @return name of the {@link AeInvokeRecoveryType} class sans package names
    */
   private static String getBareClassName()
   {
      if (sBareClassName == null)
      {
         String className = AeInvokeRecoveryType.class.getName();
         int i = className.lastIndexOf('.');
         sBareClassName = className.substring(i + 1);
      }
      
      return sBareClassName;
   }
   
   /**
    * Return the {@link AeInvokeRecoveryType} object corresponding to
    * the given name or {@link AeInvokeRecoveryType#ENGINE} if there is
    * no type for the given name.
    *
    * @param aName
    */
   public static AeInvokeRecoveryType getType(String aName)
   {
      AeInvokeRecoveryType result = (AeInvokeRecoveryType) sTypes.get(aName);
      if (result == null)
      {
         result = ENGINE;
      }

      return result;
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return getBareClassName() + '.' + getName(); 
   }
}
