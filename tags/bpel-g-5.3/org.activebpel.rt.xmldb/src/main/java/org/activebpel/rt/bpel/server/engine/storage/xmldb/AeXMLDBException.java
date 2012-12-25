//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeXMLDBException.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;

/**
 * An exception that is thrown when an error occurs in the XMLDB layer.
 */
public class AeXMLDBException extends AeStorageException
{

   /**
    * Construct a new XMLDB exception with the passed info string.
    */
   public AeXMLDBException(String aInfo)
   {
      super(aInfo);
      setInfo(aInfo);
   }

   /**
    * Construct with a root exception (used primarily for rethrowing an underlying exception).
    * 
    * @param aRootCause
    */
   public AeXMLDBException(Throwable aRootCause)
   {
      super(aRootCause);
      setRootCause(aRootCause);
      setInfo(aRootCause.getLocalizedMessage());
   }

   /**
    * Construct with a root exception (used prinarily for rethrowing an underlying exception).
    * 
    * @param aInfo Informational message for the exception
    * @param aRootCause Root cause of the exception
    */
   public AeXMLDBException(String aInfo, Throwable aRootCause)
   {
      super(aInfo, aRootCause);
      setRootCause(aRootCause);
   }
}
