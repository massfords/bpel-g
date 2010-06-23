// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/AeCatalogEvent.java,v 1.1 2006/07/18 20:05:33 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog;

/**
 *  Provides catalog resource notification information.
 */
public class AeCatalogEvent
{
   /** the location hint */
   private String mLocationHint;
   /** the replace boolean value */
   private boolean mReplacementFlag;
   
   /**
    * Factory create method.
    * @param aLocationHint
    * @param aIsReplacement
    */
   public static AeCatalogEvent create( String aLocationHint, boolean aIsReplacement )
   {
      return new AeCatalogEvent( aLocationHint, aIsReplacement );
   }

   /**
    * Constructor.
    * @param aLocationHint
    * @param aIsReplacement
    */
   protected AeCatalogEvent( String aLocationHint, boolean aIsReplacement )
   {
      mLocationHint = aLocationHint;
      mReplacementFlag = aIsReplacement;
   }
   
   /**
    * Accessor for location hint.
    */
   public String getLocationHint()
   {
      return mLocationHint;
   }
   
   /**
    * @return Returns true if this is a wsdl replacement.
    */
   public boolean isReplacement()
   {
      return mReplacementFlag;
   }
}
