// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/IAeWSResourceProblemReporter.java,v 1.2 2008/02/15 17:40:16 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation;

import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Interface that is passed to WS Resource validators when they
 * are asked to validate a resource.
 */
public interface IAeWSResourceProblemReporter
{
   /**
    * Called by the validators when they find a problem.
    * 
    * @param aMessage
    * @param aDef
    */
   public void reportProblem(String aMessage, AeBaseXmlDef aDef);
}
