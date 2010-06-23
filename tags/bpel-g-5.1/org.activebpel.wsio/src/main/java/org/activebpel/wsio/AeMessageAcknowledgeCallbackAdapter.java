//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/AeMessageAcknowledgeCallbackAdapter.java,v 1.1 2008/03/28 01:36:24 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio; 

/**
 * Provides no op impls of the interface
 */
public class AeMessageAcknowledgeCallbackAdapter implements IAeMessageAcknowledgeCallback
{
   /**
    * @see org.activebpel.wsio.IAeMessageAcknowledgeCallback#onAcknowledge(java.lang.String)
    */
   public void onAcknowledge(String aMessageId)
         throws AeMessageAcknowledgeException
   {
   }

   /**
    * @see org.activebpel.wsio.IAeMessageAcknowledgeCallback#onNotAcknowledge(java.lang.Throwable)
    */
   public void onNotAcknowledge(Throwable aReason)
   {
   }
}
 