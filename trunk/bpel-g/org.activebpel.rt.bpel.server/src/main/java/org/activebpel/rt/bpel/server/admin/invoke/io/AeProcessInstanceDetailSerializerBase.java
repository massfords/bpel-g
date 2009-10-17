//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/invoke/io/AeProcessInstanceDetailSerializerBase.java,v 1.1 2007/12/19 21:12:29 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin.invoke.io;

import java.util.Date;

import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;


/**
 * Base class responsible for serializing a <code>AeProcessInstanceDetail</code> java object
 * into an element of complex type <code>AesProcessInstanceDetail</code>.
 */
public abstract class AeProcessInstanceDetailSerializerBase extends AeEngineAdminMessageSerializerBase
{

   /**
    * Serializes the given process instance detail and appends the
    * @param aParentElement container element which is of type <code>AeProcessInstanceDetail</code>
    * @param aDetail process details.
    */
   protected void serialize(Element aParentElement, AeProcessInstanceDetail aDetail)
   {
      AeXmlUtil.addElementNSDate(aParentElement, ENGINE_ADMIN_SCHEMA_NS, "aeadmin", "ended", aDetail.getEnded(), true); //$NON-NLS-1$ //$NON-NLS-2$
      AeXmlUtil.addElementNSQName(aParentElement, ENGINE_ADMIN_SCHEMA_NS, "aeadmin", "name", aDetail.getName(), true); //$NON-NLS-1$ //$NON-NLS-2$
      createElementWithText(aParentElement, ENGINE_ADMIN_SCHEMA_NS, "aeadmin", "processId", String.valueOf(aDetail.getProcessId())); //$NON-NLS-1$ //$NON-NLS-2$
      AeXmlUtil.addElementNSDate(aParentElement, ENGINE_ADMIN_SCHEMA_NS, "aeadmin", "started", aDetail.getStarted(), true); //$NON-NLS-1$ //$NON-NLS-2$
      createElementWithText(aParentElement, ENGINE_ADMIN_SCHEMA_NS, "aeadmin", "state", String.valueOf(aDetail.getState())); //$NON-NLS-1$ //$NON-NLS-2$
      createElementWithText(aParentElement, ENGINE_ADMIN_SCHEMA_NS, "aeadmin", "stateReason", String.valueOf(aDetail.getStateReason())); //$NON-NLS-1$ //$NON-NLS-2$
   }
   
   /**
    * Serializes the given date and time.
    * @param aParentElement
    * @param aElemName
    * @param aDate
    */
   protected void serializeNillableDate(Element aParentElement, String aElemName, Date aDate)
   {
      
   }
}
