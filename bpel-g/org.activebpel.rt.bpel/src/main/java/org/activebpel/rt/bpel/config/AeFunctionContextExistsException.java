//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/config/AeFunctionContextExistsException.java,v 1.1 2007/05/04 18:35:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.config;

import org.activebpel.rt.AeException;

/**
 * Generated if there is an existing exception with a matching name, prefix or namespace.
 */
public class AeFunctionContextExistsException extends AeException
{
   // error type constants
   public static final int DUPLICATE_NAME = 1;
   public static final int DUPLICATE_PREFIX_OR_NAMESPACE = 2;
   
   /** error type */
   protected int mType;
   
   /**
    * Constructor.
    */
   public AeFunctionContextExistsException( int aType )
   {
      super();
      mType = aType;
   }
   
   /**
    * Return true of the exception was triggered because of a duplicate name.
    */
   public boolean isDuplicateName()
   {
      return mType == DUPLICATE_NAME;
   }
   
   /**
    * Return true if the exception was triggered because of a duplicate namespace or prefix.
    */
   public boolean isDuplicatePrefixOrNamespace()
   {
      return mType == DUPLICATE_PREFIX_OR_NAMESPACE;
   }
}
