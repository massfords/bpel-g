//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeConnectionProxyFactory.java,v 1.1 2005/03/23 17:32:03 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * Generic factory for wrapping JDBC connections in their proxy instances.
 */
public class AeConnectionProxyFactory
{
   /**
    * Wrap the <code>Connection</code> in the <code>InvocationHandler</code>
    * instance.
    * @param aConn
    * @param aHandler
    */
   public static Connection getConnectionProxy( Connection aConn, InvocationHandler aHandler )
   {
      return (Connection) Proxy.newProxyInstance(
            aConn.getClass().getClassLoader(),
            new Class[] { Connection.class },
            aHandler );
   }
}
