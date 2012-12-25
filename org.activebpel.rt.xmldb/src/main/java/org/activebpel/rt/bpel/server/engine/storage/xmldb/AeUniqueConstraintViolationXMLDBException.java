// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeUniqueConstraintViolationXMLDBException.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb;


/**
 * A special exception that will get thrown when there is a XMLDB unique
 * constraint violation.
 */
public class AeUniqueConstraintViolationXMLDBException extends AeXMLDBException
{
   /**
    * Construct with a root exception (used primarily for rethrowing an underlying exception).
    * 
    * @param aRootCause
    */
   public AeUniqueConstraintViolationXMLDBException(Throwable aRootCause)
   {
      super(aRootCause);
      setRootCause(aRootCause);
      setInfo(aRootCause.getLocalizedMessage());
   }
}
