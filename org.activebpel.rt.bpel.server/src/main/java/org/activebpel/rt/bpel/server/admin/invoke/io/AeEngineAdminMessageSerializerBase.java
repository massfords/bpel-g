//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/invoke/io/AeEngineAdminMessageSerializerBase.java,v 1.2 2008/02/02 19:23:26 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin.invoke.io;

import org.activebpel.rt.xml.AeXMLSerializerBase;

/**
 * Base class used for serializing content into wsdl output messages.
 */
public abstract class AeEngineAdminMessageSerializerBase extends AeXMLSerializerBase implements IAeEngineAdminMessageIOConstants
{
   /**
    * Ctor.
    */
   protected AeEngineAdminMessageSerializerBase()
   {
   }

}
