//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/AeCopyOperationComponentFactory.java,v 1.5.4.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign; 

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.activity.support.AeVarDef;
import org.activebpel.rt.bpel.def.io.readers.def.AeSpecStrategyKey;

/**
 * Base class for creating copy operation impl objects like specific
 * versions of a &lt;from&gt; or &lt;to&gt; variant
 */
public abstract class AeCopyOperationComponentFactory
{
   /**  */
   private Map mMap = new HashMap();

   /**
    * Inits the map
    */
   protected AeCopyOperationComponentFactory()
   {
      initMap();
   }

   /**
    * The map of strategy names to Class is constructed here
    */
   protected abstract void initMap();

   /**
    * Getter for the map
    */
   protected Map getMap()
   {
      return mMap;
   }

   /**
    * Factory method does a new instance on the class, passing the def in as a param
    * @param aDef
    */
   protected Object create(AeVarDef aDef)
   {
      AeSpecStrategyKey strategy = aDef.getStrategyKey();
      String strategyName = strategy.getStrategyName();
      Class clazz = (Class) getMap().get(strategyName);
      try
      {
         if (strategy.hasArguments())
         {
            Object [] arguments = strategy.getStrategyArguments();
            Class [] classes = new Class[arguments.length];
            for (int i = 0; i < arguments.length; i++)
               classes[i] = arguments[i].getClass();

            Constructor cons = clazz.getConstructor(classes);
            return cons.newInstance(arguments);
         }
         else
         {
            Constructor cons = clazz.getConstructor(new Class[] { aDef.getClass() });
            return cons.newInstance(new Object[] { aDef });
         }
      }
      catch (Throwable t)
      {
         Object[] args = {strategyName, aDef.getLocationPath()};
         String message = AeMessages.format("AeCopyOperationComponentFactory.ErrorCreatingStrategy", args); //$NON-NLS-1$
         AeException.logError(t, message);
         throw new InternalError(message);
      }
   }

}
 