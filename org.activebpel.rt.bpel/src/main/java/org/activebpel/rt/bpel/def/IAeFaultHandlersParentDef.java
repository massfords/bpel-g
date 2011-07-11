// $Header$
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
 * Bpel constructs that have a child called 'faultHandlers' should implement this interface.
 */
public interface IAeFaultHandlersParentDef
{
   /**
    * Gets the 'faultHandlers' def.
    */
   public AeFaultHandlersDef getFaultHandlersDef();
   
   /**
    * Sets the 'faultHandlers' def.
    * 
    * @param aDef
    */
   public void setFaultHandlersDef(AeFaultHandlersDef aDef);
}
