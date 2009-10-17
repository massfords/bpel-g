//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/invoke/io/AeEngineAdminMessageDeserializerBase.java,v 1.2 2008/02/02 19:23:26 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin.invoke.io;

import java.util.Map;

import org.activebpel.rt.xml.AeXMLDeserializerBase;
import org.w3c.dom.Document;

/**
 * Base class used for deserializing wsdl input messages.
 */
public abstract class AeEngineAdminMessageDeserializerBase extends AeXMLDeserializerBase implements IAeEngineAdminMessageIOConstants
{

   /**
    * Ctor.
    * @param aDocument
    */
   protected AeEngineAdminMessageDeserializerBase(Document aDocument)
   {
      setElement( aDocument.getDocumentElement() );
   }
   
   /** 
    * @see org.activebpel.rt.xml.AeXMLDeserializerBase#initNamespaceMap(java.util.Map)
    */
   protected void initNamespaceMap(Map aMap)
   {
      aMap.put("aeadminw", ENGINE_ADMIN_WSDL_NS); //$NON-NLS-1$
      aMap.put("aeadmint", ENGINE_ADMIN_SCHEMA_NS); //$NON-NLS-1$
      aMap.put("internw", ENGINE_ADMININTERNAL_WSDL_NS); //$NON-NLS-1$
      aMap.put("internt", ENGINE_ADMININTERNAL_SCHEMA_NS); //$NON-NLS-1$
   }    
}
