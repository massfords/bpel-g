// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/readers/IAeReportingDefReader.java,v 1.1 2007/09/26 02:17:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.def.io.readers;

import java.util.List;
import java.util.Set;

import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.w3c.dom.Element;

/**
 * A reader def visitor is used by the dispatch reader to actually read the attributes of a
 * def from the XML Element.
 */
public interface IAeReportingDefReader
{
   /**
    * Returns a set of Attr objects that have been consumed during the reading of the
    * def.
    */
   public Set getConsumedAttributes();

   /**
    * Returns true if there are errors encountered during the parse. These errors are
    * significant enough to not add definition object to the process def.
    * 
    * One example of an error like this would be encountering multiple child
    * activities when only one is allowed.
    */
   public boolean hasErrors();
   
   /**
    * Getter for the errors list.
    */
   public List getErrors();
   
   /**
    * Reads attributes from the element and sets them on the 
    * def. 
    */
   public void read(AeBaseXmlDef aDef, Element aElement);
}
