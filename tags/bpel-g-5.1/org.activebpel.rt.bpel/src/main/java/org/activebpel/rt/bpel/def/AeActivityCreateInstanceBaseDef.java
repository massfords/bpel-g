//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/AeActivityCreateInstanceBaseDef.java,v 1.2 2006/06/26 16:50:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

/**
 * Base definition class for activities which is the createInstance attribute.
 */
public abstract class AeActivityCreateInstanceBaseDef extends AeActivityPartnerLinkBaseDef implements IAeActivityCreateInstanceDef
{
   /**
    * createInstance attribute.
    */
   private boolean mCreateInstance;

   /**
    * Default c'tor.
    */
   public AeActivityCreateInstanceBaseDef()
   {
      super();
   }

   /**
    * Accessor method to obtain the create instance flag.
    */
   public final boolean isCreateInstance()
   {
      return mCreateInstance;
   }

   /**
    * Mutator method to set the create instance flag for the activity.
    * 
    * @param aCreateInstance boolean flag indicating if instance should be 
    *        created for activity
    */
   public final void setCreateInstance(boolean aCreateInstance)
   {
      mCreateInstance = aCreateInstance;
   }   
}
