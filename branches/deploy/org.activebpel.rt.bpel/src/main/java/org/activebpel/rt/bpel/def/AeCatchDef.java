// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.util.AeUtil;

/**
 * Definition for bpel fault handler element.
 */
public class AeCatchDef extends AeSingleActivityParentBaseDef implements IAeSingleActivityContainerDef, IAeVariableParentDef, IAeFCTHandlerDef
{
   /** The 'faultName' attribute. */
   private QName mFaultName;
   /** The 'faultVariable' attribute. */
   private String mFaultVariable;
   /** The 'faultMessageType' attribute. */
   private QName mFaultMessageType;
   /** The 'faultElement' attribute. */
   private QName mFaultElement;
   /** The variable def for the fault (only applies to BPEL 2.0) */
   private AeVariableDef mFaultVariableDef;
   
   /**
    * Default constructor
    */
   public AeCatchDef()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.def.faults.IAeCatch#hasFaultVariable()
    */
   public boolean hasFaultVariable()
   {
      return AeUtil.notNullOrEmpty(getFaultVariable());
   }

   /**
    * Accessor method to obtain fault name of this object.
    * 
    * @return the fault name of the object
    */
   public QName getFaultName()
   {
      return mFaultName;
   }

   /**
    * Mutator method to set the fault name of this object.
    * 
    * @param aFaultName the fault name for the object
    */
   public void setFaultName(QName aFaultName)
   {
      mFaultName = aFaultName;
   }

   /**
    * Accessor method to obtain fault variable of this object.
    * 
    * @return the fault variable of the object
    */
   public String getFaultVariable()
   {
      return mFaultVariable;
   }

   /**
    * Mutator method to set the fault variable of this object.
    * 
    * @param aFaultVariable the fault variable for the object
    */
   public void setFaultVariable(String aFaultVariable)
   {
      mFaultVariable = aFaultVariable;
   }

   /**
    * @return Returns the faultElement.
    */
   public QName getFaultElementName()
   {
      return mFaultElement;
   }

   /**
    * @param aFaultElement The faultElement to set.
    */
   public void setFaultElementName(QName aFaultElement)
   {
      mFaultElement = aFaultElement;
   }

   /**
    * @return Returns the faultMessageType.
    */
   public QName getFaultMessageType()
   {
      return mFaultMessageType;
   }

   /**
    * @param aFaultMessageType The faultMessageType to set.
    */
   public void setFaultMessageType(QName aFaultMessageType)
   {
      mFaultMessageType = aFaultMessageType;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * Setter for the fault variable def
    * @param aVarDef
    */
   public void setFaultVariableDef(AeVariableDef aVarDef)
   {
      mFaultVariableDef = aVarDef;
      if (mFaultVariableDef != null)
      {
         mFaultVariableDef.setImplicit(true);
      }
   }
   
   /**
    * Getter for the fault variable def
    */
   public AeVariableDef getFaultVariableDef()
   {
      return mFaultVariableDef; 
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeVariableParentDef#getVariableDef(java.lang.String)
    */
   public AeVariableDef getVariableDef(String aVariableName)
   {
      if (getFaultVariableDef() != null && getFaultVariableDef().getName().equals(aVariableName))
      {
         return getFaultVariableDef();
      }
      return null;
   }
}
