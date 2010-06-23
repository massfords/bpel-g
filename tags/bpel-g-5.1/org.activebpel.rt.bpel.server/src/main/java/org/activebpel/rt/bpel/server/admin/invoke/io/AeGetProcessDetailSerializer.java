//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/invoke/io/AeGetProcessDetailSerializer.java,v 1.3 2008/02/17 21:38:45 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin.invoke.io;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Serializes a process instance detail into a <code>getProcessDetailOutput</code>
 * element.
 */
public class AeGetProcessDetailSerializer extends AeProcessInstanceDetailSerializerBase
{
   /**
    * Process instance detail.
    */
   private AeProcessInstanceDetail mDetail;

   /**
    * Constructs serializer with the process instance detail.
    * @param aDetail
    */
   public AeGetProcessDetailSerializer(AeProcessInstanceDetail aDetail)
   {
      mDetail = aDetail;
   }   
   
   /**
    * Returns the process instance detail.
    *
    */
   public AeProcessInstanceDetail getDetail()
   {
      return mDetail;
   }

   /**
    * Overrides method to return a <code>AeProcessInstanceDetail</code> serialized into a <code>getProcessDetailOutput</code> element.
    * @see org.activebpel.rt.xml.AeXMLSerializerBase#serialize(org.w3c.dom.Element)
    */
   public Element serialize(Element aParentElement) throws AeException
   {
      Document doc = createDocumentWithElement(ENGINE_ADMIN_SCHEMA_NS, "aeadmin", "getProcessDetailOutput"); //$NON-NLS-1$ //$NON-NLS-2$
      Element responseEle = createElementWithText(doc.getDocumentElement(), ENGINE_ADMIN_SCHEMA_NS, "aeadmin", "response", null); //$NON-NLS-1$ //$NON-NLS-2$
      serialize( responseEle, getDetail() );
      return doc.getDocumentElement();
   }
}
