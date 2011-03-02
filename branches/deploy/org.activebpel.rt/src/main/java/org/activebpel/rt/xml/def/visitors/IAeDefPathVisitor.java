// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/visitors/IAeDefPathVisitor.java,v 1.1 2007/10/01 16:59:12 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.def.visitors;

import java.util.Set;



/**
 * Defines an interface for a visitor that assigns location paths and ids to
 * all of the def objects in the model.
 */
public interface IAeDefPathVisitor extends IAeBaseXmlDefVisitor
{
   /**
    * Returns set of generated location paths.
    */
   public Set getLocationPaths();
   
   /**
    * Returns location id corresponding to a location path.
    */
   public int getLocationId(String aLocationPath);
}
