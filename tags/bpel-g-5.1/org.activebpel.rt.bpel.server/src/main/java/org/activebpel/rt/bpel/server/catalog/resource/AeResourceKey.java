// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/resource/AeResourceKey.java,v 1.3 2007/09/12 12:58:12 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog.resource;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.def.IAeBPELExtendedWSDLConst;

/**
 * Key for a resource reference stored in the cache.
 */
public class AeResourceKey implements IAeResourceKey
{
   /** the resource location */
   private final String mLocation;
   /** the resource type */
   private final String mTypeURI;

   /**
    * Constructor.
    */
   public AeResourceKey( String aLocation, String aType )
   {
      mLocation = aLocation;
      mTypeURI = aType;
   }

   /**
    * @see org.activebpel.rt.bpel.server.catalog.resource.IAeResourceKey#getLocation()
    */
   public String getLocation()
   {
      return mLocation;
   }

   /**
    * @see org.activebpel.rt.bpel.server.catalog.resource.IAeResourceKey#getTypeURI()
    */
   public String getTypeURI()
   {
      return mTypeURI;
   }

   /**
    * Return true if this is a wsdl entry.
    */
   public boolean isWsdlEntry()
   {
      return IAeBPELExtendedWSDLConst.WSDL_NAMESPACE.equals(getTypeURI());
   }

   /**
    * Return true if this is a schema entry.
    */
   public boolean isSchemaEntry()
   {
      return IAeConstants.W3C_XML_SCHEMA.equals(getTypeURI());
   }

   /**
    * Returns true if this is an xsl entry.
    */
   public boolean isXslEntry()
   {
      return IAeBPELConstants.XSL_NAMESPACE.equals(getTypeURI());
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals( Object aObject )
   {
      if( aObject != null && aObject instanceof AeResourceKey )
      {
         IAeResourceKey other = (IAeResourceKey)aObject;
         return AeUtil.compareObjects(getLocation(), other.getLocation()) &&
                AeUtil.compareObjects(getTypeURI(), other.getTypeURI());
      }
      return false;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return AeUtil.getSafeString(getLocation()).hashCode() +  (AeUtil.getSafeString(getTypeURI()).hashCode() << 1);
   }
}
