//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeCatalogItemDetail.java,v 1.3 2006/09/07 15:06:26 EWittmann Exp $
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
 * Essentially a struct that wraps the detail information
 * for a single catalog resource deployment.  Information includes the
 * location hint, the target namespace, the actual resource (e.g. wsdl)
 * (as a string), and an array of <code>AeCatalogItemPlanReference</code>
 * objects that represent the process plans that utilize this resource.
 */
public class AeCatalogItemDetail extends AeCatalogItem
{
   /** The catalog item resource as a string. */
   private String mText;
   /** Array of plans that point to this resource. */
   private AeCatalogItemPlanReference[] mPlanReferences;

   /**
    * Constructor.
    *
    * @param aLocation
    * @param aTypeURI
    * @param aTargetNamespace
    * @param aText
    * @param aPlanListing
    */
   public AeCatalogItemDetail(String aLocation, String aTypeURI, String aTargetNamespace, String aText, AeCatalogItemPlanReference[] aPlanListing)
   {
      super(aLocation, aTargetNamespace, aTypeURI);
      mText = aText;
      mPlanReferences = aPlanListing;
   }

   /**
    * @return Returns the wsdl xml as a string.
    */
   public String getText()
   {
      return mText;
   }

   /**
    * @return Returns the array of <code>AeCatalogItemPlanReference</code> objects.
    */
   public AeCatalogItemPlanReference[] getPlanReferences()
   {
      return mPlanReferences;
   }
}
