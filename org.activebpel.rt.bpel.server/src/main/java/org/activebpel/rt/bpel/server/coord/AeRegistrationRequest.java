//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/AeRegistrationRequest.java,v 1.1 2005/10/28 21:10:30 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord;

import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinationContext;
import org.activebpel.rt.bpel.coord.IAeRegistrationRequest;

/**
 * Simple implementation of the registration request.
 */
public class AeRegistrationRequest extends AeContextBase implements IAeRegistrationRequest
{

   /**
    * Coordination context.
    */
   private IAeCoordinationContext mCoordinationContext;
   
   /**
    * Default constructor.
    */
   public AeRegistrationRequest()
   {
      super();
   }

   /** 
    * Overrides method to 
    * @see org.activebpel.rt.bpel.coord.IAeRegistrationRequest#getCoordinationContext()
    */
   public IAeCoordinationContext getCoordinationContext()
   {
      return mCoordinationContext;
   }
   
   /**
    * Sets the coordination context.
    * @param aCoordinationContext
    */
   public void setCoordinationContext(IAeCoordinationContext aCoordinationContext)
   {
      mCoordinationContext = aCoordinationContext;
   }
   
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.coord.IAeRegistrationRequest#getProtocolIdentifier()
    */
   public String getProtocolIdentifier()
   {
      return getProperty(IAeCoordinating.WSCOORD_PROTOCOL);
   }

   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.coord.IAeRegistrationRequest#setProtocolIdentifier(java.lang.String)
    */
   public void setProtocolIdentifier(String aProtocolId)
   {
      setProperty(IAeCoordinating.WSCOORD_PROTOCOL, aProtocolId);
   }

}
