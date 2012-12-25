//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/from/AeFromExtension.java,v 1.1 2007/11/10 03:36:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.from; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.IAeFromSpecExtension;

/**
 * Impl of the from-spec that delegates the production of the from data to an
 * adapter from an extension object.
 */
public class AeFromExtension extends AeFromBase
{
   /** def */
   private AeFromDef mDef;

   /**
    * Public ctor with this singature is required since the object is constructed
    * via reflection.
    * @param aFromDef
    */
   public AeFromExtension(AeFromDef aFromDef)
   {
      setDef(aFromDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeFrom#getFromData()
    */
   public Object getFromData() throws AeBusinessProcessException
   {
      IAeFromSpecExtension extension = (IAeFromSpecExtension) getDef().getAdapterFromAttributes(IAeFromSpecExtension.class);
      return extension.executeFromSpec(getCopyOperation(), getDef());
   }

   /**
    * @return the def
    */
   public AeFromDef getDef()
   {
      return mDef;
   }

   /**
    * @param aDef the def to set
    */
   public void setDef(AeFromDef aDef)
   {
      mDef = aDef;
   }
}
 