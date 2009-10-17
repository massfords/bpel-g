//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeCatalogListingFilter.java,v 1.2 2006/08/16 14:15:07 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.list;

/**
 * Wsdl catalog filter. Current impl only controls row offset and number of rows
 * displayed.
 */
public class AeCatalogListingFilter extends AeListingFilter
{
   /** The type uri to select which is null or empty if none selected. */
   private String mTypeURI;
   
   /** The resource name to filter on, may contain asterisk as wild card. */
   private String mResource;
   
   /** The namespace to filter on, may contain asterisk as wild card. */
   private String mNamespace;
   
   /**
    * Constructor.
    */
   public AeCatalogListingFilter()
   {
      super();
   }

   /**
    * @return Returns the typeURI, which is null or empty if none selected for fuiltering.
    */
   public String getTypeURI()
   {
      return mTypeURI;
   }

   /**
    * @param aTypeURI The typeURI to set.
    */
   public void setTypeURI(String aTypeURI)
   {
      mTypeURI = aTypeURI;
   }

   /**
    * @return Returns the resource.
    */
   public String getResource()
   {
      return mResource;
   }

   /**
    * @param aResource The resource to set.
    */
   public void setResource(String aResource)
   {
      mResource = aResource;
   }

   /**
    * @return Returns the namespace.
    */
   public String getNamespace()
   {
      return mNamespace;
   }

   /**
    * @param aNamespace The namespace to set.
    */
   public void setNamespace(String aNamespace)
   {
      mNamespace = aNamespace;
   }
}