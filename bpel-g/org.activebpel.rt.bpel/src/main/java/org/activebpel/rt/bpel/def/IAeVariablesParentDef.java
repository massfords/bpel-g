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
 * Bpel constructs that can have a 'variables' child must implement this interface.
 */
public interface IAeVariablesParentDef extends IAeVariableParentDef
{
   /**
    * Gets the variables def.
    */
   public AeVariablesDef getVariablesDef();
   
   /**
    * Sets the variables def.
    * 
    * @param aDef
    */
   public void setVariablesDef(AeVariablesDef aDef);
}
