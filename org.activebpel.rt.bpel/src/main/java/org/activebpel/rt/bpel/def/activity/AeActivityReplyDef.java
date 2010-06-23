// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/AeActivityReplyDef.java,v 1.10 2007/12/08 12:02:57 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity;

import java.util.Collections;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AeActivityPartnerLinkBaseDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.IAeToPartsParentDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartsDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Definition for bpel reply activity.
 */
public class AeActivityReplyDef extends AeActivityPartnerLinkBaseDef implements IAeToPartsParentDef, IAeMessageDataProducerDef
{
   /** The variable attribute. */
   private String mVariable;
   /** The fault name attribute. */
   private QName mFaultName;
   /** The message exchange attribute. */
   private String mMessageExchange;
   /** The 'toParts' child def. */
   private AeToPartsDef mToPartsDef;
   /** The strategy hint for producing the message data for the reply */
   private String mMessageDataProducerStrategy;

   /**
    * Default constructor
    */
   public AeActivityReplyDef()
   {
      super();
   }

   /**
    * Accessor method to obtain the name of the variable associated with this activity.
    * 
    * @return name of the variable
    */
   public String getVariable()
   {
      return mVariable;
   }

   /**
    * Mutator method to set the name of the variable for this activity.
    * 
    * @param aVariable name of variable
    */
   public void setVariable(String aVariable)
   {
      mVariable = aVariable;
   }

   /**
    * Accessor method to obtain the name of the fault associated with this activity.
    * 
    * @return name of the fault
    */
   public QName getFaultName()
   {
      return mFaultName;
   }

   /**
    * Mutator method to set the name of the fault for this activity.
    * 
    * @param aFaultName name of fault
    */
   public void setFaultName(QName aFaultName)
   {
      mFaultName = aFaultName;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeActivityDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * @return Returns the messageExchange.
    */
   public String getMessageExchange()
   {
      return mMessageExchange;
   }

   /**
    * @param aMessageExchange The messageExchange to set.
    */
   public void setMessageExchange(String aMessageExchange)
   {
      mMessageExchange = aMessageExchange;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeFromPartsParentDef#getFromPartDefs()
    */
   public Iterator getToPartDefs()
   {
      if (getToPartsDef() == null)
         return Collections.EMPTY_LIST.iterator();
      else
         return getToPartsDef().getToPartDefs();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.IAeToPartsParentDef#getToPartsDef()
    */
   public AeToPartsDef getToPartsDef()
   {
      return mToPartsDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeToPartsParentDef#setToPartsDef(AeToPartsDef)
    */
   public void setToPartsDef(AeToPartsDef aDef)
   {
      mToPartsDef = aDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef#getMessageDataProducerVariable()
    */
   public AeVariableDef getMessageDataProducerVariable()
   {
      return AeDefUtil.getVariableByName(getVariable(), this);
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef#getMessageDataProducerStrategy()
    */
   public String getMessageDataProducerStrategy()
   {
      return mMessageDataProducerStrategy;
   }

   /**
    * @param aMessageDataProducerStrategy The messageDataProducerStrategy to set.
    */
   public void setMessageDataProducerStrategy(String aMessageDataProducerStrategy)
   {
      mMessageDataProducerStrategy = aMessageDataProducerStrategy;
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef#getProducerOperation()
    */
   public String getProducerOperation()
   {
      return getOperation();
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef#getProducerPortType()
    */
   public QName getProducerPortType()
   {
      return getPortType();
   }
}
