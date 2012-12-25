//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/from/AeFromVariableElement.java,v 1.6 2007/05/09 20:36:36 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.from; 

import org.activebpel.rt.attachment.IAeAttachmentContainer;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;

/**
 * Handles selecting data from an element variable 
 */
public class AeFromVariableElement extends AeFromBase
{
   /** variable */
   private IAeVariable mVariable;

   /**
    * Ctor takes the def
    * 
    * @param aDef
    */
   public AeFromVariableElement(AeFromDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Ctor accepts variable name
    * @param aVariableName
    */
   public AeFromVariableElement(String aVariableName)
   {
      setVariableName(aVariableName);
   }
   
   /**
    * Ctor accepts variable
    * @param aVariable
    */
   public AeFromVariableElement(IAeVariable aVariable)
   {
      setVariable(aVariable);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeFrom#getFromData()
    */
   public Object getFromData() throws AeBusinessProcessException
   {
      return getVariable().getElementData();
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
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.from.AeFromBase#getAttachmentsSource()
    */
   public IAeAttachmentContainer getAttachmentsSource()
   {
      return getVariable().getAttachmentData();
   }
}
 