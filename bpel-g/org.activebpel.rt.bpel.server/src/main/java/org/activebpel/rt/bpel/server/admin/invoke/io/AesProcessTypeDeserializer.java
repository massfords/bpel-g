//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/invoke/io/AesProcessTypeDeserializer.java,v 1.2 2008/02/02 19:23:26 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin.invoke.io;

import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Document;

/**
 * Deserializes a AesProcessType message.
 */
public class AesProcessTypeDeserializer extends AeEngineAdminMessageDeserializerBase
{
   /**
    * Ctor.
    * @param aDocument
    */
   public AesProcessTypeDeserializer(Document aDocument)
   {
      super(aDocument);
   }

   /**
    * Returns process id based on the value of the pid element.
    * @return process id or -1 if not available.
    */
   public long getProcessId()
   {
      String pid = getText(getElement(), "aeadmint:pid"); //$NON-NLS-1$
      return AeUtil.parseLong(pid, -1L);
   }
}
