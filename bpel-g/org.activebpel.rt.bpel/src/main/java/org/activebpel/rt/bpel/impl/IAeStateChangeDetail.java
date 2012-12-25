// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeStateChangeDetail.java,v 1.3 2004/07/08 13:09:58 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

/**
 * Contains additional information regarding an object's state change.
 */
public interface IAeStateChangeDetail
{
   /**
    * Returns non-null if the object's change was due to a fault
    */
   public String getFaultName();
   
   /**
    * Returns any additional info related to the state change.
    */
   public String getAdditionalInfo();
}
