//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/resource/AeResourceException.java,v 1.2 2008/02/17 21:38:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog.resource;

import org.activebpel.rt.AeException;

/**
 * This class wrappers exceptions getting resource from cache.
 */
public class AeResourceException extends AeException
{
   /**
    * Construct a resource excetpion with the passed info.
    * @param aInfo
    */
   public AeResourceException(String aInfo)
   {
      super(aInfo);
   }

   /**
    * Construct a resource excetpion with the passed cause.
    * @param aRootCause
    */
   public AeResourceException(Throwable aRootCause)
   {
      super(aRootCause);
   }

   /**
    * Construct a resource excetpion with the passed info and cause.
    * @param aInfo
    */
   public AeResourceException(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
   }

}
