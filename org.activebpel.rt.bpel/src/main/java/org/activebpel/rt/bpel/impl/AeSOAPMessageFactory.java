// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeSOAPMessageFactory.java,v 1.1 2006/09/19 20:16:40 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import javax.xml.soap.MessageFactory;

/**
 * Provides access to the global SOAP message factory.
 */
public class AeSOAPMessageFactory
{
   /** SOAP Message factory */
   private static MessageFactory sMessageFactory;

   /**
    * Returns the SOAP message factory.
    */
   public static MessageFactory getSOAPMessageFactory()
   {
      return sMessageFactory;
   }

   /**
    * Sets the SOAP message factory.
    */
   public static void setSOAPMessageFactory(MessageFactory aMessageFactory)
   {
      sMessageFactory = aMessageFactory;
   }
}
