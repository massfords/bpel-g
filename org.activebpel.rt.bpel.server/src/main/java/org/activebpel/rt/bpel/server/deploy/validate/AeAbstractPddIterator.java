//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AeAbstractPddIterator.java,v 1.4 2006/07/18 20:05:32 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import java.util.Iterator;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.w3c.dom.Document;

/**
 * Base class for validators that need to iterator over all pdd files in the bpr. 
 */
public abstract class AeAbstractPddIterator implements IAePredeploymentValidator 
{

   /**
    * @see org.activebpel.rt.bpel.server.deploy.validate.IAePredeploymentValidator#validate(org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr, org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter)
    */
   public void validate(IAeBpr aBprFile, IAeBaseErrorReporter aReporter)
         throws AeException
   {
      for( Iterator iter = aBprFile.getPddResources().iterator(); iter.hasNext(); )
      {
         String pddName = (String) iter.next();
         Document pddDom = aBprFile.getResourceAsDocument( pddName );
         AePddInfo pddInfo = new AePddInfo( pddName, pddDom );
         
         validateImpl( pddInfo, aBprFile, aReporter );
      }
   }
   
   /**
    * Perform the actual validation logic.
    * @param aPddInfo
    * @param aBprFile
    * @param aReporter
    * @throws AeException
    */
   protected abstract void validateImpl( AePddInfo aPddInfo, 
         IAeBpr aBprFile, IAeBaseErrorReporter aReporter ) throws AeException;
   
}
