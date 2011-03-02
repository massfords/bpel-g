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

import org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef;

/**
 * Defs that can have a fromParts child construct will implement this interface.
 */
public interface IAeFromPartsParentDef
{
   /**
    * Sets the fromParts on the activity.
    * 
    * @param aDef
    */
   public void setFromPartsDef(AeFromPartsDef aDef);
   
   /**
    * Gets the fromParts def from the activity.
    */
   public AeFromPartsDef getFromPartsDef();

   /**
    * Gets an iterator over the list of fromPart defs.
    */
   public Iterator getFromPartDefs();
}
