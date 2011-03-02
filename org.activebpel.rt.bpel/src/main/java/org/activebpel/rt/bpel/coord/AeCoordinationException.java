//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/coord/AeCoordinationException.java,v 1.1 2005/10/28 21:07:19 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.coord;

import org.activebpel.rt.AeException;

/**
 * Base class for coordination related exceptions.
 */
public class AeCoordinationException extends AeException
{

   /**
    * Default constructor.
    */
   public AeCoordinationException()
   {
      super();
   }

   /**
    * Creates an exception given the message.
    * @param aInfo message
    */
   public AeCoordinationException(String aInfo)
   {
      super(aInfo);
   }

   /**
    * Creates an exception given the root cause.
    * @param aRootCause 
    */
   public AeCoordinationException(Throwable aRootCause)
   {
      super(aRootCause);
   }

   /**
    * Creates an exception given the root cause and the message.
    * @param aInfo
    * @param aRootCause
    */
   public AeCoordinationException(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
   }

}
