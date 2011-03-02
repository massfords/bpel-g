//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/AeCreateContextResponse.java,v 1.1 2005/10/28 21:10:30 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord;

import org.activebpel.rt.bpel.coord.IAeCoordinationContext;
import org.activebpel.rt.bpel.coord.IAeCreateContextResponse;

/**
 * Basic implementation of a create context response.
 */
public class AeCreateContextResponse extends AeContextBase implements IAeCreateContextResponse
{

   /**
    * Coordination context.
    */
   private IAeCoordinationContext mContext = null;
   
   /**
    * Creates a response given the context. 
    */
   public AeCreateContextResponse(IAeCoordinationContext aContext)
   {
      super();
      mContext = aContext;
   }

   /**
    * @return coordination context.
    */
   public IAeCoordinationContext getContext()
   {
      return mContext;
   }
}
