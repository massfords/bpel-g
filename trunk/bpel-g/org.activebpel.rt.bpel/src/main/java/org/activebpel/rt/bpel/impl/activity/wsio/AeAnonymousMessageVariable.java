// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/AeAnonymousMessageVariable.java,v 1.1 2006/08/03 23:33:04 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.wsio;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.impl.AeVariable;
import org.activebpel.rt.message.AeMessagePartsMap;

/**
 * Implements an anonymous message variable.
 */
public class AeAnonymousMessageVariable extends AeVariable
{
   /**
    * Constructs an anonymous message variable with the given message parts map.
    *
    * @param aMessagePartsMap
    */
   public AeAnonymousMessageVariable(AeMessagePartsMap aMessagePartsMap) throws AeBusinessProcessException
   {
      super(new AeAnonymousVariableContainer(), new AeVariableDef(aMessagePartsMap));

      getParent().addVariable(this);
   }

   /**
    * Overrides method to do nothing for anonymous variable.
    *
    * @see org.activebpel.rt.bpel.IAeVariable#incrementVersionNumber()
    */
   public void incrementVersionNumber()
   {
      // Do nothing for anonymous variable.
   }
}
