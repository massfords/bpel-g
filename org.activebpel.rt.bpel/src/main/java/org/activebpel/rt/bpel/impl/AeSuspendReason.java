// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeSuspendReason.java,v 1.4 2007/12/18 23:10:24 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.IAeVariable;

/**
 * Class used to indicate reason for a process being suspended.
 */
public class AeSuspendReason
{
   /** Process was suspended via the administrative console */
   public static final int SUSPEND_CODE_MANUAL = 0;
   /** Process was suspended due to and uncaught fault */
   public static final int SUSPEND_CODE_AUTOMATIC = 1;
   /** Process was suspended due to the suspend activity */
   public static final int SUSPEND_CODE_LOGICAL = 2;   
   /** Process was suspended for internal process migration. */
   public static final int SUSPEND_CODE_MIGRATE = 3;   
   /** Process was suspended due to a non-durable invoke pending during process recovery. */
   public static final int SUSPEND_CODE_INVOKE_RECOVERY = 4;
   /** Process was suspended due to retry policy on an invoke */
   public static final int SUSPEND_CODE_INVOKE_RETRY = 5;
   

   /** The reason the process was suspended */
   private int mReasonCode;
   /** Location path where the process was suspended */
   private String mLocationPath;
   /** Variable from suspend activity */
   private IAeVariable mVariable;
   
   /**
    * Default contructor, used for administrative suspension.
    */
   public AeSuspendReason()
   {
      mReasonCode = SUSPEND_CODE_MANUAL;
   }

   /**
    * Constructor which accepts detail information
    * @param aReasonCode the reason the process was suspened 
    * @param aLocationPath location path where process was suspended (may be null)
    * @param aVariable variable from suspend activity (may be null) 
    */
   public AeSuspendReason(int aReasonCode, String aLocationPath, IAeVariable aVariable)
   {
      mReasonCode = aReasonCode;
      mLocationPath = aLocationPath;
      mVariable = aVariable;
   }

   /**
    * Returns the locationPath, or null if none specified.
    */
   public String getLocationPath()
   {
      return mLocationPath;
   }

   /**
    * Returns the reasonCode for process suspension.
    */
   public int getReasonCode()
   {
      return mReasonCode;
   }

   /**
    * Returns the fault variable responsible for process being suspended.
    */
   public IAeVariable getVariable()
   {
      return mVariable;
   }
}