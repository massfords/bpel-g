//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/copy/AeSrefToEprReplaceElementStrategy.java,v 1.1 2006/10/24 21:23:56 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.copy;

import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;

/**
 * Handles copying the source's first child element to the target (e.g. service-ref element to wsa:epr element).
 */
public class AeSrefToEprReplaceElementStrategy extends AeReplaceElementStrategy
{

   /** 
    * Overrides method to copy the first child element of the source element to the target.
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyStrategy#copy(org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation, java.lang.Object, java.lang.Object)
    */
   public void copy(IAeCopyOperation aCopyOperation, Object aFromData, Object aToData) throws AeBpelException
   {
      Element src = (Element)aFromData;
      // get first child element.
      // E.g. unwrap service-ref: <sref:service-ref><wsa:EndpointReference/></sref:service-ref> to get <wsa:EndpointReference/>
      src = AeXmlUtil.getFirstSubElement(src);
      super.copy(aCopyOperation, src, aToData);
   }

}
