//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/IAeMessageValidationAdapter.java,v 1.1 2007/12/07 18:50:49 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import org.activebpel.rt.bpel.impl.IAeMessageValidator;

/**
 * Parent adapter for an extension activity that validates messages
 */
public interface IAeMessageValidationAdapter extends IAeImplAdapter
{
   /**
    * Sets message validator
    * @param aMessageValidator
    */
   public void setMessageValidator(IAeMessageValidator aMessageValidator);
   
   /**
    * Returns message validator
    */
   public IAeMessageValidator getMessageValidator();
}
