// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def;

import java.util.Iterator;

import org.activebpel.rt.bpel.def.activity.support.AeToPartsDef;

/**
 * Defs that can have a toParts child construct will implement this interface.
 */
public interface IAeToPartsParentDef
{
   /**
    * Adds a toParts def to the activity.
    * 
    * @param aDef
    */
   public void setToPartsDef(AeToPartsDef aDef);
   
   /**
    * Gets the toParts def from the activity.
    */
   public AeToPartsDef getToPartsDef();

   /**
    * Gets an iterator over the list of toPart defs.
    */
   public Iterator getToPartDefs();
}
