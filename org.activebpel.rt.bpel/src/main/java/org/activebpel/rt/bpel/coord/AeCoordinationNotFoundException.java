//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/coord/AeCoordinationNotFoundException.java,v 1.2 2006/06/26 16:50:49 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.coord;

/**
 * Exception to indicate a coordination or context was not found in the server.
 */
public class AeCoordinationNotFoundException extends AeCoordinationException
{

   /**
    * Coordination lookup id or key..
    */
   private String mCoordinationIdKey;

   /**
    * Default ctor
    */
   public AeCoordinationNotFoundException()
   {
   }

   /**
    * @param aCoordinationIdKey
    */
   public AeCoordinationNotFoundException(String aCoordinationIdKey)
   {
      setCoordinationIdKey(aCoordinationIdKey);
   }

   /**
    * @param aRootCause
    */
   public AeCoordinationNotFoundException(Throwable aRootCause)
   {
      this("", aRootCause);//$NON-NLS-1$
   }

   /**
    * @param aCoordinationIdKey
    * @param aRootCause
    */
   public AeCoordinationNotFoundException(String aCoordinationIdKey, Throwable aRootCause)
   {
      super(aRootCause);
      setCoordinationIdKey(aCoordinationIdKey);
   }

   /**
    * @return Returns the coordinationIdKey.
    */
   public String getCoordinationIdKey()
   {
      return mCoordinationIdKey;
   }

   /**
    * @param aCoordinationIdKey The coordinationIdKey to set.
    */
   public void setCoordinationIdKey(String aCoordinationIdKey)
   {
      mCoordinationIdKey = aCoordinationIdKey;
   }
}
