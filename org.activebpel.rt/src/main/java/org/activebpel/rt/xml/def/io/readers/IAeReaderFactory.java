// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/readers/IAeReaderFactory.java,v 1.1 2007/09/26 02:17:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.def.io.readers;

import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.w3c.dom.Element;

/**
 * A factory interface for creating reader def visitors.
 */
public interface IAeReaderFactory
{
   /**
    * Creates a reader def visitor.
    * 
    * @param aParentDef
    * @param aNewDef
    * @param aElement
    */
   public IAeReportingDefReader createReportingDefReader(AeBaseXmlDef aParentDef, AeBaseXmlDef aNewDef, Element aElement);
}
