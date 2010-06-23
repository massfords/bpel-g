// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AeBpelLocationValidator.java,v 1.7 2005/06/13 17:54:06 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;

/**
 * Validates that the value of the locatio attribute on the pdd process element
 * points to a bpel file contained in the bpr. 
 */
/**
 *
 */
public class AeBpelLocationValidator extends AeAbstractPddIterator
{
   /** Missing BPEL error message template */
   private static final String MISSING_BPEL = AeMessages.getString("AeBpelLocationValidator.0"); //$NON-NLS-1$

   /**
    * @see org.activebpel.rt.bpel.server.deploy.validate.AeAbstractPddIterator#validateImpl(org.activebpel.rt.bpel.server.deploy.validate.AePddInfo, org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr, org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter)
    */
   protected void validateImpl(AePddInfo aPddInfo, IAeBpr aBprFile, IAeBaseErrorReporter aReporter) throws AeException
   {
      if( !aBprFile.exists( aPddInfo.getBpelLocation() ) )
      {
         String[] args = {aPddInfo.getName(), aBprFile.getBprFileName(), aPddInfo.getBpelLocation()};
         aReporter.addError(MISSING_BPEL, args, null);
      }
   }
}
