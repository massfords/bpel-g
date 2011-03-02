//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/attachment/AeAttachmentException.java,v 1.1 2007/04/23 21:20:42 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.attachment;

import org.activebpel.rt.AeException;

/**
 * Base class for attachment related exceptions.
 */
public class AeAttachmentException extends AeException
{

   /**
    * Default constructor.
    */
   public AeAttachmentException()
   {
      super();
   }

   /**
    * Creates an exception given the message.
    * @param aInfo message
    */
   public AeAttachmentException(String aInfo)
   {
      super(aInfo);
   }

   /**
    * Creates an exception given the root cause.
    * @param aRootCause 
    */
   public AeAttachmentException(Throwable aRootCause)
   {
      super(aRootCause);
   }

   /**
    * Creates an exception given the root cause and the message.
    * @param aInfo
    * @param aRootCause
    */
   public AeAttachmentException(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
   }

}
