// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/AeActivityThrowDef.java,v 1.9 2006/06/26 16:50:31 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Definition for bpel throw activity.
 */
public class AeActivityThrowDef extends AeActivityDef
{
   /** fault name for the throw */
   private QName mFaultName;
   /** name of variable for throw */
   private String mFaultVariable;

   // standard element names of the activity definition
   public static final String TAG_THROW = "throw"; //$NON-NLS-1$

   /**
    * Default constructor
    */
   public AeActivityThrowDef()
   {
   }

   /**
    * Accessor method to obtain the name of the fault for the throw activity.
    * 
    * @return name of fault
    */
   public QName getFaultName()
   {
      return mFaultName;
   }

   /**
    * Mutator method to set the fault name.
    * 
    * @param aFaultName name of fault to be set
    */
   public void setFaultName(QName aFaultName)
   {
      mFaultName = aFaultName;
   }

   /**
    * Accessor method to obtain the name of the fault variable for the throw activity.
    * 
    * @return name of fault variable
    */
   public String getFaultVariable()
   {
      return mFaultVariable;
   }

   /**
    * Mutator method to set the fault variable name.
    * 
    * @param aFaultVariable name of fault variable to be set
    */ 
   public void setFaultVariable(String aFaultVariable)
   {
      mFaultVariable = aFaultVariable;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeActivityDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
