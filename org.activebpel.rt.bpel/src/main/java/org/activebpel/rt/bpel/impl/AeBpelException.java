// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeBpelException.java,v 1.7 2006/06/26 16:50:29 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.util.AeUtil;

/**
 * An exception which is created as a result of a BPEL execution failure.
 */
public class AeBpelException extends AeBusinessProcessException
{
   /** The BPEL fault which was created */
   private IAeFault mFault;
   
   /**
    * Constructs a new BPEL exception with the given info and fault.
    * @param aInfo informational message  
    * @param aFault the BPEL fault responsible for this exception
    */
   public AeBpelException(String aInfo, IAeFault aFault)
   {
      super(aInfo);
      
      mFault = aFault;
      mFault.setInfo( aInfo );
   }
   
   /**
    * Constructs a new BPEL exception with the info, fault, and a throwable
    * who's stacktrace will be included in the fault.
    * 
    * @param aInfo
    * @param aFault
    * @param aThrowable
    */
   public AeBpelException(String aInfo, IAeFault aFault, Throwable aThrowable)
   {
      super(aInfo);
      mFault = aFault;
      mFault.setInfo(aInfo);
      if (aThrowable != null)
         mFault.setDetailedInfo(AeUtil.getStacktrace(aThrowable));
   }

   /**
    * Returns the BPEL fault which was the cause of this exception.
    */
   public IAeFault getFault()
   {
      return mFault;
   }
}
