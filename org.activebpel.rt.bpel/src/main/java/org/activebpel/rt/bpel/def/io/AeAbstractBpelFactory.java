// $Header$
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.io;

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.xml.def.io.IAeDefRegistry;

/**
 * A base class for the BPEL factories.
 */
public abstract class AeAbstractBpelFactory implements IAeBpelFactory
{
   /** bpel reader/writer registry */
   private IAeDefRegistry mBpelRegistry;
   
   /** factory's features map. */
   private Map mFeatures = new HashMap();

   /**
    * Default c'tor.
    */
   public AeAbstractBpelFactory()
   {
      super();
   }

   /**
    * Setter for the registry
    * @param aRegistry
    */
   protected void setBpelRegistry(IAeDefRegistry aRegistry)
   {
      mBpelRegistry = aRegistry;
   }

   /**
    * Accessor for the IAeBpelRegistry impl. If none has been set, a new <code>AeDefaultBpelRegistry</code>
    * is created and set as the member registry. <br />
    * This registry will be installed in all readers and writers.
    * 
    * @return IAeBpelRegistry impl
    */
   public IAeDefRegistry getBpelRegistry()
   {
      if (mBpelRegistry == null)
      {
         mBpelRegistry = createBpelRegistry();
      }
      return mBpelRegistry;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.IAeBpelFactory#setFeature(java.lang.String, boolean)
    */
   public void setFeature(String aName, boolean aFlag)
   {
      getFeatures().put(aName, new Boolean(aFlag));
   }
   
   /**
    * @return the factory's features map.
    */
   protected Map getFeatures()
   {
      return mFeatures;
   }

   /**
    * Creates the BPEL registry.
    */
   protected abstract IAeDefRegistry createBpelRegistry();
}
