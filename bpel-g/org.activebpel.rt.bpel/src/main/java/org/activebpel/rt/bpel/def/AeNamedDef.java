// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/AeNamedDef.java,v 1.8 2007/09/26 02:21:04 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import org.activebpel.rt.xml.def.IAeNamedDef;

/**
 * Definition base for all named definition elements
 */
public abstract class AeNamedDef extends AeBaseDef implements IAeNamedDef
{
   // persistent attributes of the definition
   private String mName = ""; //$NON-NLS-1$

   /**
    * Default constructor
    */
   public AeNamedDef()
   {
      super();
   }

   /**
    * Accessor method to obtain name of this object.
    * 
    * @return name of object
    * @see org.activebpel.rt.xml.def.IAeNamedDef#getName()
    */
   public String getName()
   {
      return mName;
   }

   /**
    * Mutator method to set name of this object.
    * 
    * @param aName of object, ignored if null, use empty string to clear
    */
   public void setName(String aName)
   {
      if(aName != null)
        mName = aName;
   }
}
