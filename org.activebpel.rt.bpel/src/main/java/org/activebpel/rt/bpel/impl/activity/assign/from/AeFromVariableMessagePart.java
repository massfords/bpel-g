//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/from/AeFromVariableMessagePart.java,v 1.6 2007/06/09 01:06:30 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.from; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.w3c.dom.Document;

/**
 * Handles selecting data from a variable part. The data will either be a xsd simple type,
 * element, or complex type.
 */
public class AeFromVariableMessagePart extends AeFromBase
{
   /** message part name */
   private String mPart;

   /** variable */
   private IAeVariable mVariable;

   /**
    * Ctor accepts the def object
    * 
    * @param aFromDef
    */
   public AeFromVariableMessagePart(AeFromDef aFromDef)
   {
      super(aFromDef);
      setPart(aFromDef.getPart());
   }
   
   /**
    * Ctor accepts variable name and part name
    * @param aVariableName
    * @param aPartName
    */
   public AeFromVariableMessagePart(String aVariableName, String aPartName)
   {
      setVariableName(aVariableName);
      setPart(aPartName);
   }

   /**
    * Ctor accepts variable and part name
    * @param aVariable
    * @param aPartName
    */
   public AeFromVariableMessagePart(IAeVariable aVariable, String aPartName)
   {
      this((String) null, aPartName);
      setVariable(aVariable);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeFrom#getFromData()
    */
   public Object getFromData() throws AeBusinessProcessException
   {
      Object data = getVariable().getMessageData().getData(getPart());
      if (data instanceof Document)
      {
         return ((Document)data).getDocumentElement();
      }
      return data;
   }

   /**
    * @return Returns the part.
    */
   public String getPart()
   {
      return mPart;
   }

   /**
    * @param aPart The part to set.
    */
   public void setPart(String aPart)
   {
      mPart = aPart;
   }

   /**
    * Overrides method to return variable set by {@link #setVariable(IAeVariable)} if defined.
    * 
    * @see org.activebpel.rt.bpel.impl.activity.assign.from.AeFromBase#getVariable()
    */
   protected IAeVariable getVariable()
   {
      return (mVariable != null) ? mVariable : super.getVariable();
   }

   /**
    * @param aVariable The variable to set.
    */
   public void setVariable(IAeVariable aVariable)
   {
      mVariable = aVariable;
   }
}
