// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/IAeTerminationHandlerParentDef.java,v 1.1 2006/10/12 20:15:22 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def;

/**
 * BPEL constructs that parent the 'terminationHandler' construct should implement this interface.
 */
public interface IAeTerminationHandlerParentDef
{
   /**
    * Gets the 'terminationHandler' def.
    */
   public AeTerminationHandlerDef getTerminationHandlerDef();
   
   /**
    * Sets the 'terminationHandler' def.
    * 
    * @param aDef
    */
   public void setTerminationHandlerDef(AeTerminationHandlerDef aDef);
}
