//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeToExtension.java,v 1.3 2007/11/21 03:22:18 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.to; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.activity.support.IAeToSpecExtension;

/**
 * Impl object for an extension that handles copying the data.
 */
public class AeToExtension extends AeToBase
{
   /** def */
   private AeToDef mDef;
   
   /**
    * Public ctor with this signature is required since the factory uses
    * reflection to load the instances.
    * @param aToDef
    */
   public AeToExtension(AeToDef aToDef)
   {
      setDef(aToDef);
   }
   
   /**
    * Delegates the production of the target to an adapter in the extension object
    * 
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeTo#getTarget()
    */
   public Object getTarget() throws AeBusinessProcessException
   {
      AeToDef def = getDef();
      IAeToSpecExtension extension = (IAeToSpecExtension) def.getAdapterFromAttributes(IAeToSpecExtension.class);
      return extension.createCopyStrategy(getCopyOperation(), def);
   }

   /**
    * @return the def
    */
   public AeToDef getDef()
   {
      return mDef;
   }

   /**
    * @param aDef the def to set
    */
   public void setDef(AeToDef aDef)
   {
      mDef = aDef;
   }
}
 