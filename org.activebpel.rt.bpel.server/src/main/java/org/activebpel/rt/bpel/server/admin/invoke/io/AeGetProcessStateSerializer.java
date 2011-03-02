//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/invoke/io/AeGetProcessStateSerializer.java,v 1.2 2008/02/02 19:23:26 PJayanetti Exp $
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
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *  Serializes process state information into a getProcessStateResponse message.
 */
public class AeGetProcessStateSerializer extends AeEngineAdminMessageSerializerBase
{
   /**
    * Process state document.
    */
   private Document mProcessStateDocument;

   /**
    * Ctor.
    */
   public AeGetProcessStateSerializer(Document aProcessStateDocument)
   {
      mProcessStateDocument = aProcessStateDocument;
   }

   /**
    * @return Process state document.
    */
   protected Document getProcessStateDocument()
   {
      return mProcessStateDocument;
   }

   /**
    * Overrides method to serialize process status into a getProcessStateOutput element result.
    * @see org.activebpel.rt.xml.AeXMLSerializerBase#serialize(org.w3c.dom.Element)
    */
   public Element serialize(Element aParentElement) throws AeException
   {
      Document doc = createDocumentWithElement(ENGINE_ADMIN_SCHEMA_NS, "aeadmint", "getProcessStateOutput"); //$NON-NLS-1$ //$NON-NLS-2$
      String processStateString = AeXMLParserBase.documentToString( getProcessStateDocument(), true);
      createElementWithText(doc.getDocumentElement(), ENGINE_ADMIN_SCHEMA_NS, "aeadmint", "response", processStateString); //$NON-NLS-1$ //$NON-NLS-2$
      return doc.getDocumentElement();
   }

}
