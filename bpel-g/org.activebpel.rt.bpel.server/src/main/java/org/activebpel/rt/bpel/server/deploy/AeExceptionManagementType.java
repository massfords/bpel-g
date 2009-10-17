//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeExceptionManagementType.java,v 1.1 2005/08/31 22:09:34 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration class for exception management types.
 */
public class AeExceptionManagementType
{

   /** Maps type names to type instances. */
   private static Map mTypes = new HashMap();

   // type constants
   public static final AeExceptionManagementType ENGINE = new AeExceptionManagementType( AeDeployConstants.EXCEPTION_MANAGEMENT_TYPE_ENGINE );
   public static final AeExceptionManagementType SUSPEND = new AeExceptionManagementType( AeDeployConstants.EXCEPTION_MANAGEMENT_TYPE_SUSPEND );
   public static final AeExceptionManagementType NORMAL = new AeExceptionManagementType( AeDeployConstants.EXCEPTION_MANAGEMENT_TYPE_NORMAL );
   
   /** name field */
   private String mName;
   
   /**
    * Constructor.
    * @param aName
    */
   private AeExceptionManagementType( String aName )
   {
      mName = aName;
      mTypes.put( aName, this );
   }

   /**
    * @return Returns the name.
    */
   public String getName()
   {
      return mName;
   }
   
   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return getName(); 
   }
   
   /**
    * Return the <code>AeExceptionManagementType</code> object mapped to
    * the given name or the default ENGINE type if no match is found.
    * @param aName
    */
   public static AeExceptionManagementType getType( String aName )
   {
      AeExceptionManagementType type = (AeExceptionManagementType)mTypes.get( aName );
      if( type == null )
      {
         type = ENGINE;
      }
      return type;
   }
}
