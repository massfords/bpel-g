// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/IAePortType.java,v 1.3 2006/06/26 16:46:43 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def;

import javax.xml.namespace.QName;

/**
 * This interface represents a Role's PortType element.  This PortType
 * element references a WSDL PortType.
 */
public interface IAePortType
{
   /**
    * Get the name of this PortType.
    * @return QName
    */
   public QName getQName();

   /**
    * Set the name of this port type.
    * @param aQName
    */
   public void setQName(QName aQName);
}
