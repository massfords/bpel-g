//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/IAeMessageVariableWrapper.java,v 1.2.16.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign;

import org.activebpel.rt.bpel.IAeVariable;

/**
 * Wrapper interface for a message variable returned from a &lt;from&gt; or targeted by a &lt;to&gt;
 */
public interface IAeMessageVariableWrapper {
    /**
     * Getter for the variable
     */
    public IAeVariable getVariable();
}
 