// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/IAeContextWSDLProvider.java,v 1.1 2008/01/11 19:27:47 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl;

import java.util.Iterator;


/**
 *  Provides access to all WSDL available to a 
 *  deployment context.
 */
public interface IAeContextWSDLProvider extends IAeWSDLProvider
{
   /**
    * Returns an iterator over the WSDLs known to this provider.
    * 
    * @return Iterator
    */
   public Iterator getWSDLIterator();
}
