// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/addressing/AeEndpointReferenceSourceType.java,v 1.5 2005/06/22 16:53:56 MFord Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.addressing;

import org.activebpel.rt.bpel.server.AeMessages;

/**
 * Enumerated constant type for address sources.  
 */
public class AeEndpointReferenceSourceType
{
   /** the deployment file contains the endpoint reference */
   public static final AeEndpointReferenceSourceType STATIC = new AeEndpointReferenceSourceType("static"); //$NON-NLS-1$
   /** the process will assign the endpoint information dynamically during its normal execution */
   public static final AeEndpointReferenceSourceType DYNAMIC = new AeEndpointReferenceSourceType("dynamic"); //$NON-NLS-1$
   /** the partner provisioning layer will have the endpoint mapped to the authenticated principal */
   public static final AeEndpointReferenceSourceType PRINCIPAL = new AeEndpointReferenceSourceType("principal"); //$NON-NLS-1$
   /** the metadata contained within the invocation request contains the endpoint information */
   public static final AeEndpointReferenceSourceType INVOKER = new AeEndpointReferenceSourceType("invoker"); //$NON-NLS-1$
   
   /** name of the type, useful for converting from a String */
   private String mName;
   
   /**
    * Private ctor to prevent external instantiation
    * @param aName
    */
   private AeEndpointReferenceSourceType(String aName)
   {
      mName = aName;
   }
   
   /**
    * Getter for the name
    */
   public String getName()
   {
      return mName;
   }
   
   /**
    * Factory method for getting the type by its name.
    * @param aName
    */
   public static AeEndpointReferenceSourceType getByName(String aName)
   {
      if (STATIC.getName().equals(aName))
         return STATIC;
      if (DYNAMIC.getName().equals(aName))
         return DYNAMIC;
      if (PRINCIPAL.getName().equals(aName))
         return PRINCIPAL;
      if (INVOKER.getName().equals(aName))
         return INVOKER;
         
      throw new IllegalArgumentException(AeMessages.format("AeEndpointReferenceSourceType.ERROR_4", aName)); //$NON-NLS-1$
   }
}
