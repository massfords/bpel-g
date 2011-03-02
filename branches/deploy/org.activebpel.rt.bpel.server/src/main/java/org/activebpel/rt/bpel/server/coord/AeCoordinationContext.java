//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/AeCoordinationContext.java,v 1.3 2006/05/24 23:16:35 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord;

import java.util.Iterator;
import java.util.Properties;

import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinationContext;

/**
 * Simple imlementation of a coordination context.
 */
public class AeCoordinationContext extends AeContextBase implements IAeCoordinationContext
{

   /**
    * Coordination instance id.
    */
   private IAeCoordinationId mCoordinationId;
   
   /**
    * Constructs a coordination context given the id.
    */
   public AeCoordinationContext(IAeCoordinationId aCoordinationId)
   {
      super();
      mCoordinationId = aCoordinationId;
   }
   
   /**
    * Sets given properties by copying the argument content to an internal Properties object.
    * @param aProperties
    */
   public void setProperties(Properties aProperties)
   {
      if (aProperties != null)
      {
         Iterator keyIter = aProperties.keySet().iterator();
         String key = null;
         while (keyIter.hasNext())
         {
            key = (String) keyIter.next();
            setProperty(key, aProperties.getProperty(key));
         }
      }
   }

   /**
    * Returns the type of coordination this context has been activated for.
    * @return coordination type. 
    */
   public String getCoordinationType()
   {
      return getProperty(IAeCoordinating.WSCOORD_TYPE);
   }   
   
   /**
    * Returns the coordination identifier.
    * @return coordination id.
    */
   public String getIdentifier()
   {
      return getCoordinationId().getIdentifier();
   }

   /** 
    * @return the coordination identifier wrapper.
    */
   public IAeCoordinationId getCoordinationId()
   {
      return mCoordinationId;
   }
}
