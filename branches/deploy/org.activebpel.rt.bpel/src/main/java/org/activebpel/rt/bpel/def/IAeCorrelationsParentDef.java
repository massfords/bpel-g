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
 * Any activity that can have a 'correlations' child must implement this interface.
 */
public interface IAeCorrelationsParentDef
{
   /**
    * Gets the correlations def.
    */
   public AeCorrelationsDef getCorrelationsDef();
   
   /**
    * Sets the 'correlations' container.
    * 
    * @param aDef
    */
   public void setCorrelationsDef(AeCorrelationsDef aDef);
}
