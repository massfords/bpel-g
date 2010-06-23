// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/AeMessageDataFactory.java,v 1.3 2007/06/10 19:07:57 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.message.AeMessageData;
import org.activebpel.rt.message.IAeMessageData;

/**
 * The factory for creating message data implementations which contain the data
 * for variables in a bpel process.
 */
public class AeMessageDataFactory
{
   /** The singleton instance of the factory */
   private static AeMessageDataFactory mFactory = new AeMessageDataFactory();

   /**
    * Private constructor to force singleton.
    */
   private AeMessageDataFactory()
   {
   }

   /**
    * Returns the singleton instance of the Win32 Service factory.
    */
   public static AeMessageDataFactory instance()
   {
      return mFactory;
   }

   /**
    * Creates a message to be used during BPEL process execution.
    * @param aMsgName The qualified name of the message we are creating
    */
   public IAeMessageData createMessageData(QName aMsgName)
   {
      return new AeMessageData(aMsgName);
   }
   
   /**
    * Create a message with the given QName and message data.
    * @param aMsgName
    * @param aMessageData
    */
   public IAeMessageData createMessageData( QName aMsgName, Map aMessageData )
   {
      return new AeMessageData( aMsgName, aMessageData );
   }
}
